/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.library;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;

public class LibraryPage extends Page {

	private Image previewImage;

	private TableViewer tvLibrary;

	private Canvas previewCanvas;

	private Composite libMainContainer;

	private Action importImagesAction, deleteItemAction;

	// private BMotionStudioEditor editor;

	public LibraryPage() {
	}

	@Override
	public void createControl(final Composite parent) {

		libMainContainer = new Composite(parent, SWT.NONE);

		GridLayout gl = new GridLayout(1, true);
		gl.horizontalSpacing = 0;

		libMainContainer.setLayout(gl);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalIndent = 0;

		previewCanvas = new Canvas(libMainContainer, SWT.BORDER);
		previewCanvas.addPaintListener(new PaintListener() {
			public void paintControl(final PaintEvent e) {
				if (previewImage == null) {
					e.gc.drawString("No preview...", 0, 0);
				} else {
					e.gc.drawImage(previewImage, 0, 0);
				}
			}
		});
		previewCanvas.setLayoutData(gd);

		Composite libContainer = new Composite(libMainContainer, SWT.NONE);
		libContainer.setLayoutData(gd);
		libContainer.setLayout(new FillLayout());

		tvLibrary = new TableViewer(libContainer, SWT.FULL_SELECTION
				| SWT.V_SCROLL | SWT.MULTI);
		tvLibrary.getTable().setLayoutData(gd);
		tvLibrary.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(final SelectionChangedEvent event) {

				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();

				LibraryObject obj = (LibraryObject) selection.getFirstElement();

				if (previewImage != null) {
					previewImage.dispose();
				}

				if (obj != null) {
					previewImage = obj.getPreview(LibraryPage.this);
				} else {
					previewImage = null;
				}

				previewCanvas.redraw();

				updateActionEnablement();

			}

		});

		tvLibrary.getControl().addKeyListener(new KeyAdapter() {
			public void keyPressed(final KeyEvent event) {
				if (event.character == SWT.DEL && event.stateMask == 0
						&& deleteItemAction.isEnabled()) {
					deleteItemAction.run();
				}
			}
		});

		ObservableListContentProvider contentProvider = new ObservableListContentProvider();
		tvLibrary.setContentProvider(contentProvider);

		tvLibrary.getTable().setLinesVisible(true);
		tvLibrary.getTable().setHeaderVisible(true);

		TableViewerColumn column1 = new TableViewerColumn(tvLibrary, SWT.NONE);
		column1.getColumn().setText("Name");
		column1.getColumn().setWidth(190);
		column1.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				cell.setText(((LibraryObject) cell.getElement()).getName());
				cell.setImage(((LibraryObject) cell.getElement()).getImage());
			}
		});

		TableViewerColumn column2 = new TableViewerColumn(tvLibrary, SWT.NONE);
		column2.getColumn().setText("Type");
		column2.getColumn().setWidth(60);
		column2.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(final ViewerCell cell) {
				cell.setText(((LibraryObject) cell.getElement()).getType());
			}
		});

		createDragAndDropSupport();
		createActions();
		createContextMenu();
		createMenu(getSite());
		refresh();

	}

	private void createDragAndDropSupport() {

		tvLibrary.addDragSupport(DND.DROP_COPY,
				new Transfer[] { AttributeTransfer.getInstance() },
				new DragSourceAdapter() {

					public void dragSetData(final DragSourceEvent event) {
						LibraryObject object = (LibraryObject) ((StructuredSelection) tvLibrary
								.getSelection()).getFirstElement();

						// TODO: Abstract this method!!!
						// String attributeID =
						// AttributeConstants.ATTRIBUTE_IMAGE;
						// if (object.getType().equals("variable")) {
						// attributeID = AttributeConstants.ATTRIBUTE_TEXT;
						// }
						event.data = new AttributeTransferObject(object);
					}

					public void dragStart(final DragSourceEvent event) {
					}

				});

	}

	@Override
	public Control getControl() {
		return libMainContainer;
	}

	@Override
	public void setFocus() {
		libMainContainer.setFocus();
	}

	private void createContextMenu() {
		MenuManager manager = new MenuManager();
		tvLibrary.getControl().setMenu(
				manager.createContextMenu(tvLibrary.getControl()));
		manager.add(deleteItemAction);
	}

	public void refresh() {
		tvLibrary.setInput(new WritableList(getLibraryObjects(),
				LibraryObject.class));
		updateActionEnablement();
	}

	private List<LibraryObject> getLibraryObjects() {

		List<LibraryObject> tmpList = new ArrayList<LibraryObject>();

		// if (editor != null) {

			// TODO Reimplement me!

			// String basePath = (editor.getVisualization().getProjectFile()
			// .getProject().getLocation().toString())
			// .replace("file:", "");
			// File dir = new File(basePath + "/images");
			// File[] fileList = dir.listFiles(new FilenameFilter() {
			// public boolean accept(final File dir, final String name) {
			// if (name.toLowerCase().endsWith(".jpg")
			// || name.toLowerCase().endsWith(".gif")
			// || name.toLowerCase().endsWith(".png")) {
			// return true;
			// }
			// return false;
			// }
			// });
			// if (fileList != null) {
			// for (File f : fileList) {
			// Image img = null;
			// if (f.getName().toLowerCase().endsWith(".jpg")) {
			// img = BMotionStudioImage
			// .getImage(EditorImageRegistry.IMG_ICON_JPG);
			// } else {
			// img = BMotionStudioImage
			// .getImage(EditorImageRegistry.IMG_ICON_GIF);
			// }
			// tmpList.add(new LibraryImageObject(f.getName(), "image",
			// img));
			// }
			// }

			// TODO: Reimplement me!!!
			// Visualization vis = this.editor.getVisualization();
			// if (vis != null) {
			// for (MachineContentObject obj : vis.getVariableList()
			// .getCollection()) {
			// tmpList.add(new LibraryVariableObject(obj.getLabel(),
			// "variable", BMotionStudioImage
			// .getImage(BMotionStudioImage.IMG_LOGO_B)));
			// }
			// }

		// }

		return tmpList;

	}

	private void createMenu(final IPageSite pageSite) {
		pageSite.getActionBars().getToolBarManager().add(importImagesAction);
		pageSite.getActionBars().getToolBarManager().add(deleteItemAction);
	}

	private void createActions() {
		importImagesAction = new ImportImagesAction(this);
		deleteItemAction = new DeleteItemsAction(this);
	}

	private void updateActionEnablement() {
		IStructuredSelection sel = (IStructuredSelection) tvLibrary
				.getSelection();
		deleteItemAction.setEnabled(sel.size() > 0);
	}

	@Override
	public void dispose() {
		if (previewImage != null)
			previewImage.dispose();
		super.dispose();
	}

	// public BMotionStudioEditor getEditor() {
	// return editor;
	// }
	//
	// public void setEditor(final BMotionStudioEditor editor) {
	// this.editor = editor;
	// }

	public TableViewer getTableViewer() {
		return tvLibrary;
	}

	public void setTableViewer(final TableViewer viewer) {
		this.tvLibrary = viewer;
	}

}
