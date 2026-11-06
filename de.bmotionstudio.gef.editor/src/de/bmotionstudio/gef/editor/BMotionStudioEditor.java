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
import javax.xml.transform.TransformerException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import de.bmotionstudio.gef.editor.internal.BMSConverter512;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.prob.core.ILifecycleListener;
import de.prob.logging.Logger;

public class BMotionStudioEditor extends MultiPageEditorPart implements
		ILifecycleListener {

	public static final String ID = "de.bmotionstudio.gef.editor.BMotionStudioEditor";

	public static final String PERSPECTIVE_RUN = "de.bmotionstudio.perspective.run";

	public static final String PERSPECTIVE_EDIT = "de.bmotionstudio.perspective.edit";

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

		// a restart: stop the previous run, otherwise its animation keeps
		// evaluating the observers of the old controls on every state change
		// and its page stays open
		if (this.animation != null && this.animation != animation) {
			this.animation.unregister();
			this.animation.getVisualization().setIsRunning(false);
		}
		this.animation = animation;
		if (runPage != null) {
			fireRunPageRemovedListener();
			removeRunPageTab(runPage);
			runPage = null;
		}

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
		final BMotionStudioRunPage page = runPage;
		runPage = null;
		if (page != null) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					removeRunPageTab(page);
				}
			});
		}
		unregister();
	}

	/**
	 * Removes the tab of the given run page (which disposes it), if it is
	 * still open; the index of the tab may have changed in the meantime
	 */
	private void removeRunPageTab(final BMotionStudioRunPage page) {
		if (getContainer() == null || getContainer().isDisposed())
			return; // editor closed
		for (int i = 0; i < getPageCount(); i++) {
			if (getEditor(i) == page) {
				removePage(i);
				return;
			}
		}
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
		final IWorkbenchPage page = getSite() != null ? getSite().getPage()
				: null;
		super.dispose();
		if (page != null)
			closePerspectivesLater(page);
	}

	/**
	 * Closes the BMS Edit and BMS Run perspectives, which removes their icons
	 * from the perspective bar, once no visualisation is open in the page any
	 * more. Not when the workbench closes, so that they are restored with
	 * the visualisations at the next start.
	 */
	private static void closePerspectivesLater(final IWorkbenchPage page) {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench.isClosing())
			return;
		// after the editor was removed from the page, and only once when
		// several visualisations are closed together
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (workbench.isClosing() || page.getWorkbenchWindow() == null
						|| page.getWorkbenchWindow().getShell() == null
						|| page.getWorkbenchWindow().getShell().isDisposed())
					return;
				for (IEditorReference editor : page.getEditorReferences())
					if (ID.equals(editor.getId()))
						return; // a visualisation is still open
				final IPerspectiveDescriptor active = page.getPerspective();
				IPerspectiveDescriptor closeLast = null;
				for (IPerspectiveDescriptor perspective : page
						.getOpenPerspectives()) {
					if (!isBMotionPerspective(perspective))
						continue;
					if (perspective.equals(active))
						closeLast = perspective;
					else
						page.closePerspective(perspective, false, false);
				}
				// Eclipse then switches to the previously used perspective,
				// unless there is none
				if (closeLast != null && page.getOpenPerspectives().length > 1)
					page.closePerspective(closeLast, false, false);
			}
		});
	}

	private static boolean isBMotionPerspective(
			final IPerspectiveDescriptor perspective) {
		return PERSPECTIVE_RUN.equals(perspective.getId())
				|| PERSPECTIVE_EDIT.equals(perspective.getId());
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

		Object loaded;
		try {

			// -------------------------------------------------------

			DocumentBuilderFactory dbFactory = BMotionEditorPlugin
					.createSecureDocumentBuilderFactory();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try (InputStream in = file.getContents()) {
				doc = dBuilder.parse(in);
			}
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

			try (InputStream inputStream = file.getContents()) {
				loaded = xstream.fromXML(inputStream);
			}

		} catch (SAXException | IOException | CoreException
				| ParserConfigurationException | TransformerException
				| XStreamException e) {
			throw new PartInitException("Could not open BMotion Studio file "
					+ file.getFullPath() + ": " + e.getMessage(), e);
		}

		if (!(loaded instanceof Visualization)) {
			throw new PartInitException("The file " + file.getFullPath()
					+ " does not contain a BMotion Studio visualization.");
		}
		visualization = (Visualization) loaded;
		visualization.setProjectFile(file);
		// initLanguage(visualization);

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
		super.pageChange(newPageIndex);
		String newPageString = getPageText(newPageIndex);
		final String perspective = newPageString.equals("Run")
				? PERSPECTIVE_RUN : PERSPECTIVE_EDIT;
		// Not immediately: pageChange is also called while the editor is
		// created, e.g. when Rodin restores it at startup in the middle of
		// creating the workbench window. Switching the perspective then left
		// the views of the run perspective (State, History) empty.
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				final Composite container = getContainer();
				if (container == null || container.isDisposed()
						|| getActivePage() != newPageIndex)
					return; // closed or another page selected meanwhile
				switchPerspective(perspective);
			}
		});
	}

	private void switchPerspective(String id) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window == null)
			return; // e.g. another shell is active
		final IWorkbenchPage page = window.getActivePage();
		if (page != null && page.getPerspective() != null
				&& id.equals(page.getPerspective().getId()))
			return;
		try {
			workbench.showPerspective(id, window);
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

	@Override
	public void reset() {
		removeRunPage();
	}

	private void unregister() {
		// visualization is null if init failed
		if (visualization != null) {
			visualization.setIsRunning(false);
		}
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
