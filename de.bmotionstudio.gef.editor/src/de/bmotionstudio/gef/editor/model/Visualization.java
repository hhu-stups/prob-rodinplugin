/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.PositionConstants;

import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.ButtonGroupHelper;

public class Visualization extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.visualization";

	protected String bmachine, language, version;

	protected BMotionRuler leftRuler, topRuler;

	private boolean rulersVisibility, snapToGeometry, gridEnabled;

	private transient List<String> allBControlIDs;

	public List<String> getAllBControlIDs() {
		if (allBControlIDs == null)
			allBControlIDs = getAllBControlNames();
		return allBControlIDs;
	}

	private transient Boolean isRunning;

	private transient Animation animation;

	private transient IFile projectFile;

	public Visualization(String bmachine, String language, String version) {
		super(null);
		setVisualization(this);
		this.rulersVisibility = true;
		this.bmachine = bmachine;
		this.language = language;
		this.version = version;
		this.isRunning = false;
		this.snapToGeometry = true;
		createRulers();
		ButtonGroupHelper.reset();
	}

	@Override
	protected Object readResolve() {
		super.readResolve();
		this.isRunning = false;
		createRulers();
		ButtonGroupHelper.reset();
		setVisualization(this);
		init();
		initChildren(getChildrenArray());
		return this;
	}

	private void initChildren(List<BControl> children) {
		for (BControl c : children) {
			c.setVisualization(this);
			c.init();
			for (BConnection sc : c.getSourceConnections()) {
				sc.setVisualization(this);
				sc.init();
			}
			for (BConnection tc : c.getTargetConnections()) {
				tc.setVisualization(this);
				tc.init();
			}
			initChildren(c.getChildrenArray());
		}
	}

	public void setIsRunning(Boolean bol) {
		this.isRunning = bol;
	}

	public Boolean isRunning() {
		return isRunning;
	}

	public IFile getProjectFile() {
		return projectFile;
	}

	public void setProjectFile(IFile pf) {
		projectFile = pf;
	}

	public String getMachineName() {
		return this.bmachine;
	}

	public void setMachineName(String machineName) {
		this.bmachine = machineName;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public Animation getAnimation() {
		return this.animation;
	}

	public List<String> getAllBControlNames() {
		return getAllBControlNames(getChildrenArray());
	}

	private List<String> getAllBControlNames(List<BControl> children) {
		List<String> list = new ArrayList<String>();
		for (BControl control : children) {
			list.add(control.getID());
			// Check children
			List<BControl> subchildren = control.getChildrenArray();
			if (children.size() > 0)
				list.addAll(getAllBControlNames(subchildren));
			// Check connections
			List<BControl> connections = new ArrayList<BControl>();
			connections.addAll(control.getSourceConnections());
			connections.addAll(control.getTargetConnections());
			if (connections.size() > 0)
				list.addAll(getAllBControlNames(connections));
		}
		return list;
	}

	public String getMaxIDString(String type) {
		String newID = getMaxID(type, 0, getAllBControlIDs());
		getAllBControlIDs().add(newID);
		return newID;
	}

	// old method
	private String getMaxID(String type, int count, List<String> allIDs) {
		String newID = "control_" + count;
		if (allIDs.contains(newID)) {
			return getMaxID(type, (count + 1), allIDs);
		} else {
			return newID;
		}
	}

	public boolean checkIfIdExists(String ID) {
		return getAllBControlNames().contains(ID);
	}

	public BControl getBControl(String ID) {
		return getBControl(ID, getChildrenArray());
	}

	private BControl getBControl(String ID, List<BControl> children) {
		for (BControl control : children) {
			if (control.getID().equals(ID)) {
				return control;
			}
			for (BConnection c : control.getSourceConnections()) {
				if (c.getID().equals(ID))
					return c;
			}
			for (BConnection c : control.getTargetConnections()) {
				if (c.getID().equals(ID))
					return c;
			}
			if (control.getChildrenArray().size() > 0) {
				BControl childControl = getBControl(ID,
						control.getChildrenArray());
				if (childControl != null)
					return childControl;
			}
		}
		return null;
	}

	public BMotionRuler getRuler(int orientation) {
		BMotionRuler result = null;
		switch (orientation) {
		case PositionConstants.NORTH:
			result = topRuler;
			break;
		case PositionConstants.WEST:
			result = leftRuler;
			break;
		}
		return result;
	}

	public BMotionRuler getTopRuler() {
		return topRuler;
	}

	public void setTopRuler(BMotionRuler topRuler) {
		this.topRuler = topRuler;
	}

	public BMotionRuler getLeftRuler() {
		return leftRuler;
	}

	public void setLeftRuler(BMotionRuler leftRuler) {
		this.leftRuler = leftRuler;
	}

	protected void createRulers() {
		if (leftRuler == null)
			leftRuler = new BMotionRuler(false);
		if (topRuler == null)
			topRuler = new BMotionRuler(true);
	}

	public void setRulerVisibility(boolean newValue) {
		rulersVisibility = newValue;
	}

	public void setGridEnabled(boolean isEnabled) {
		gridEnabled = isEnabled;
	}

	public void setSnapToGeometry(boolean isEnabled) {
		snapToGeometry = isEnabled;
	}

	public boolean getRulerVisibility() {
		return rulersVisibility;
	}

	public boolean isGridEnabled() {
		return gridEnabled;
	}

	public boolean isSnapToGeometryEnabled() {
		return snapToGeometry;
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		return null;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	protected void initAttributes() {
	}

	@Override
	public boolean canHaveChildren() {
		return true;
	}

	@Override
	public Visualization getVisualization() {
		return this;
	}

}
