package de.prob.model.representation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Label implements IEntity {

	public List<IEntity> children = new ArrayList<IEntity>();
	protected String name;
	private boolean locked = false;

	public Label(final String name) {
		this.name = name;
	}

	public void addChild(final IEntity child) {
		if (!locked) {
			children.add(child);
		}
	}

	@Override
	public List<IEntity> getChildren() {
		if (locked) {
			return Collections.unmodifiableList(children);
		}
		return children;
	}

	public String getName() {
		return name;
	}

	public void lock() {
		locked = true;
		for (final IEntity child : children) {
			if (child instanceof Label) {
				((Label) child).lock();
			}
		}
	}

	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}
}
