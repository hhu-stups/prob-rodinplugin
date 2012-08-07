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

import de.be4.classicalb.core.parser.exceptions.BException;
import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.ButtonGroupHelper;
import de.bmotionstudio.gef.editor.IAddErrorListener;
import de.bmotionstudio.gef.editor.scheduler.PredicateOperation;
import de.prob.core.command.ExecuteOperationCommand;
import de.prob.core.command.GetOperationByPredicateCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.exceptions.ProBException;

public class Visualization extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.text";

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

	private transient ArrayList<IAddErrorListener> errorListener;

	private transient Thread operationSchedulerThread;

	private ArrayList<PredicateOperation> schedulerOperations;

	public Visualization(String bmachine, String language, String version) {
		super(null);
		setVisualization(this);
		this.rulersVisibility = true;
		this.bmachine = bmachine;
		this.language = language;
		this.version = version;
		this.errorListener = new ArrayList<IAddErrorListener>();
		this.isRunning = false;
		createRulers();
		ButtonGroupHelper.reset();
	}

	@Override
	protected Object readResolve() {
		super.readResolve();
		this.isRunning = false;
		populateVisualization(this);
		createRulers();
		this.errorListener = new ArrayList<IAddErrorListener>();
		ButtonGroupHelper.reset();
		// this.errorMessages = new ArrayList<ErrorMessage>();
		// setAttributeValue(AttributeConstants.ATTRIBUTE_ID, "surface", false);
		return this;
	}

	public void startOperationScheduler() {

		if (!getSchedulerOperations().isEmpty()) {

			operationSchedulerThread = new Thread(new Runnable() {
				public void run() {
					while (true) {

						for (PredicateOperation p : getSchedulerOperations()) {

							if (animation.getCurrentStateOperation(p
									.getOperationName()) != null) {

								try {

									String fpredicate = "1=1";

									if (p.getPredicate().length() > 0) {
										fpredicate = p.getPredicate();
									}

									Operation op = GetOperationByPredicateCommand
											.getOperation(animation
													.getAnimator(), animation
													.getState().getId(), p
													.getOperationName(),
													fpredicate);
									ExecuteOperationCommand.executeOperation(
											animation.getAnimator(), op);

								} catch (ProBException e) {
									break;
								} catch (BException e) {
									break;
								}

							}

						}

						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							break;
						}

					}
				}
			});
			operationSchedulerThread.start();

		}

	}

	public void stopOperationScheduler() {
		if (operationSchedulerThread != null)
			operationSchedulerThread.interrupt();
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
		for (BControl bcontrol : children) {
			list.add(bcontrol
					.getAttributeValue(AttributeConstants.ATTRIBUTE_ID)
					.toString());
			if (bcontrol.getChildrenArray().size() > 0) {
				list.addAll(getAllBControlNames(bcontrol.getChildrenArray()));
			}
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

	// TODO: Reimplement me!!!
	public boolean checkIfIdExists(String ID) {
		// if (getVariableList().hasEntry(ID) == true)
		// return true;
		// if (getConstantList().hasEntry(ID) == true)
		// return true;
		return getAllBControlNames().contains(ID);
	}

	public BControl getBControl(String ID) {
		return getBControl(ID, getChildrenArray());
	}

	private BControl getBControl(String ID, List<BControl> children) {
		for (BControl bcontrol : children) {
			if (bcontrol.getID().equals(ID)) {
				return bcontrol;
			}
			if (bcontrol.getChildrenArray().size() > 0) {
				BControl childControl = getBControl(ID,
						bcontrol.getChildrenArray());
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

	public void addErrorListener(IAddErrorListener listener) {
		this.errorListener.add(listener);
	}

	public void removeErrorListener(IAddErrorListener listener) {
		this.errorListener.remove(listener);
	}

	public void setSchedulerOperations(
			ArrayList<PredicateOperation> schedulerOperations) {
		this.schedulerOperations = schedulerOperations;
	}

	public ArrayList<PredicateOperation> getSchedulerOperations() {
		if (this.schedulerOperations == null)
			this.schedulerOperations = new ArrayList<PredicateOperation>();
		return this.schedulerOperations;
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

}
