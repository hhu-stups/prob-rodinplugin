package de.prob.eventb.disprover.ui.export;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProverSequent;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.rodinp.core.RodinDBException;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.AEventBContextParseUnit;
import de.prob.eventb.disprover.core.translation.DisproverContextCreator;
import de.prob.eventb.translator.internal.TranslationVisitor;
import de.prob.logging.Logger;
import de.prob.prolog.output.PrologTermOutput;

public class ExportPOsHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IEventBRoot root = getSelectedEventBRoot(event);
		if (root != null) {
			final Preferences prefs = Platform.getPreferencesService()
					.getRootNode().node(InstanceScope.SCOPE)
					.node("prob_export_preferences");
			final Shell shell = HandlerUtil.getActiveShell(event);
			final String fileName = askForExportFile(prefs, shell, root);
			if (fileName != null) {
				exportPOs(fileName, root);
			}
		}
		return null;
	}

	private IEventBRoot getSelectedEventBRoot(final ExecutionEvent event) {
		final ISelection fSelection = HandlerUtil.getCurrentSelection(event);
		IEventBRoot root = null;
		if (fSelection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) fSelection;
			if (ssel.size() == 1
					&& ssel.getFirstElement() instanceof IEventBRoot) {
				root = (IEventBRoot) ssel.getFirstElement();
			}
		}
		return root;
	}

	private String askForExportFile(final Preferences prefs, final Shell shell,
			final IEventBRoot root) {
		final String path = prefs.get("dir", System.getProperty("user.home"));

		final FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterExtensions(new String[] { "*.pl" });

		dialog.setFilterPath(path);
		final String subext = (root instanceof IMachineRoot) ? "_mch" : "_ctx";
		dialog.setFileName(root.getComponentName() + subext + ".pl");
		String result = dialog.open();
		if (result != null) {
			final String newPath = dialog.getFilterPath();
			if (!path.equals(newPath)) {
				prefs.put("dir", newPath);
				try {
					prefs.flush();
				} catch (BackingStoreException e) {
					// Ignore, if preferences are not stored correctly we simply
					// ignore it (annoying, but not critical)
				}
			}
			if (!result.endsWith(".pl")) {
				result += ".pl";
			}
		}
		return result;
	}

	public static void exportPOs(final String filename, final IEventBRoot root) {
		final boolean isSafeToWrite = isSafeToWrite(filename);

		if (isSafeToWrite) {
			PrintWriter fw = null;
			try {
				fw = new PrintWriter(filename);
				if (root instanceof IContextRoot) {
					IContextRoot croot = (IContextRoot) root;
					IPORoot poRoot = croot.getSCContextRoot().getPORoot();
					IPSRoot psRoot = croot.getSCContextRoot().getPSRoot();
					exportPOs(fw, poRoot, psRoot);
				} else if (root instanceof IMachineRoot) {
					IMachineRoot croot = (IMachineRoot) root;
					IPORoot poRoot = croot.getSCMachineRoot().getPORoot();
					IPSRoot psRoot = croot.getSCMachineRoot().getPSRoot();
					exportPOs(fw, poRoot, psRoot);
				} else {
					throw new IllegalArgumentException(
							"Not a Context or Machine root.");
				}
				fw.append('\n');

			} catch (IllegalArgumentException e) {
				Logger.notifyUser(e.getMessage());
			} catch (IOException e) {
				Logger.notifyUser("Unable to create file '" + filename + "'");
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (fw != null) {
					fw.close();
				}
			}
		}
	}

	private static void exportPOs(PrintWriter fw, IPORoot poRoot, IPSRoot psRoot)
			throws RodinDBException {
		PrologTermOutput pto = new PrologTermOutput(fw, false);
		ASTProlog modelAst = new ASTProlog(pto, null);
		TranslationVisitor tVisitor = new TranslationVisitor();

		Date date = new Date();
		pto.openTerm("generated");
		pto.printNumber(date.getTime());
		pto.printAtom(date.toString());
		pto.closeTerm();
		pto.fullstop();

		pto.openTerm("machine_name");
		pto.printAtom(poRoot.getElementName());
		pto.closeTerm();
		pto.fullstop();

		IPOSequent[] sequents = poRoot.getSequents();
		for (IPOSequent ipoSequent : sequents) {
			IProverSequent proverSequent = toProverSequent(ipoSequent);
			AEventBContextParseUnit disproverContext = DisproverContextCreator
					.createDisproverContext(proverSequent);

			// disprover_po(Name,Context,Goal,Hyps,SelectedHyps)
			pto.openTerm("disprover_po");

			pto.printAtom(ipoSequent.getElementName());

			disproverContext.apply(modelAst);

			proverSequent.goal().accept(tVisitor);
			tVisitor.getPredicate().apply(modelAst);

			pto.openList();
			for (Predicate hyp : proverSequent.hypIterable()) {
				hyp.accept(tVisitor);
				tVisitor.getPredicate().apply(modelAst);
			}
			pto.closeList();

			pto.openList();
			for (Predicate hyp : proverSequent.selectedHypIterable()) {
				hyp.accept(tVisitor);
				tVisitor.getPredicate().apply(modelAst);
			}
			pto.closeList();

			// The result: true = proven in rodin, unknown else
			IPSStatus status = psRoot.getStatus(ipoSequent.getElementName());
			if (status.getConfidence() == IConfidence.DISCHARGED_MAX) {
				pto.printAtom("true");
			} else {
				pto.printAtom("unknown");
			}

			pto.closeTerm();
			pto.fullstop();
		}
	}

	public static IProverSequent toProverSequent(IPOSequent sequent)
			throws RodinDBException {
		IPORoot poRoot = (IPORoot) sequent.getRoot();
		IProofManager pm = EventBPlugin.getProofManager();
		IProofComponent pc = pm.getProofComponent(poRoot);
		IProofAttempt pa = pc.createProofAttempt(sequent.getElementName(),
				"ProB PO Export", null);

		IProofTree proofTree = pa.getProofTree();

		IProverSequent proverSequent = proofTree.getSequent();
		pa.dispose();

		return proverSequent;
	}

	private static boolean isSafeToWrite(final String filename) {
		if (new File(filename).exists()) {
			final MessageDialog dialog = new MessageDialog(null, "File exists",
					null, "The file exists. Do you want to overwrite it?",
					MessageDialog.QUESTION, new String[] { "Yes", "No" }, 0);
			return dialog.open() == 0;
		} else
			return true;
	}
}
