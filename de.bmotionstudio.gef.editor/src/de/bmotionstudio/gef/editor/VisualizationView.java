package de.bmotionstudio.gef.editor;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.part.BMSEditPartFactory;

public class VisualizationView extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {

		ScrollingGraphicalViewer scrollingGraphicalViewer = new ScrollingGraphicalViewer();
		scrollingGraphicalViewer.createControl(parent);

		ScalableRootEditPart rootEditPart = new ScalableRootEditPart();
		rootEditPart.setViewer(scrollingGraphicalViewer);
		
		scrollingGraphicalViewer.setRootEditPart(rootEditPart);
		scrollingGraphicalViewer.setEditPartFactory(new BMSEditPartFactory());
		scrollingGraphicalViewer.setEditDomain(new EditDomain());

		IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getProject("Farmer").getFile("Farmer.bmso");

		try {
			InputStream inputStream = file.getContents();

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
							return super.shouldSerializeMember(definedIn,
									fieldName);
						}
					};
				}
			};

			BMotionEditorPlugin.setAliases(xstream);

			Visualization visualization = (Visualization) xstream
					.fromXML(inputStream);
			visualization.setProjectFile(file);

			scrollingGraphicalViewer.setContents(visualization);

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
