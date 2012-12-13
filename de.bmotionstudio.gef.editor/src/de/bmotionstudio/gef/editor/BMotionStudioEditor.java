/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferenceFilter;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.PreferenceFilterEntry;
import org.eclipse.gef.EditDomain;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.EditorPart;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import de.bmotionstudio.gef.editor.model.Visualization;
import de.prob.logging.Logger;

public class BMotionStudioEditor extends EditorPart {

	// private BMotionStudioEditorPage editPage;

	// private BMotionStudioRunPage runPage;

	private Visualization visualization;

	private EditDomain editDomain;

	private Composite container;

	private IFile file;

	// private Animation animation;

	// private ArrayList<IRunPageListener> runPageListener = new
	// ArrayList<IRunPageListener>();

	// @Override
	// protected void createPages() {
	// createEditPage();
	// }

	// private void createEditPage() {
	// editPage = new BMotionStudioEditorPage(getVisualization(), this);
	// try {
	// int index = addPage(editPage, getEditorInput());
	// setPageText(index, "Edit");
	// } catch (PartInitException e) {
	// e.printStackTrace();
	// }
	// }

	// public void createRunPage(Visualization visualization, Animation
	// animation) {
	// StaticListenerRegistry.registerListener(this);
	// this.animation = animation;
	//
	// if (runPage != null)
	// runPage.dispose();
	//
	// runPage = new BMotionStudioRunPage(visualization);
	// try {
	// int index = addPage(runPage, getEditorInput());
	// setPageText(index, "Run");
	// setActivePage(index);
	// fireRunPageCreatedListener();
	// } catch (PartInitException e) {
	// }
	// }

	// public void removeRunPage() {
	// fireRunPageRemovedListener();
	// if (runPage != null) {
	// Display.getDefault().asyncExec(new Runnable() {
	// public void run() {
	// removePage(1);
	// }
	// });
	// }
	// unregister();
	// }

	// private void fireRunPageCreatedListener() {
	// for (IRunPageListener listener : runPageListener) {
	// listener.runPageCreated(runPage);
	// }
	// }

	// private void fireRunPageRemovedListener() {
	// for (IRunPageListener listener : runPageListener) {
	// listener.runPageRemoved(runPage);
	// }
	// }

	@Override
	public void dispose() {
		// unregister();
		super.dispose();
	}

	// public Visualization getVisualization() {
	// return visualization;
	// }

	// public void setDirty(boolean dirty) {
	// editPage.setDirty(dirty);
	// }

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {

		file = ((IFileEditorInput) input).getFile();

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

			visualization = (Visualization) xstream.fromXML(inputStream);
			visualization.setProjectFile(file);

			editDomain = new EditDomain();

			// Check if a perspective for this visualization exists
			IPerspectiveRegistry perspectiveRegistry = PlatformUI
					.getWorkbench().getPerspectiveRegistry();

			String perspectiveId = "BMS_" + file.getName().replace(".bmso", "");
			IPerspectiveDescriptor perspective = perspectiveRegistry
					.findPerspectiveWithId(perspectiveId);

			// Yes --> switch to this perspective
			if (perspective != null) {
				switchPerspective(perspectiveId);
			} else {

				// No --> create one
				IWorkbenchWindow window = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				IWorkbenchPage page = window.getActivePage();
				if (page != null) {
					IPerspectiveDescriptor personalPerspectiveDescriptor = perspectiveRegistry
							.findPerspectiveWithId(perspectiveId);
					if (personalPerspectiveDescriptor == null) {
						IPerspectiveDescriptor originalPerspectiveDescriptor = perspectiveRegistry
								.findPerspectiveWithId("de.bmotionstudio.perspective.run");
						switchPerspective(originalPerspectiveDescriptor.getId());
						personalPerspectiveDescriptor = perspectiveRegistry
								.clonePerspective(perspectiveId, perspectiveId,
										originalPerspectiveDescriptor);
						// save the perspective
						page.savePerspectiveAs(personalPerspectiveDescriptor);
						exportPerspective(perspectiveId);
					}
				}

			}

			// System.out.println("===> "
			// + service.getRootNode().nodeExists(
			// "/instance/org.eclipse.ui.workbench"));
			//
			// Preferences node = node;
			//
			// System.out.println(service.getRootNode().node("instance")
			// .node("org.eclipse.ui.workbench").childrenNames().length);
			//
			// for (String s1 : service.getRootNode().childrenNames()) {
			//
			// Preferences node2 = service.getRootNode().node(s1);
			// for (String s2 : node2.childrenNames())
			// for (String s3 : node2.node(s2)
			// .childrenNames())
			// System.out.println(s3);
			// }

			IWorkbenchWindow window = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			IWorkbenchPage activePage = window.getActivePage();
			VisualizationView visualizationView1 = (VisualizationView) activePage
					.showView(VisualizationView.ID, "1",
							IWorkbenchPage.VIEW_VISIBLE);
			visualizationView1.initGraphicalViewer(editDomain, visualization);
			//
			// VisualizationView visualizationView2 = (VisualizationView)
			// activePage
			// .showView(VisualizationView.ID, "2",
			// IWorkbenchPage.VIEW_VISIBLE);
			// visualizationView2.initGraphicalViewer(editDomain,
			// visualization);
			//
			// VisualizationView visualizationView3 = (VisualizationView)
			// activePage
			// .showView(VisualizationView.ID, "3",
			// IWorkbenchPage.VIEW_VISIBLE);
			// visualizationView3.initGraphicalViewer(editDomain,
			// visualization);

		} catch (CoreException e) {
			e.printStackTrace();
		}

		setSite(site);
		setInput(input);

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

	// @Override
	// public String getPartName() {
	// return file.getName().replace(".bmso", "")
	// + " (" + file.getMachineName() + " - "
	// + getVisualization().getLanguage() + ")";
	// }

	private void exportPerspective(final String perspectiveName) {

		try {

			IPreferenceFilter[] transfers = null;

			// export all
			transfers = new IPreferenceFilter[1];

			// For export all create a preference filter that can export
			// all nodes of the Instance and Configuration scopes
			transfers[0] = new IPreferenceFilter() {
				public String[] getScopes() {
					return new String[] { InstanceScope.SCOPE };
				}

				public Map<String, PreferenceFilterEntry[]> getMapping(
						String scope) {
					Map<String, PreferenceFilterEntry[]> map = new HashMap<String, PreferenceFilterEntry[]>();
					map.put("org.eclipse.ui.workbench",
							new PreferenceFilterEntry[] { new PreferenceFilterEntry(
									perspectiveName + "_persp") });
					return map;
				}
			};

			IFile pFile = file.getProject().getFile(
					file.getName().replace(".bmso", ".bmsop"));

			String content = "";

			if (!pFile.exists())
				pFile.create(new ByteArrayInputStream(content.getBytes()),
						true,
						new NullProgressMonitor());

			File exportFile = new File(pFile.getLocationURI());
			FileOutputStream fos = new FileOutputStream(exportFile);
			IPreferencesService service = Platform.getPreferencesService();
			service.exportPreferences(service.getRootNode(), transfers, fos);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void doSave(final IProgressMonitor monitor) {
		// editPage.doSave(monitor);
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
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	// @Override
	// protected void pageChange(int newPageIndex) {
	// String newPageString = getPageText(newPageIndex);
	// if (newPageString.equals("Run")) {
	// switchPerspective("de.bmotionstudio.perspective.run");
	// } else {
	// switchPerspective("de.bmotionstudio.perspective.edit");
	// }
	// }

	// private void switchPerspective(String id) {
	// IWorkbench workbench = PlatformUI.getWorkbench();
	// try {
	// workbench.showPerspective(id, workbench.getActiveWorkbenchWindow());
	// } catch (WorkbenchException e) {
	// Logger.notifyUser(
	// "An error occured while trying to switch the perspective.",
	// e);
	// }
	// }

	// public BMotionStudioEditorPage getEditPage() {
	// return this.editPage;
	// }
	//
	// public BMotionStudioRunPage getRunPage() {
	// return this.runPage;
	// }

	// public void reset() {
	// removeRunPage();
	// }
	//
	// private void unregister() {
	// getVisualization().setIsRunning(false);
	// StaticListenerRegistry.unregisterListener(this);
	// if (animation != null) {
	// animation.unregister();
	// }
	// }

	// public IEditorPart getCurrentPage() {
	// return getActiveEditor();
	// }
	//
	// public void addRunPageListener(IRunPageListener listener) {
	// this.runPageListener.add(listener);
	// }
	//
	// public void removeRunPageListener(IRunPageListener listener) {
	// this.runPageListener.remove(listener);
	// }

	// public double getZoomFactor() {
	// switch (getActivePage()) {
	// case 0:
	// return editPage.getRootEditPart().getZoomManager().getZoom();
	// case 1:
	// return runPage.getRootEditPart().getZoomManager().getZoom();
	// default:
	// return 1;
	// }
	// }

	// @Override
	// protected void setActivePage(int pageIndex) {
	// super.setActivePage(pageIndex);
	// // TODO: This is a hack for fixing the selection bug in the editor!
	// getSite().setSelectionProvider(
	// editPage.getSite().getSelectionProvider());
	// }

}
