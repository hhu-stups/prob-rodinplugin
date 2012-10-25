package de.prob.model.representation;

import java.util.List;

public interface IEntity {
	public List<IEntity> getChildren();

	public boolean hasChildren();
}
