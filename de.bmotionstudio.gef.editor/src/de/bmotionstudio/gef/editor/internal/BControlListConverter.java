/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.internal;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.Mapper;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.BControlList;

/**
 * Writes each control of a list as an element named by the control's class.
 * When reading, the class names are resolved by XStream's mapper, so that
 * only the types allowed by
 * {@link de.bmotionstudio.gef.editor.BMotionEditorPlugin#allowTypes} can be
 * created from a file.
 */
public class BControlListConverter implements Converter {

	private final Mapper mapper;

	public BControlListConverter(final Mapper mapper) {
		this.mapper = mapper;
	}

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
			final String name = reader.getNodeName();
			final Class<?> type;
			try {
				// throws a ForbiddenClassException for types not allowed
				type = mapper.realClass(name);
			} catch (CannotResolveClassException e) {
				// fail instead of dropping the control, which would remove
				// it from the file when the visualization is saved
				throw new ConversionException("Unknown control type " + name,
						e);
			}
			if (!BControl.class.isAssignableFrom(type)) {
				throw new ConversionException("Not a control type: " + name);
			}
			list.add((BControl) context.convertAnother(list, type));
			reader.moveUp();
		}
		return list;
	}

}
