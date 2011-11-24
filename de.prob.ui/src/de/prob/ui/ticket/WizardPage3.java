package de.prob.ui.ticket;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class WizardPage3 extends WizardPage {
	private Composite container;
	private Button buttonAdd;
	private Button buttonDelete;
	private Table tableAttachments;
	private int columns;

	public WizardPage3() {
		super("Third Page");
		setTitle("Submit Bugreport");
		setDescription("Add Attachments to Bugreport");
	}

	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout containerLayout = new GridLayout();
		containerLayout.numColumns = 2;
		container.setLayout(containerLayout);

		// Files:
		Label labelFiles = new Label(container, SWT.NULL);
		labelFiles.setText("Files:");

		tableAttachments = new Table(container, SWT.BORDER | SWT.MULTI
				| SWT.FULL_SELECTION);
		tableAttachments.setHeaderVisible(true);
		String[] titles = { "Name", "Description" };
		columns = titles.length;
		for (int i = 0; i < columns; i++) {
			TableColumn column = new TableColumn(tableAttachments, SWT.NONE);
			column.setText(titles[i]); // Header-Caption
			switch (i) {
			case 0:
				column.setWidth(200);
				break;
			case 1:
				column.setWidth(387);
			}
		}

		// Description:

		final TableEditor editor = new TableEditor(tableAttachments);
		// The editor must have the same size as the cell and must
		// not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
		// editing the second column
		final int EDITABLECOLUMN = 1;

		tableAttachments.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				// Clean up any previous editor control
				Control oldEditor = editor.getEditor();
				if (oldEditor != null)
					oldEditor.dispose();

				// Identify the selected row
				TableItem item = (TableItem) e.item;
				if (item == null)
					return;

				// The control that will be the editor must be a child of the
				// Table
				Text newEditor = new Text(tableAttachments, SWT.NONE);
				newEditor.setText(item.getText(EDITABLECOLUMN));
				newEditor.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent me) {
						Text text = (Text) editor.getEditor();
						editor.getItem()
								.setText(EDITABLECOLUMN, text.getText());
						Attachment a = (Attachment) editor.getItem().getData();
						a.setDescription(text.getText());
					}
				});
				newEditor.selectAll();
				newEditor.setFocus();
				editor.setEditor(newEditor, item, EDITABLECOLUMN);
			}
		});

		// Add file:
		buttonAdd = new Button(container, SWT.PUSH);
		buttonAdd.setText("Add file ...");
		buttonAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				IPath path = Platform.getLocation();

				FileDialog dialog = new FileDialog(container.getShell(),
						SWT.MULTI);

				dialog.setText("Add Attachment Files");
				dialog.setFilterPath(path.toOSString());
				dialog.setFilterNames(new String[] { "All Files", "Text Files",
						"Image Files" });
				dialog.setFilterExtensions(new String[] { "*",
						"*.txt;*.pdf;*.doc;*.docx",
						"*.gif;*.png;*.jpg;*.jpeg;*.bmp;*.wmf;*.tiff" });

				if (dialog.open() != null) {
					String[] fileNames = dialog.getFileNames();
					IPath filterPath = new Path(dialog.getFilterPath());
					addAttachmentFiles(filterPath, fileNames);
				}
			}
		});

		// Delete attachment:
		buttonDelete = new Button(container, SWT.PUSH);
		buttonDelete.setText("Delete attachment");
		buttonDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// delete from displayed Table
				tableAttachments.remove(tableAttachments.getSelectionIndices());
			}
		});

		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalSpan = 2;
		tableAttachments.setLayoutData(layoutData);

		// Required to avoid an error in the system
		setControl(container);

		// to enable "Finish"-button not until this page was shown
		setPageComplete(false);
	}

	@Override
	/*
	 * Override setVisible(boolean visible)-Method of DialogPage to be enabled
	 * to set focus at will
	 */
	public void setVisible(boolean visible) {
		container.setVisible(visible);

		// Set Focus
		buttonAdd.setFocus();

		// now that this wizard page has been shown, the "Finish"-button may be
		// enabled
		setPageComplete(true);
	}

	/**
	 * If a duplicate filename is chosen, an iterative number will be appended
	 * to the original filename. Thus the user can differ them in the Wizard
	 * Page shown.
	 * 
	 * @param path
	 * @param fileNames
	 */
	protected void addAttachmentFiles(IPath path, String[] fileNames) {
		// Examples:
		// path: /Users/Marc/Downloads
		// name: motogp.bmp
		// filePath: /Users/Marc/Downloads/motogp.bmp

		for (String name : fileNames) {
			IPath filePath = path.append(name);
			String uniqueFileName = createUniqueFileName(name);
			Attachment a;
			try {
				a = new Attachment(filePath.toOSString(), "");
				a.setFilename(uniqueFileName);

				TableItem item = new TableItem(tableAttachments, SWT.NONE);
				item.setText(0, uniqueFileName);
				item.setText(1, "");
				item.setData(a);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private String createUniqueFileName(String name) {

		StringBuilder uniqueName = new StringBuilder(name);
		int counter = 1;

		for (TableItem item : this.tableAttachments.getItems()) {
			if (item.getText(0).equals(name)) {
				counter++;
			}
		}

		if (counter > 1) {
			uniqueName.insert(uniqueName.lastIndexOf("."), "(" + counter + ")");
		}

		return uniqueName.toString();
	}

	public ArrayList<Attachment> getAttachments() {
		ArrayList<Attachment> attachments = new ArrayList<Attachment>();

		for (int i = 0; i < tableAttachments.getItemCount(); i++) {
			attachments.add((Attachment) tableAttachments.getItem(i).getData());
		}

		return attachments;
	}

}
