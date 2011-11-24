/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */
package de.prob.core.domainobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class OperationInfo {

	private final String name;
	private final List<String> parameters;

	public OperationInfo(final String name, final List<String> parameters) {
		this.name = name;
		this.parameters = parameters;
	}

	public String getName() {
		return name;
	}

	public List<String> getParameters() {
		return Collections.unmodifiableList(parameters);
	}

	public static List<String> extractNames(
			final Collection<OperationInfo> names) {
		List<String> result = new ArrayList<String>();
		for (OperationInfo operationInfo : names) {
			result.add(operationInfo.getName());
		}
		return result;
	}

	public static OperationInfo getParams(final String name,
			final Collection<OperationInfo> names) {
		for (OperationInfo info : names) {
			if (name.equals(info.getName())) {
				return info;
			}
		}
		return null;
	}

}
