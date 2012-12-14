/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IExportedPreferences;
import org.eclipse.core.runtime.preferences.IPreferenceFilter;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.PreferenceFilterEntry;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.EditorPart;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import de.bmotionstudio.gef.editor.model.Simulation;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.model.VisualizationView;
import de.prob.logging.Logger;

public class BMotionStudioEditor extends EditorPart implements IPartListener2 {

	private Simulation simulation;

	private EditDomain editDomain;

	private Composite container;

	private IFile file;

	private boolean isDirty;

	private IPerspectiveDescriptor perspective;

	private String getPerspectiveId() {
		return "BMS_" + file.getName().replace(".bmso", "");
	}

	private String getPerspectiveFileName() {
		return file.getName().replace(".bmso", ".bmsop");
	}

	private void closePerspective(IWorkbenchPage page,
			IPerspectiveDescriptor perspectiveDescriptor) {
		if (perspectiveDescriptor == null || page == null)
			return;
		page.closePerspective(perspectiveDescriptor, false, true);
	}

	private void deletePerspective(IWorkbenchPage page,
			IPerspectiveDescriptor perspectiveDescriptor) {
		if (perspectiveDescriptor == null || page == null)
			return;
		IPerspectiveRegistry perspectiveRegistry = page.getWorkbenchWindow()
				.getWorkbench().getPerspectiveRegistry();
		perspectiveRegistry.deletePerspective(perspectiveDescriptor);
	}

	private void openPerspective(IWorkbenchPage page) {

		if (page == null)
			return;

		// Try to get the corresponding perspective
		IPerspectiveRegistry perspectiveRegistry = page.getWorkbenchWindow()
				.getWorkbench().getPerspectiveRegistry();
		String perspectiveId = getPerspectiveId();
		perspective = perspectiveRegistry.findPerspectiveWithId(perspectiveId);

		// Yes --> just switch to this perspective
		if (perspective != null) {
			switchPerspective(perspective.getId());
		} else {
			// No --> create a new one
			IPerspectiveDescriptor originalPerspectiveDescriptor = perspectiveRegistry
					.findPerspectiveWithId("de.bmotionstudio.perspective.run");
			switchPerspective(originalPerspectiveDescriptor.getId());
			perspective = perspectiveRegistry.clonePerspective(perspectiveId,
					perspectiveId, originalPerspectiveDescriptor);
			// save the perspective
			page.savePerspectiveAs(perspective);
		}

	}

	private void importPerspective(IFile perspectiveFile) {

		FileInputStream fis = null;

		try {

			IPreferenceFilter[] transfers = null;
			transfers = new IPreferenceFilter[1];

			// Only import if a perspective file exists
			if (perspectiveFile.exists()) {

				File exportFile = new File(perspectiveFile.getLocationURI());
				fis = new FileInputStream(exportFile);
				IPreferencesService service = Platform.getPreferencesService();
				// service.importPreferences(fis);
				IExportedPreferences prefs = service.readPreferences(fis);
				transfers[0] = new IPreferenceFilter() {
					public String[] getScopes() {
						return new String[] { InstanceScope.SCOPE };
					}

					public Map<String, PreferenceFilterEntry[]> getMapping(
							String scope) {
						Map<String, PreferenceFilterEntry[]> map = new HashMap<String, PreferenceFilterEntry[]>();
						map.put("org.eclipse.ui.workbench",
								new PreferenceFilterEntry[] { new PreferenceFilterEntry(
										getPerspectiveId() + "_persp") });
						return map;
					}
				};
				service.applyPreferences(prefs, transfers);
			}

		} catch (FileNotFoundException e) {
		} catch (CoreException e) {
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void exportPerspective(final IPerspectiveDescriptor perspective) {

		if (perspective != null) {

			FileOutputStream fos = null;

			try {

				IPreferenceFilter[] transfers = null;

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
										perspective.getId() + "_persp") });
						return map;
					}
				};

				IFile pFile = file.getProject().getFile(
						file.getName().replace(".bmso", ".bmsop"));

				String content = "";

				if (!pFile.exists())
					pFile.create(new ByteArrayInputStream(content.getBytes()),
							true, new NullProgressMonitor());

				File exportFile = new File(pFile.getLocationURI());
				fos = new FileOutputStream(exportFile);
				IPreferencesService service = Platform.getPreferencesService();
				service.exportPreferences(service.getRootNode(), transfers, fos);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (fos != null)
						fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	@Override
	public void dispose() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		if (activePage != null)
			activePage.removePartListener(this);
		super.dispose();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {

		site.getPage().addPartListener(this);

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
			Object obj = xstream.fromXML(inputStream);

			editDomain = new EditDomain();

			importPerspective(file.getProject().getFile(
					getPerspectiveFileName()));
			openPerspective(site.getPage());

			if (obj instanceof Visualization) {

				simulation = new Simulation();

				Visualization visualization = (Visualization) obj;
				visualization.setProjectFile(file);

				VisualizationView visualizationView = new VisualizationView(
						"New Visualization View", visualization);

				String secId = UUID.randomUUID().toString();
				createVisualizationViewPart(secId, editDomain, visualization);

				simulation.getVisualizationViews()
						.put(secId, visualizationView);

			} else if (obj instanceof Simulation) {
				simulation = (Simulation) obj;
			}

			if (simulation != null) {

				for (Map.Entry<String, VisualizationView> entry : simulation
						.getVisualizationViews().entrySet()) {

					String secId = entry.getKey();
					VisualizationView visView = entry.getValue();
					Visualization vis = visView.getVisualization();
					vis.setProjectFile(file);
					// String partName = visView.getPartName();
					IViewReference viewReference = site.getPage()
							.findViewReference(
							VisualizationViewPart.ID, secId);
					// Check if view already exists
					if (viewReference != null) {
						VisualizationViewPart visualizationView = (VisualizationViewPart) viewReference
								.getView(false);
						if (visualizationView != null) {
							visualizationView.initGraphicalViewer(editDomain,
									vis);
						} else {
							//TODO return some error!
						}
					} else {
						// If not, create a new one
						createVisualizationViewPart(secId, editDomain, vis);
					}

				}

			}

		} catch (CoreException e) {
			e.printStackTrace();
		}

		setSite(site);
		setInput(input);

	}

	private VisualizationViewPart createVisualizationViewPart(String secId,
			EditDomain editDomain, Visualization visualization)
			throws PartInitException {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();
		VisualizationViewPart visualizationView = (VisualizationViewPart) activePage
				.showView(VisualizationViewPart.ID, secId,
						IWorkbenchPage.VIEW_VISIBLE);
		visualizationView.initGraphicalViewer(editDomain, visualization);
		return visualizationView;
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

	public void setDirty(boolean dirty) {
		if (isDirty() != dirty) {
			isDirty = dirty;
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	@Override
	public void doSave(final IProgressMonitor monitor) {

		exportPerspective(perspective);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			// saveProperties();
			OutputStreamWriter writer = new OutputStreamWriter(out, "UTF8");
			XStream xstream = new XStream();
			BMotionEditorPlugin.setAliases(xstream);
			xstream.toXML(simulation, writer);
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			file.setContents(new ByteArrayInputStream(out.toByteArray()), true,
					false, monitor);
			getCommandStack().markSaveLocation();
		} catch (CoreException ce) {
			ce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

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
		return isDirty;
	}

	@Override
	public void createPartControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout());
		Button button = new Button(container, SWT.PUSH);
		button.setText("Add new Visualization View");
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					
					String secId = UUID.randomUUID().toString();
					// Create a new visualization
					String version = Platform
							.getBundle(BMotionEditorPlugin.PLUGIN_ID)
							.getHeaders().get("Bundle-Version");
					Visualization visualization = new Visualization(
							"RushHour.bmso",
							"EventB", version);
					
					createVisualizationViewPart(secId, editDomain,
							visualization);

					VisualizationView visualizationView = new VisualizationView(
							"New Visulization View", visualization);
					simulation.getVisualizationViews().put(secId,
							visualizationView);

					setDirty(true);

				} catch (PartInitException e1) {
					e1.printStackTrace();
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});
	}

	@Override
	public void setFocus() {
	}

	public CommandStack getCommandStack() {
		return getEditDomain().getCommandStack();
	}

	protected EditDomain getEditDomain() {
		return editDomain;
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		IWorkbenchPart part = partRef.getPart(false);
		if (part == this)
			openPerspective(partRef.getPage());
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		if (partRef.getPart(false) == this) {
			partRef.getPage().savePerspectiveAs(perspective);
			exportPerspective(perspective);
			closePerspective(partRef.getPage(), perspective);
			deletePerspective(partRef.getPage(), perspective);
		}
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
	}

}
