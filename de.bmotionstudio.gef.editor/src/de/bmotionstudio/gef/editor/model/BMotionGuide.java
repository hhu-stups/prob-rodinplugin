/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.requests.ChangeBoundsRequest;

/**
 * Model object representing a guide.
 * <p>
 * In addition to maintaining information about which parts are attached to the
 * guide, LogicGuide also maintains information about the edge along which those
 * parts are attached. This information is useful during resize operations to
 * determine the attachment status of a part.
 * </p>
 */
public class BMotionGuide implements Serializable {

	/**
	 * Property used to notify listeners when the parts attached to a guide are
	 * changed
	 */
	public transient static final String PROPERTY_CHILDREN = "subparts changed"; //$NON-NLS-1$
	/**
	 * Property used to notify listeners when the guide is re-positioned
	 */
	public transient static final String PROPERTY_POSITION = "position changed"; //$NON-NLS-1$

	static transient final long serialVersionUID = 1;

	protected transient PropertyChangeSupport listeners = new PropertyChangeSupport(
			this);
	private HashMap<BControl, Integer> map;
	private int position;
	private boolean horizontal;

	/**
	 * Empty default constructor
	 */
	public BMotionGuide() {
		// empty constructor
	}

	/**
	 * Constructor
	 * 
	 * @param isHorizontal
	 *            <code>true</code> if the guide is horizontal (i.e., placed on
	 *            a vertical ruler)
	 */
	public BMotionGuide(boolean isHorizontal) {
		setHorizontal(isHorizontal);
	}

	protected Object readResolve() {
		this.listeners = new PropertyChangeSupport(this);
		return this;
	}

	/**
	 * @see PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	/*
	 * @TODO:Pratik use PositionConstants here
	 */
	/**
	 * Attaches the given part along the given edge to this guide. The
	 * LogicSubpart is also updated to reflect this attachment.
	 * 
	 * @param part
	 *            The part that is to be attached to this guide; if the part is
	 *            already attached, its alignment is updated
	 * @param alignment
	 *            -1 is left or top; 0, center; 1, right or bottom
	 */
	public void attachPart(BControl control, int alignment) {

		if (getMap().containsKey(control) && getAlignment(control) == alignment)
			return;

		getMap().put(control, Integer.valueOf(alignment));

		BMotionGuide parent = isHorizontal() ? control.getHorizontalGuide()
				: control.getVerticalGuide();
		if (parent != null && parent != this) {
			parent.detachPart(control);
		}
		if (isHorizontal()) {
			control.setHorizontalGuide(this);
		} else {
			control.setVerticalGuide(this);
		}
		listeners.firePropertyChange(PROPERTY_CHILDREN, null, control);

	}

	/**
	 * Detaches the given part from this guide. The LogicSubpart is also updated
	 * to reflect this change.
	 * 
	 * @param part
	 *            the part that is to be detached from this guide
	 */
	public void detachPart(BControl control) {
		if (getMap().containsKey(control)) {
			getMap().remove(control);
			if (isHorizontal()) {
				control.setHorizontalGuide(null);
			} else {
				control.setVerticalGuide(null);
			}
			listeners.firePropertyChange(PROPERTY_CHILDREN, null, control);
		}
	}

	/**
	 * This methods returns the edge along which the given part is attached to
	 * this guide. This information is used by
	 * {@link org.eclipse.gef.examples.logicdesigner.edit.LogicXYLayoutEditPolicy
	 * LogicXYLayoutEditPolicy} to determine whether to attach or detach a part
	 * from a guide during resize operations.
	 * 
	 * @param part
	 *            The part whose alignment has to be found
	 * @return an int representing the edge along which the given part is
	 *         attached to this guide; 1 is bottom or right; 0, center; -1, top
	 *         or left; -2 if the part is not attached to this guide
	 * @see org.eclipse.gef.examples.logicdesigner.edit.LogicXYLayoutEditPolicy#createChangeConstraintCommand(ChangeBoundsRequest,
	 *      EditPart, Object)
	 */
	public int getAlignment(BControl part) {
		if (getMap().get(part) != null)
			return ((Integer) getMap().get(part)).intValue();
		return -2;
	}

	/**
	 * @return The Map containing all the parts attached to this guide, and
	 *         their alignments; the keys are LogicSubparts and values are
	 *         Integers
	 */
	public HashMap<BControl, Integer> getMap() {
		if (map == null) {
			map = new HashMap<BControl, Integer>();
		}
		return map;
	}

	/**
	 * @return the set of all the parts attached to this guide; a set is used
	 *         because a part can only be attached to a guide along one edge.
	 */
	public Set<BControl> getParts() {
		return getMap().keySet();
	}

	/**
	 * @return the position/location of the guide (in pixels)
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @return <code>true</code> if the guide is horizontal (i.e., placed on a
	 *         vertical ruler)
	 */
	public boolean isHorizontal() {
		return horizontal;
	}

	/**
	 * @see PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}

	/**
	 * Sets the orientation of the guide
	 * 
	 * @param isHorizontal
	 *            <code>true</code> if this guide is to be placed on a vertical
	 *            ruler
	 */
	public void setHorizontal(boolean isHorizontal) {
		horizontal = isHorizontal;
	}

	/**
	 * Sets the location of the guide
	 * 
	 * @param offset
	 *            The location of the guide (in pixels)
	 */
	public void setPosition(int offset) {
		if (position != offset) {
			int oldValue = position;
			position = offset;
			listeners.firePropertyChange(PROPERTY_POSITION,
					Integer.valueOf(oldValue), Integer.valueOf(position));
		}
	}
}
