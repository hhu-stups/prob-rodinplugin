/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;

import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.attribute.AbstractAttribute;
import de.bmotionstudio.gef.editor.attribute.BAttributeCoordinates;
import de.bmotionstudio.gef.editor.attribute.BAttributeCustom;
import de.bmotionstudio.gef.editor.attribute.BAttributeHeight;
import de.bmotionstudio.gef.editor.attribute.BAttributeID;
import de.bmotionstudio.gef.editor.attribute.BAttributeMisc;
import de.bmotionstudio.gef.editor.attribute.BAttributeSize;
import de.bmotionstudio.gef.editor.attribute.BAttributeVisible;
import de.bmotionstudio.gef.editor.attribute.BAttributeWidth;
import de.bmotionstudio.gef.editor.attribute.BAttributeX;
import de.bmotionstudio.gef.editor.attribute.BAttributeY;
import de.bmotionstudio.gef.editor.command.CopyPasteHelper;
import de.bmotionstudio.gef.editor.internal.BControlPropertySource;
import de.bmotionstudio.gef.editor.observer.Observer;
import de.bmotionstudio.gef.editor.scheduler.SchedulerEvent;

/**
 * 
 * A Control is a graphical representation of some aspects of the model.
 * Typically we use labels, images or buttons to represent informations. For
 * instance, if we model a system that has a temperature and a threshold
 * temperature that triggers a cool down, we might simply use two labels
 * displaying both values, or maybe we can incorporate both information into a
 * gauge display. It is also possible to define new controls for domain specific
 * visualizations.
 * 
 * @author Lukas Ladenberger
 * 
 */
public abstract class BControl implements IAdaptable, Cloneable {

	protected String type;

	private BControlList children;

	private Map<String, Observer> observers;

	private Map<String, SchedulerEvent> events;

	private Map<String, AbstractAttribute> attributes;

	private BMotionGuide verticalGuide, horizontalGuide;

	// List of outgoing Connections
	private List<BConnection> sourceConnections;
	// List of incoming Connections
	private List<BConnection> targetConnections;

	private transient Rectangle layout = null;

	private transient Point location = null;

	private transient BControl parent;

	private transient Visualization visualization;

	private transient PropertyChangeSupport listeners;
	
	private transient boolean newControl;

	public BControl(Visualization visualization) {
		this.visualization = visualization;
		this.children = new BControlList();
		this.observers = new HashMap<String, Observer>();
		this.events = new HashMap<String, SchedulerEvent>();
		this.attributes = new HashMap<String, AbstractAttribute>();
		this.listeners = new PropertyChangeSupport(this);
		this.sourceConnections = new ArrayList<BConnection>();
		this.targetConnections = new ArrayList<BConnection>();
		this.newControl = true;
		init();
	}

	protected Object readResolve() {
		// Populate parent
		for (BControl child : getChildrenArray())
			child.setParent(this);
		this.newControl = false;
		return this;
	}

	/**
	 * Remove an incoming or outgoing connection from this shape.
	 * 
	 * @param conn
	 *            a non-null connection instance
	 * @throws IllegalArgumentException
	 *             if the parameter is null
	 */
	public void removeConnection(BConnection conn) {
		if (conn == null) {
			throw new IllegalArgumentException();
		}
		if (conn.getSource() == this) {
			getSourceConnections().remove(conn);
			getListeners().firePropertyChange(
					BControlPropertyConstants.SOURCE_CONNECTIONS, null, conn);
		} else if (conn.getTarget() == this) {
			getTargetConnections().remove(conn);
			getListeners().firePropertyChange(
					BControlPropertyConstants.TARGET_CONNECTIONS, null, conn);
		}
	}

	/**
	 * Add an incoming or outgoing connection to this shape.
	 * 
	 * @param conn
	 *            a non-null connection instance
	 * @throws IllegalArgumentException
	 *             if the connection is null or has not distinct endpoints
	 */
	public void addConnection(BConnection conn) {
		if (conn == null || conn.getSource() == conn.getTarget()) {
			throw new IllegalArgumentException();
		}
		if (conn.getSource() == this) {
			getSourceConnections().add(conn);
			getListeners().firePropertyChange(
					BControlPropertyConstants.SOURCE_CONNECTIONS, null, conn);
		} else if (conn.getTarget() == this) {
			getTargetConnections().add(conn);
			getListeners().firePropertyChange(
					BControlPropertyConstants.TARGET_CONNECTIONS, null, conn);
		}
	}

	protected void init() {
		// Init standard control attributes
		initStandardAttributes();
		// Init custom control attributes
		initAttributes();

	}

	private List<String> getSupportedObserverList() {

		List<String> supportedObserver = new ArrayList<String>();

		IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint(
						"de.bmotionstudio.gef.editor.includeObserver");

		for (IExtension extension : extensionPoint.getExtensions()) {
			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if ("include".equals(configurationElement.getName())) {

					String langID = configurationElement
							.getAttribute("language");

					if (langID != null
							&& langID.equals(getVisualization()
									.getLanguage())) {

						for (IConfigurationElement configC : configurationElement
								.getChildren("control")) {

							String cID = configC.getAttribute("id");

							if (getType().equals(cID)) {

								for (IConfigurationElement configO : configC
										.getChildren("observer")) {

									supportedObserver.add(configO
											.getAttribute("id"));

								}

							}

						}

					}

				}

			}
		}

		return supportedObserver;

	}

	private void initStandardAttributes() {

		// Init unique ID
		String ID;
		if (this instanceof Visualization)
			ID = "visualization";
		else if (visualization == null)
			ID = UUID.randomUUID().toString();
		else
			ID = (visualization.getMaxIDString(type));

		BAttributeID aID = new BAttributeID(ID);
		aID.setGroup(AbstractAttribute.ROOT);
		initAttribute(aID);

		BAttributeMisc aMisc = new BAttributeMisc("");
		aMisc.setGroup(AbstractAttribute.ROOT);
		initAttribute(aMisc);
		
		// Init location and size attributes
		BAttributeCoordinates aCoordinates = new BAttributeCoordinates(null);
		aCoordinates.setGroup(AbstractAttribute.ROOT);
		initAttribute(aCoordinates);
		
		BAttributeX aX = new BAttributeX(100);
		aX.setGroup(aCoordinates);
		initAttribute(aX);
		
		BAttributeY aY = new BAttributeY(100);
		aY.setGroup(aCoordinates);
		initAttribute(aY);
		
		BAttributeSize aSize = new BAttributeSize(null);
		aSize.setGroup(AbstractAttribute.ROOT);
		initAttribute(aSize);
		
		BAttributeWidth aWidth = new BAttributeWidth(100);
		aWidth.setGroup(aSize);
		initAttribute(aWidth);

		BAttributeHeight aHeight = new BAttributeHeight(100);
		aHeight.setGroup(aSize);
		initAttribute(aHeight);

		// Init visible and this attribute
		BAttributeVisible aVisible = new BAttributeVisible(true);
		aVisible.setGroup(AbstractAttribute.ROOT);
		initAttribute(aVisible);

		BAttributeCustom aCustom = new BAttributeCustom("");
		aCustom.setGroup(AbstractAttribute.ROOT);
		initAttribute(aCustom);

	}

	protected abstract void initAttributes();

	public String getID() {
		return getAttributeValue(AttributeConstants.ATTRIBUTE_ID).toString();
	}

	public AbstractAttribute getAttribute(String attributeID) {
		return getAttributes().get(attributeID);
	}

	public Object getAttributeValue(String attributeID) {

		AbstractAttribute atr = attributes.get(attributeID);

		if (atr != null) {
			return atr.getValue();
		} else {
			// TODO: handle error/exception (no such attribute)
			return null;
		}

	}

	public boolean setAttributeValue(String attributeID, Object value) {
		return setAttributeValue(attributeID, value, true, true);
	}

	public boolean setAttributeValue(String attributeID, Object value,
			Boolean firePropertyChange) {
		return setAttributeValue(attributeID, value, firePropertyChange, true);
	}

	public boolean setAttributeValue(String attributeID, Object value,
			Boolean firePropertyChange, Boolean setInitVal) {

		AbstractAttribute atr = attributes.get(attributeID);

		if (atr == null) {
			return false;
			// TODO: Throw some error!?!
		}

		atr.setControl(this);

		if ((atr.getValue() != null && atr.getValue().equals(value))
				|| !atr.isEditable())
			return true;

		atr.setValue(value, firePropertyChange, setInitVal);

		return true;

	}

	public void restoreDefaultValue(String attributeID) {
		AbstractAttribute atr = attributes.get(attributeID);
		if (atr != null) {
			atr.restoreValue();
			Object oldVal = atr.getValue();
			Object initValue = atr.getInitValue();
			getListeners().firePropertyChange(attributeID, oldVal, initValue);
		}
	}

	public boolean hasAttribute(String attributeID) {
		return attributes.containsKey(attributeID);
	}

	public void setLayout(Rectangle newLayout) {
		Rectangle oldLayout = getLayout();
		layout = newLayout;
		setAttributeValue(AttributeConstants.ATTRIBUTE_X, newLayout.x, false);
		setAttributeValue(AttributeConstants.ATTRIBUTE_Y, newLayout.y, false);
		setAttributeValue(AttributeConstants.ATTRIBUTE_WIDTH, newLayout.width,
				false);
		setAttributeValue(AttributeConstants.ATTRIBUTE_HEIGHT,
				newLayout.height, false);
		getListeners()
				.firePropertyChange(BControlPropertyConstants.PROPERTY_LAYOUT,
						oldLayout, newLayout);
	}

	public Rectangle getLayout() {

		String widthStr = getAttributeValue(AttributeConstants.ATTRIBUTE_WIDTH)
				.toString();
		String heightStr = getAttributeValue(
				AttributeConstants.ATTRIBUTE_HEIGHT).toString();
		String xStr = getAttributeValue(AttributeConstants.ATTRIBUTE_X)
				.toString();
		String yStr = getAttributeValue(AttributeConstants.ATTRIBUTE_Y)
				.toString();

		// TODO: check if strings are a correct integers

		try {

			int width = Integer.valueOf(widthStr);
			int height = Integer.valueOf(heightStr);
			int x = Integer.valueOf(xStr);
			int y = Integer.valueOf(yStr);

			if (layout == null) {
				layout = new Rectangle(x, y, width, height);
			} else {
				layout.x = x;
				layout.y = y;
				layout.width = width;
				layout.height = height;
			}

		} catch (NumberFormatException e) {
			// We ignore number format exceptions, however we should return an
			// error message here
			// TODO: return error message
		}

		return layout;

	}

	public void setLocation(Point newLocation) {
		Point oldLocation = getLocation();
		location = newLocation;
		setAttributeValue(AttributeConstants.ATTRIBUTE_X, newLocation.x, false);
		setAttributeValue(AttributeConstants.ATTRIBUTE_Y, newLocation.y, false);
		getListeners().firePropertyChange(
				BControlPropertyConstants.PROPERTY_LOCATION, oldLocation,
				newLocation);
	}

	public Point getLocation() {
		int x = Integer.valueOf(getAttributeValue(
				AttributeConstants.ATTRIBUTE_X).toString());
		int y = Integer.valueOf(getAttributeValue(
				AttributeConstants.ATTRIBUTE_Y).toString());
		if (location == null) {
			location = new Point(x, y);
		} else {
			location.x = x;
			location.y = y;
		}
		return location;
	}

	public Dimension getDimension() {
		int width = Integer.valueOf(getAttributeValue(
				AttributeConstants.ATTRIBUTE_WIDTH).toString());
		int height = Integer.valueOf(getAttributeValue(
				AttributeConstants.ATTRIBUTE_HEIGHT).toString());
		return new Dimension(width, height);
	}

	public void addChild(BControl child) {
		addChild(child, -1);
	}

	public void addChild(BControl child, int index) {
		child.setParent(this);
		if (index >= 0) {
			children.add(index, child);
		} else {
			children.add(child);
		}
		getListeners().firePropertyChange(
				BControlPropertyConstants.PROPERTY_ADD_CHILD, index, child);
	}

	public void removeAllChildren() {
		getChildrenArray().clear();
		getListeners().firePropertyChange(
				BControlPropertyConstants.PROPERTY_REMOVE_CHILD, null, null);
	}

	public boolean removeChild(int index) {
		BControl control = children.get(index);
		return removeChild(control);
	}

	public boolean removeChild(BControl child) {
		boolean b = children.remove(child);
		if (b)
			getListeners().firePropertyChange(
					BControlPropertyConstants.PROPERTY_REMOVE_CHILD, child,
					null);
		return b;
	}

	public List<BControl> getChildrenArray() {
		if (children == null)
			children = new BControlList();
		return children;
	}

	public void setChildrenArray(BControlList children) {
		this.children = children;
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	public BControl getChild(String ID) {
		for (BControl bcontrol : children) {
			String bcontrolID = bcontrol.getAttributeValue(
					AttributeConstants.ATTRIBUTE_ID).toString();
			if (bcontrolID != null) {
				if (bcontrolID.equals(ID))
					return bcontrol;
			}
		}
		return null;
	}

	public Map<String, Observer> getObservers() {
		if (observers == null)
			observers = new HashMap<String, Observer>();
		return observers;
	}

	public Observer getObserver(String observerID) {
		return this.observers.get(observerID);
	}

	public Boolean hasObserver(String ID) {
		if (getObservers().containsKey(ID))
			return true;
		return false;
	}

	public void addObserver(Observer observer) {
		observers.put(observer.getID(), (Observer) observer);
		getListeners()
				.firePropertyChange(
						BControlPropertyConstants.PROPERTY_ADD_OBSERVER,
						observer, null);
	}

	public void removeObserver(Observer observer) {
		removeObserver(observer.getID());
	}

	public void removeObserver(String observerID) {
		Observer o = observers.remove(observerID);
		if (o != null) {
			o.beforeDelete(this);
			getListeners()
					.firePropertyChange(
							BControlPropertyConstants.PROPERTY_REMOVE_OBSERVER,
							o, null);
		}
	}

	public Map<String, SchedulerEvent> getEvents() {
		if (events == null)
			events = new HashMap<String, SchedulerEvent>();
		return events;
	}

	public SchedulerEvent getEvent(String ID) {
		return events.get(ID);
	}

	public Boolean hasEvent(String ID) {
		if (getEvents().containsKey(ID))
			return true;
		return false;
	}

	public void addEvent(String eventID, SchedulerEvent schedulerEvent) {
		events.put(eventID, schedulerEvent);
		getListeners().firePropertyChange(
				BControlPropertyConstants.PROPERTY_ADD_EVENT, schedulerEvent,
				null);
	}

	public void removeEvent(String eventID) {
		SchedulerEvent e = events.remove(eventID);
		if (e != null) {
			e.beforeDelete(this);
			getListeners().firePropertyChange(
					BControlPropertyConstants.PROPERTY_REMOVE_EVENT, e, null);
		}
	}

	public Map<String, AbstractAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, AbstractAttribute> attributes) {
		this.attributes = attributes;
	}

	public void setParent(BControl parent) {
		this.parent = parent;
	}

	public BControl getParent() {
		return this.parent;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getListeners().addPropertyChangeListener(listener);
	}

	public PropertyChangeSupport getListeners() {
		if (listeners == null)
			listeners = new PropertyChangeSupport(this);
		return listeners;
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		getListeners().removePropertyChangeListener(listener);
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySource.class) {
			return new BControlPropertySource(this);
		}
		return null;
	}

	public boolean contains(BControl child) {
		return children.contains(child);
	}

	public String getDefaultValue(String attributeID) {

		IConfigurationElement configurationElement = BMotionEditorPlugin
				.getControlExtension(getType());

		if (configurationElement != null) {

			for (final IConfigurationElement configBAttributes : configurationElement
					.getChildren("attributes")) {

				for (final IConfigurationElement configBAttribute : configBAttributes
						.getChildren("attribute-string")) {

					String aID = configBAttribute.getAttribute("id");

					if (aID.equals(attributeID)) {
						String attributeDefaultValue = configBAttribute
								.getAttribute("default-value");

						return attributeDefaultValue;
					}

				}

			}

		}

		return null;

	}

	public Boolean isAttributeEditable(String attributeID) {

		IConfigurationElement configurationElement = BMotionEditorPlugin
				.getControlExtension(getType());

		if (configurationElement != null) {

			for (final IConfigurationElement configBAttributes : configurationElement
					.getChildren("attributes")) {

				for (final IConfigurationElement configBAttribute : configBAttributes
						.getChildren("attribute-string")) {

					String aID = configBAttribute.getAttribute("id");

					if (aID.equals(attributeID)) {
						String val = configBAttribute.getAttribute("editable");
						return Boolean.valueOf(val);
					}

				}

			}

		}

		return false;

	}

	public Visualization getVisualization() {
		return visualization;
	}

	public void setVisualization(Visualization visualization) {
		this.visualization = visualization;
	}

	@Override
	public BControl clone() throws CloneNotSupportedException {

		BControl clonedControl = (BControl) super.clone();

		clonedControl.listeners = new PropertyChangeSupport(clonedControl);
		clonedControl.sourceConnections = new ArrayList<BConnection>();
		clonedControl.targetConnections = new ArrayList<BConnection>();

		clonedControl.setParent(getParent());

		// Clone attributes
		Map<String, AbstractAttribute> newProperties = new HashMap<String, AbstractAttribute>();
		for (Entry<String, AbstractAttribute> e : getAttributes().entrySet()) {
			AbstractAttribute idAtr = e.getValue().clone();
			newProperties.put(e.getKey(), idAtr);
		}
		clonedControl.setAttributes(newProperties);
		clonedControl.setAttributeValue(AttributeConstants.ATTRIBUTE_ID,
				getVisualization().getMaxIDString(type));

		// Clone children
		clonedControl.setChildrenArray(new BControlList());
		Iterator<BControl> it = getChildrenArray().iterator();
		while (it.hasNext()) {
			BControl next = (BControl) it.next();
			BControl childClone = next.clone();
			CopyPasteHelper cHelper = (CopyPasteHelper) Clipboard.getDefault()
					.getContents();
			if (cHelper != null)
				cHelper.getAlreadyClonedMap().put(next, childClone);
			clonedControl.addChild(childClone);
		}

		// Clone observer
		clonedControl.setObserverMap(new HashMap<String, Observer>());
		for (Observer observer : observers.values()) {
			clonedControl.addObserver(observer.clone());
		}

		// Clone events
		clonedControl.setEventMap(new HashMap<String, SchedulerEvent>());
		for (Map.Entry<String, SchedulerEvent> e : events.entrySet()) {
			clonedControl.addEvent(e.getKey(), e.getValue().clone());
		}

		return clonedControl;

	}

	public void checkObserver(final Animation animation) {

		// Check all Observers
		for (Observer observer : getObservers().values()) {
			observer.check(animation, BControl.this);
		}

		// TODO: Currently connection observer are checked twice (source +
		// target) => change this, so that observer are checked only on time per
		// state!!!
		for (BConnection con : getSourceConnections()) {
			con.checkObserver(animation);
		}
		for (BConnection con : getTargetConnections()) {
			con.checkObserver(animation);
		}

	}

	public void executeEvent(String eventID) {
		if (hasAttribute(AttributeConstants.ATTRIBUTE_ENABLED)) {
			if (!(Boolean) getAttributeValue(AttributeConstants.ATTRIBUTE_ENABLED))
				return;
		}
		SchedulerEvent e = getEvents().get(eventID);
		if (e != null)
			e.execute(getVisualization().getAnimation(), this);
	}

	public void setVerticalGuide(BMotionGuide verticalGuide) {
		this.verticalGuide = verticalGuide;
	}

	public BMotionGuide getVerticalGuide() {
		return verticalGuide;
	}

	public void setHorizontalGuide(BMotionGuide horizontalGuide) {
		this.horizontalGuide = horizontalGuide;
	}

	public BMotionGuide getHorizontalGuide() {
		return horizontalGuide;
	}

	/**
	 * Return a List of outgoing Connections.
	 */
	public List<BConnection> getSourceConnections() {
		if (this.sourceConnections == null)
			this.sourceConnections = new ArrayList<BConnection>();
		return this.sourceConnections;
	}

	public void setSourceConnections(List<BConnection> connections) {
		this.sourceConnections = connections;
	}

	public void setTargetConnections(List<BConnection> connections) {
		this.targetConnections = connections;
	}

	/**
	 * Return a List of incoming Connections.
	 */
	public List<BConnection> getTargetConnections() {
		if (this.targetConnections == null)
			this.targetConnections = new ArrayList<BConnection>();
		return this.targetConnections;
	}

	public boolean hasConnections() {
		return !getTargetConnections().isEmpty()
				|| !getSourceConnections().isEmpty();
	}

	public boolean showInOutlineView() {
		return true;
	}

	public void setObserverMap(Map<String, Observer> observerMap) {
		observers = observerMap;
	}

	public void setEventMap(Map<String, SchedulerEvent> eventMap) {
		events = eventMap;
	}

	public abstract String getType();

	protected void initAttribute(AbstractAttribute atr) {

		AbstractAttribute oldAtr = getAttribute(atr.getID());

		// If a new control is created via the editor (not from the saved file)
		// set the saved value of the file
		if (oldAtr != null && !newControl) {
			atr.setValue(oldAtr.getValue());
			atr.setDefaultValue(oldAtr.getDefaultValue());
		}

		getAttributes().put(atr.getID(), atr);

	}

	public boolean canHaveChildren() {
		return false;
	}

	public String getValueOfData() {
		return getAttributeValue(AttributeConstants.ATTRIBUTE_CUSTOM)
				.toString();
	}

	public Image getIcon() {
		return BMotionStudioImage.getBControlImage(getType());
	}

}
