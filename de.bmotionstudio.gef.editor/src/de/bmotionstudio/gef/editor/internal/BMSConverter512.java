/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.internal;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.IBControlService;
import de.bmotionstudio.gef.editor.attribute.AbstractAttribute;
import de.bmotionstudio.gef.editor.model.BConnection;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.BControlList;
import de.bmotionstudio.gef.editor.model.BMotionGuide;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.observer.Observer;
import de.bmotionstudio.gef.editor.scheduler.SchedulerEvent;

public class BMSConverter512 {

	private IFile file;

	public BMSConverter512(IFile file) {
		this.file = file;
		try {
			convert();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}
	}

	private void convert() throws CoreException, IOException,
			ParserConfigurationException, SAXException,
			TransformerConfigurationException, TransformerException,
			TransformerFactoryConfigurationError {

		InputStream inputStream = file.getContents();

		ByteArrayInputStream byteArrayInputStream = null;

		String str = inputStreamToString(inputStream);

		str = str.replace("de.bmotionstudio.gef.basic",
				"de.bmotionstudio.gef.editor");
		str = str.replace("de.bmotionstudio.gef.core",
				"de.bmotionstudio.gef.editor");

		str = str.replace("de.bmotionstudio.gef.editor.model.BControl",
				"control");
		str = str.replace("de.bmotionstudio.gef.editor.model.BMotionGuide",
				"guide");
		str = str.replace("de.bmotionstudio.gef.editor.Visualization",
				"visualization");
		str = str.replace("de.bmotionstudio.gef.editor.model.Visualization",
				"visualization");
		str = str.replace("de.bmotionstudio.gef.editor.model.BConnection",
				"connection");

		str = str.replace("de.bmotionstudio.gef.editor.rectangle",
				"de.bmotionstudio.gef.editor.shape");

		str = str.replace(
				"de.bmotionstudio.gef.editor.observer.ToggleCoordinates",
				"de.bmotionstudio.gef.editor.observer.SwitchCoordinates");
		str = str.replace("de.bmotionstudio.gef.editor.observer.ToggleImage",
				"de.bmotionstudio.gef.editor.observer.SwitchImage");
		str = str.replace("de.bmotionstudio.gef.editor.observer.ToggleImage",
				"de.bmotionstudio.gef.editor.observer.SwitchImage");
		str = str.replace(
				"de.bmotionstudio.gef.editor.observer.ToggleChildCoordinates",
				"de.bmotionstudio.gef.editor.observer.SwitchChildCoordinates");

		byteArrayInputStream = new ByteArrayInputStream(str.getBytes());

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(byteArrayInputStream);

		NodeList nList = doc.getElementsByTagName("type");
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			if (nNode.getParentNode().getNodeName().equals("control")) {
				Element parent = (Element) nNode.getParentNode();
				parent.setAttribute("type", nNode.getTextContent());
			}

		}

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Source xmlSource = new DOMSource(doc);
		Result outputTarget = new StreamResult(outputStream);
		TransformerFactory.newInstance().newTransformer()
				.transform(xmlSource, outputTarget);
		byteArrayInputStream = new ByteArrayInputStream(
				outputStream.toByteArray());

		XStream xstream = new XStream() {
			@Override
			protected MapperWrapper wrapMapper(final MapperWrapper next) {
				return new MapperWrapper(next) {
					@Override
					public boolean shouldSerializeMember(
							@SuppressWarnings("rawtypes") final Class definedIn,
							final String fieldName) {
						if (definedIn == Object.class)
							return false;
						return super
								.shouldSerializeMember(definedIn, fieldName);
					}
				};
			}
		};

		xstream.registerConverter(new Converter() {

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
					list.add((BControl) context.convertAnother(list,
							BControl.class));
					reader.moveUp();
				}
				return list;
			}
		});
		xstream.registerConverter(new Converter() {

			@Override
			public boolean canConvert(Class clazz) {
				if (clazz == BControl.class)
					return true;
				return false;
			}

			@Override
			public void marshal(Object obj, HierarchicalStreamWriter writer,
					MarshallingContext context) {
				BControl c = (BControl) obj;
				writer.startNode(c.getClass().getName());
				context.convertAnother(c);
				writer.endNode();
			}

			@Override
			public Object unmarshal(HierarchicalStreamReader reader,
					UnmarshallingContext context) {

				String type = reader.getAttribute("type");

				BControl c = null;

				IConfigurationElement controlExtension = BMotionEditorPlugin
						.getControlExtension(type);
				if (controlExtension != null) {
					try {
						IBControlService service = (IBControlService) controlExtension
								.createExecutableExtension("service");
						c = service.createControl(null);
					} catch (CoreException e) {
						e.printStackTrace();
					}

				}

				if (c != null) {

					while (reader.hasMoreChildren()) {

						reader.moveDown();

						if ("children".equals(reader.getNodeName())) {
							c.setChildrenArray((BControlList) context
									.convertAnother(c, BControlList.class));
						} else if ("observers".equals(reader.getNodeName())) {
							c.setObserverMap((Map<String, Observer>) context
									.convertAnother(c, Map.class));
						} else if ("events".equals(reader.getNodeName())) {
							c.setEventMap((Map<String, SchedulerEvent>) (context
									.convertAnother(c, Map.class)));
						} else if ("attributes".equals(reader.getNodeName())) {
							Map<String, AbstractAttribute> attributes = (Map<String, AbstractAttribute>) context
									.convertAnother(c, Map.class);
							for (AbstractAttribute atr : attributes.values()) {
								atr.setEditable(true);
								atr.setShow(true);
							}
							c.setAttributes(attributes);
						} else if ("verticalGuide".equals(reader.getNodeName())) {
							c.setVerticalGuide((BMotionGuide) context
									.convertAnother(c, BMotionGuide.class));
						} else if ("horizontalGuide".equals(reader
								.getNodeName())) {
							c.setVerticalGuide((BMotionGuide) context
									.convertAnother(c, BMotionGuide.class));
						} else if ("sourceConnections".equals(reader
								.getNodeName())) {
							c.setHorizontalGuide((BMotionGuide) context
									.convertAnother(c, BMotionGuide.class));
						} else if ("targetConnections".equals(reader
								.getNodeName())) {
							c.setTargetConnections((List<BConnection>) context
									.convertAnother(c, List.class));
						}

						reader.moveUp();

					}

				}

				return c;

			}

		});
		xstream.useAttributeFor(BControl.class, "type");
		xstream.alias("control", BControl.class);
		xstream.alias("visualization", Visualization.class);
		xstream.alias("guide", BMotionGuide.class);
		xstream.alias("connection", BConnection.class);
		xstream.alias("children", BControlList.class);
		BMotionEditorPlugin.allowTypes(xstream);

		Visualization visualization = (Visualization) xstream
				.fromXML(byteArrayInputStream);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(out, "UTF8");
		visualization.setVersion("5.2.0");
		xstream.toXML(visualization, writer);
		file.setContents(new ByteArrayInputStream(out.toByteArray()), false,
				false, null);

	}

	private static String inputStreamToString(InputStream in)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(in));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line + "\n");
		}
		bufferedReader.close();
		return stringBuilder.toString();
	}

}
