/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.internal;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.BControlList;

public class BControlListConverter implements Converter {

	@Override
	public boolean canConvert(Class clazz) {
		if (clazz == BControlList.class)
			return true;
		return false;
	}

	@Override
	public void marshal(Object obj, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		BControlList list = (BControlList) obj;
		for (BControl control : list) {
			writer.startNode(control.getClass().getName());
			context.convertAnother(control);
			writer.endNode();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		BControlList list = new BControlList();
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			try {
				BControl c = (BControl) context.convertAnother(list,
						Class.forName(reader.getNodeName()));
				list.add(c);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			reader.moveUp();
		}
		return list;
	}

}
