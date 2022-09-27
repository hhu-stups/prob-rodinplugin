/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.EditorImageRegistry;


public abstract class ProgressBarDialog extends Dialog {

	private Label processMessageLabel; // info of process finish
	private Button cancelButton; // cancel button
	private Composite cancelComposite;
	private Composite progressBarComposite;//
	private CLabel message;//
	private ProgressBar progressBar = null; //

	private Shell shell; //

	public Shell getShell() {
		return shell;
	}

	private Display display = null;

	protected volatile boolean isClosed = false;// closed state

	protected int executeTime = 50;// process times
	protected String processMessage = "process......";// procress info
	protected String shellTitle = "Progress..."; //
	protected Image processImage = BMotionStudioImage
			.getImage(EditorImageRegistry.IMG_ICON_LOADING);// image
	protected boolean mayCancel = true; // cancel
	protected int processBarStyle = SWT.SMOOTH; // process bar style

	protected ProcessThread currentThread;

	public void setMayCancel(boolean mayCancel) {
		this.mayCancel = mayCancel;
	}

	public void setExecuteTime(int executeTime) {
		this.executeTime = executeTime;
	}

	public void setProcessImage(Image processImage) {
		this.processImage = processImage;
	}

	public void setProcessMessage(String processInfo) {
		this.processMessage = processInfo;
	}

	public ProgressBarDialog(Shell parent) {
		super(parent);
	}

	protected ProcessThread getCurrentThread() {
		return this.currentThread;
	}

	public abstract void initGuage();

	public void open() {
		createContents(); // create window
		shell.open();
		shell.layout();

		// start work
		currentThread = new ProcessThread(executeTime);
		currentThread.start();

		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	protected void setClose(boolean b) {
		this.isClosed = b;
	}

	protected void createContents() {
		shell = new Shell(getParent(), SWT.TITLE | SWT.PRIMARY_MODAL
				| SWT.NO_TRIM | SWT.ON_TOP);
		display = shell.getDisplay();
		FormLayout fl = new FormLayout();

		shell.setLayout(fl);
		shell.setSize(483, 350);
		shell.setText(shellTitle);
		shell.setBackgroundImage(BMotionStudioImage
				.getImage(EditorImageRegistry.IMG_SPLASH));

		Monitor primary = Display.getCurrent().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);

		FormData fd = new FormData();
		fd.right = new FormAttachment(100, -5);
		fd.bottom = new FormAttachment(100, -10);

		// Cancel button
		cancelComposite = new Composite(shell, SWT.NONE);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		cancelComposite.setLayout(gridLayout_1);
		cancelComposite.setLayoutData(fd);
		cancelComposite.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));

		cancelButton = new Button(cancelComposite, SWT.NONE);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				isClosed = true;
			}
		});
		cancelButton.setLayoutData(new GridData(78, SWT.DEFAULT));
		cancelButton.setText("Cancel");
		cancelButton.setEnabled(this.mayCancel);

		fd = new FormData();
		fd.right = new FormAttachment(100, -5);
		fd.left = new FormAttachment(0, 10);
		fd.bottom = new FormAttachment(cancelComposite, -5);

		processMessageLabel = new Label(shell, SWT.NONE);
		processMessageLabel.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));
		processMessageLabel.setLayoutData(fd);

		fd = new FormData();
		fd.right = new FormAttachment(100, -10);
		fd.left = new FormAttachment(0, 10);
		fd.bottom = new FormAttachment(processMessageLabel, -5);

		progressBarComposite = new Composite(shell, SWT.NONE);
		progressBarComposite.setLayout(new FillLayout());
		progressBarComposite.setLayoutData(fd);
		progressBar = new ProgressBar(progressBarComposite, processBarStyle);
		progressBar.setMaximum(executeTime);

		fd = new FormData();
		fd.right = new FormAttachment(100, -5);
		fd.left = new FormAttachment(0, 10);
		fd.bottom = new FormAttachment(progressBarComposite, -5);

		// Message label
		message = new CLabel(shell, SWT.NONE);
		message.setLayoutData(fd);
		message.setImage(processImage);
		message.setText(processMessage);
		message.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));
	}

	protected abstract String process(int times);

	protected void cleanUp() {
	}

	protected void doBefore() {
	}

	protected void doAfter() {
	}

	class ProcessThread extends Thread {
		private int max = 0;
		private volatile boolean shouldStop = false;

		ProcessThread(int max) {
			this.max = max;
		}

		public void run() {
			doBefore();
			for (final int[] i = new int[] { 1 }; i[0] <= max; i[0]++) {
				final String info = process(i[0]);
				if (display.isDisposed()) {
					return;
				}
				display.syncExec(new Runnable() {
					public void run() {
						if (progressBar.isDisposed()) {
							return;
						}
						processMessageLabel.setText(info);
						progressBar.setSelection(i[0]);
						if (i[0] == max || isClosed) {
							if (isClosed) {
								shouldStop = true;
								cleanUp();
							}
							shell.close();
						}
					}
				});
				if (shouldStop)
					break;
			}
			doAfter();
		}
	}

	public void setShellTitle(String shellTitle) {
		this.shellTitle = shellTitle;
	}

	public void setProcessBarStyle(boolean pStyle) {
		if (pStyle)
			this.processBarStyle = SWT.SMOOTH;
		else
			this.processBarStyle = SWT.NONE;
	}

}
