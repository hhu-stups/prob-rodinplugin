package de.prob.model.eventb;

import java.util.Collections;
import java.util.List;

import de.prob.model.representation.IEntity;

public class Action implements IEntity {

	private final String code;
	private final String name;

	public Action(String code, String name) {
		this.code = code;
		this.name = name;
	}

	@Override
	public List<IEntity> getChildren() {
		return Collections.emptyList();
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

}
