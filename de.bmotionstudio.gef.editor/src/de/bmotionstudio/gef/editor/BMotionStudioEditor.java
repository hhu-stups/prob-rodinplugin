/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import de.bmotionstudio.gef.editor.animation.StaticListenerRegistry;
import de.bmotionstudio.gef.editor.internal.BMSConverter512;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.prob.core.ILifecycleListener;
import de.prob.logging.Logger;

public class BMotionStudioEditor extends MultiPageEditorPart implements
		ILifecycleListener {

	private BMotionStudioEditorPage editPage;

	private BMotionStudioRunPage runPage;

	private Visualization visualization;

	private Animation animation;

	private ArrayList<IRunPageListener> runPageListener = new ArrayList<IRunPageListener>();

	@Override
	protected void createPages() {
		createEditPage();
	}

	private void createEditPage() {
		editPage = new BMotionStudioEditorPage(getVisualization(), this);
		try {
			int index = addPage(editPage, getEditorInput());
			setPageText(index, "Edit");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	public void createRunPage(Visualization visualization, Animation animation) {
		StaticListenerRegistry.registerListener(this);
		this.animation = animation;

		if (runPage != null)
			runPage.dispose();

		runPage = new BMotionStudioRunPage(visualization);
		try {
			int index = addPage(runPage, getEditorInput());
			setPageText(index, "Run");
			setActivePage(index);
			fireRunPageCreatedListener();
		} catch (PartInitException e) {
		}
	}

	public void removeRunPage() {
		fireRunPageRemovedListener();
		if (runPage != null) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					removePage(1);
				}
			});
		}
		unregister();
	}

	private void fireRunPageCreatedListener() {
		for (IRunPageListener listener : runPageListener) {
			listener.runPageCreated(runPage);
		}
	}

	private void fireRunPageRemovedListener() {
		for (IRunPageListener listener : runPageListener) {
			listener.runPageRemoved(runPage);
		}
	}

	@Override
	public void dispose() {
		unregister();
		super.dispose();
	}

	public Visualization getVisualization() {
		return visualization;
	}

	public void setDirty(boolean dirty) {
		editPage.setDirty(dirty);
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#init(IEditorSite, IEditorInput)
	 **/
	@Override
	public void init(IEditorSite iSite, IEditorInput iInput)
			throws PartInitException {

		super.init(iSite, iInput);

		IFile file = ((IFileEditorInput) iInput).getFile();

		try {

			// -------------------------------------------------------

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file.getContents());
			NodeList nList = doc.getElementsByTagName("version");
			if (nList.item(0) != null) {
				Element versionElement = (Element) nList.item(0);
				String version = versionElement.getTextContent();
				if (version.equals("5.1.2")) {
					new BMSConverter512(file);
				}
			} else {
				new BMSConverter512(file);
			}

			// -------------------------------------------------------

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

			visualization = (Visualization) xstream.fromXML(inputStream);
			visualization.setProjectFile(((IFileEditorInput) getEditorInput())
					.getFile());
			// initLanguage(visualization);

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getPartName() {
		return visualization.getProjectFile().getName().replace(".bmso", "")
				+ " (" + getVisualization().getMachineName() + " - "
				+ getVisualization().getLanguage() + ")";
	}

	@Override
	public void doSave(final IProgressMonitor monitor) {
		editPage.doSave(monitor);
	}

	@Override
	public void doSaveAs() {
		// Nothing to do here, this is never allowed
		throw new IllegalAccessError("No way to enter this method.");
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	protected void pageChange(int newPageIndex) {
		String newPageString = getPageText(newPageIndex);
		if (newPageString.equals("Run")) {
			switchPerspective("de.bmotionstudio.perspective.run");
		} else {
			switchPerspective("de.bmotionstudio.perspective.edit");
		}
	}

	private void switchPerspective(String id) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		try {
			workbench.showPerspective(id, workbench.getActiveWorkbenchWindow());
		} catch (WorkbenchException e) {
			Logger.notifyUser(
					"An error occured while trying to switch the perspective.",
					e);
		}
	}

	public BMotionStudioEditorPage getEditPage() {
		return this.editPage;
	}

	public BMotionStudioRunPage getRunPage() {
		return this.runPage;
	}

	public void reset() {
		removeRunPage();
	}

	private void unregister() {
		getVisualization().setIsRunning(false);
		StaticListenerRegistry.unregisterListener(this);
		if (animation != null) {
			animation.unregister();
		}
	}

	public IEditorPart getCurrentPage() {
		return getActiveEditor();
	}

	public void addRunPageListener(IRunPageListener listener) {
		this.runPageListener.add(listener);
	}

	public void removeRunPageListener(IRunPageListener listener) {
		this.runPageListener.remove(listener);
	}

	public double getZoomFactor() {
		switch (getActivePage()) {
		case 0:
			return editPage.getRootEditPart().getZoomManager().getZoom();
		case 1:
			return runPage.getRootEditPart().getZoomManager().getZoom();
		default:
			return 1;
		}
	}

	@Override
	protected void setActivePage(int pageIndex) {
		super.setActivePage(pageIndex);
		// TODO: This is a hack for fixing the selection bug in the editor!
		getSite().setSelectionProvider(
				editPage.getSite().getSelectionProvider());
	}

}
