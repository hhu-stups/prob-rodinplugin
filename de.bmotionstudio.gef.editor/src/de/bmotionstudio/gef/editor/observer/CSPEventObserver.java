package de.bmotionstudio.gef.editor.observer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.wizard.WizardObserverCSPOperation;
import de.prob.core.command.CSPEvauateExpressionCommand;
import de.prob.exceptions.ProBException;

public class CSPEventObserver extends Observer {

	private List<SetAttributeObject> setAttributeObjects;
	private transient List<String> setAttributes;

	public CSPEventObserver() {
		super();
		this.setAttributeObjects = new ArrayList<SetAttributeObject>();
		this.setAttributes = new ArrayList<String>();
	}

	protected Object readResolve() {
		setAttributes = new ArrayList<String>();
		return super.readResolve();
	}

	@Override
	public void check(Animation animation, BControl control) {
		IFile f = animation.getVisualization().getProjectFile().getProject()
				.getFile(animation.getVisualization().getMachineName());

		// String n = f.getName();
		// String name = n.substring(0, n.length() - 4);
		File file = new File(f.getLocationURI());

		this.setAttributes.clear();

		// Collect evaluate predicate objects in list
		for (SetAttributeObject obj : setAttributeObjects) {

			obj.setHasError(false);

			// First evaluate predicate (predicate field)
			String bolValue = "true";
			if (obj.getEval().length() > 0) {

				String AsImplodedString = "";

				if (animation.getLastExecutedOperation().getArguments().size() > 0) {

					String[] inputArray = animation
							.getLastExecutedOperation()
							.getArguments()
							.toArray(
									new String[animation
											.getLastExecutedOperation()
											.getArguments().size()]);

					StringBuffer sb = new StringBuffer();
					sb.append(inputArray[0]);
					for (int i = 1; i < inputArray.length; i++) {
						sb.append(".");
						sb.append(inputArray[i]);
					}
					AsImplodedString = "." + sb.toString();

				}
				try {
					bolValue = CSPEvauateExpressionCommand.evaluate(animation
							.getAnimator(), "member("
							+ animation.getLastExecutedOperation().getName()
							+ AsImplodedString + "," + obj.getEval()
							+ ")",
							file
							.getAbsolutePath());
					System.out.println("BOOL: ==> " + bolValue);
				} catch (ProBException e) {
					e.printStackTrace();
				}

				// bolValue = BMSUtil.parsePredicate(obj.getEval(), control,
				// animation);
			}

			System.out.println(Boolean.valueOf(bolValue));

			if (!obj.hasError() && Boolean.valueOf(bolValue)) {



				String attributeID = obj.getAttribute();

				// AbstractAttribute attributeObj = control
				// .getAttribute(attributeID);

				Object attributeVal = obj.getValue();

				// if (obj.isExpressionMode()) {
				// String strAtrVal = BMSUtil.parseExpression(
				// attributeVal.toString(), control, animation);
				// String er = attributeObj.validateValue(strAtrVal, null);
				// if (er != null) {
				// obj.setHasError(true);
				// } else {
				// attributeVal = attributeObj.unmarshal(strAtrVal);
				// }
				// }

				if (!obj.hasError()) {
					Object oldAttrVal = control.getAttributeValue(attributeID);
					if (!oldAttrVal.equals(attributeVal)) {
						control.setAttributeValue(attributeID, attributeVal,
								true, false);
					}
				}

				setAttributes.add(attributeID);

			}

		}

		// Restore attribute values
		// for (SetAttributeObject obj : setAttributeObjects) {
		// if (!setAttributes.contains(obj.getAttribute())) {
		// AbstractAttribute attributeObj = control.getAttribute(obj
		// .getAttribute());
		// Object oldAttrVal = control.getAttributeValue(obj
		// .getAttribute());
		// if (!oldAttrVal.equals(attributeObj.getInitValue())) {
		// control.restoreDefaultValue(attributeObj.getID());
		// }
		// }
		// }





	}

	public void setSetAttributeObjects(
			List<SetAttributeObject> setAttributeObjects) {
		this.setAttributeObjects = setAttributeObjects;
	}

	public List<SetAttributeObject> getSetAttributeObjects() {
		return setAttributeObjects;
	}

	@Override
	public ObserverWizard getWizard(Shell shell, BControl control) {
		return new WizardObserverCSPOperation(shell, control, this);
	}

}
