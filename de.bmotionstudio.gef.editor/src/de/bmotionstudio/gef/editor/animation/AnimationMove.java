/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.animation;

import java.util.ArrayList;

import org.eclipse.draw2d.geometry.Point;

import com.jgoodies.animation.AbstractAnimation;
import com.jgoodies.animation.AnimationFunction;
import com.jgoodies.animation.AnimationFunctions;
import com.jgoodies.animation.Animator;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.model.BControl;

public class AnimationMove extends AbstractAnimation {

	private AnimationFunction af;

	private ArrayList<Point> points = new ArrayList<Point>();

	private Animator animator;

	private BControl control;

	public AnimationMove(long duration, boolean freezed, BControl control,
			Integer toX, Integer toY) {
		super(duration, freezed);

		this.control = control;

		Integer fromX = Integer.valueOf(control.getAttributeValue(
				AttributeConstants.ATTRIBUTE_X).toString());
		Integer fromY = Integer.valueOf(control.getAttributeValue(
				AttributeConstants.ATTRIBUTE_Y).toString());

		Bresenham br = new Bresenham();
		br.plot(fromX, fromY, toX, toY);

		points.clear();

		while (br.next()) {
			Point pt = new Point(br.getX(), br.getY());
			points.add(pt);
		}

		af = AnimationFunctions.discrete(duration(), points
				.toArray(new Point[points.size()]));
	}

	public void start() {
		if (points != null) {
			if (points.size() > 0) {
				animator = new Animator(this, 30);
				animator.start();
			} else {
				fireAnimationStopped(30);
			}
		}
	}

	protected void applyEffect(long time) {
		Point pt = (Point) af.valueAt(time);
		control.setLocation(pt);
	}

}
