/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.rulers.RulerChangeListener;
import org.eclipse.gef.rulers.RulerProvider;

import de.bmotionstudio.gef.editor.command.CreateGuideCommand;
import de.bmotionstudio.gef.editor.command.DeleteGuideCommand;
import de.bmotionstudio.gef.editor.command.MoveGuideCommand;

public class BMotionRulerProvider extends RulerProvider {

	private BMotionRuler ruler;
	private PropertyChangeListener rulerListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(BMotionRuler.PROPERTY_CHILDREN)) {
				BMotionGuide guide = (BMotionGuide) evt.getNewValue();
				if (getGuides().contains(guide)) {
					guide.addPropertyChangeListener(guideListener);
				} else {
					guide.removePropertyChangeListener(guideListener);
				}
				for (int i = 0; i < listeners.size(); i++) {
					((RulerChangeListener) listeners.get(i))
							.notifyGuideReparented(guide);
				}
			} else {
				for (int i = 0; i < listeners.size(); i++) {
					((RulerChangeListener) listeners.get(i))
							.notifyUnitsChanged(ruler.getUnit());
				}
			}
		}
	};
	private PropertyChangeListener guideListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(BMotionGuide.PROPERTY_CHILDREN)) {
				for (int i = 0; i < listeners.size(); i++) {
					((RulerChangeListener) listeners.get(i))
							.notifyPartAttachmentChanged(evt.getNewValue(), evt
									.getSource());
				}
			} else {
				for (int i = 0; i < listeners.size(); i++) {
					((RulerChangeListener) listeners.get(i))
							.notifyGuideMoved(evt.getSource());
				}
			}
		}
	};

	public BMotionRulerProvider(BMotionRuler ruler) {
		this.ruler = ruler;
		this.ruler.addPropertyChangeListener(rulerListener);
		List<BMotionGuide> guides = getGuides();
		for (int i = 0; i < guides.size(); i++) {
			((BMotionGuide) guides.get(i))
					.addPropertyChangeListener(guideListener);
		}
	}

	public List<BControl> getAttachedModelObjects(Object guide) {
		return new ArrayList<BControl>(((BMotionGuide) guide).getParts());
	}

	public Command getCreateGuideCommand(int position) {
		return new CreateGuideCommand(ruler, position);
	}

	public Command getDeleteGuideCommand(Object guide) {
		return new DeleteGuideCommand((BMotionGuide) guide, ruler);
	}

	public Command getMoveGuideCommand(Object guide, int pDelta) {
		return new MoveGuideCommand((BMotionGuide) guide, pDelta);
	}

	public int[] getGuidePositions() {
		List<BMotionGuide> guides = getGuides();
		int[] result = new int[guides.size()];
		for (int i = 0; i < guides.size(); i++) {
			result[i] = ((BMotionGuide) guides.get(i)).getPosition();
		}
		return result;
	}

	public Object getRuler() {
		return ruler;
	}

	public int getUnit() {
		return ruler.getUnit();
	}

	public void setUnit(int newUnit) {
		ruler.setUnit(newUnit);
	}

	public int getGuidePosition(Object guide) {
		return ((BMotionGuide) guide).getPosition();
	}

	public List<BMotionGuide> getGuides() {
		return ruler.getGuides();
	}

}
