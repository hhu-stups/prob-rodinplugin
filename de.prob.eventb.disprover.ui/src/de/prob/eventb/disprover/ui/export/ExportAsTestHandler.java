package de.prob.eventb.disprover.ui.export;

import java.io.*;

import org.eclipse.core.commands.*;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.*;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.*;
import org.eventb.core.seqprover.*;
import org.osgi.service.prefs.*;
import org.rodinp.core.*;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.AEventBContextParseUnit;
import de.prob.eventb.disprover.core.translation.DisproverContextCreator;
import de.prob.eventb.translator.internal.TranslationVisitor;
import de.prob.logging.Logger;
import de.prob.prolog.output.*;

public class ExportAsTestHandler extends AbstractHandler implements IHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		try {
			final IPOSequent poSequent = getSelectedSequent(event);
			final IProverSequent sequent = toProverSequent(poSequent);

			if (sequent != null) {
				final Preferences prefs = Platform.getPreferencesService()
						.getRootNode().node(InstanceScope.SCOPE)
						.node("prob_export_preferences");
				final Shell shell = HandlerUtil.getActiveShell(event);
				final String fileName = askForExportFile(prefs, shell, sequent);
				if (fileName != null) {
					StringBuffer sb;

					sb = toDisproverMachine(sequent);
					appendDisproverTestCase(sb, sequent, poSequent);
					dumpStringBufferToFile(sb, fileName);
				}
			}
		} catch (RodinDBException e) {
			throw new ExecutionException(e.getMessage());
		}
		return null;
	}

	private void dumpStringBufferToFile(StringBuffer sb, String fileName) {
		final boolean isSafeToWrite = isSafeToWrite(fileName);

		if (isSafeToWrite) {
			PrintWriter out = null;
			try {
				out = new PrintWriter(new FileWriter(fileName));
				out.println(sb.toString());
			} catch (IOException e) {
				Logger.notifyUser("Unable to append to file '" + fileName + "'");
			} finally {
				if (out != null) {
					out.close();
				}
			}
		}

	}

	public static IProverSequent toProverSequent(IPOSequent sequent)
			throws RodinDBException {
		IPORoot poRoot = (IPORoot) sequent.getRoot();
		IProofManager pm = EventBPlugin.getProofManager();
		IProofComponent pc = pm.getProofComponent(poRoot);
		IProofAttempt pa = pc.createProofAttempt(sequent.getElementName(),
				"Disprover Translation", null);
		IProofTree proofTree = pa.getProofTree();

		IProverSequent proverSequent = proofTree.getSequent();
		pa.dispose();

		return proverSequent;
	}

	private void appendDisproverTestCase(StringBuffer export,
			IProverSequent sequent, IPOSequent poSequent)
			throws RodinDBException {

		StringWriter st = new StringWriter();
		PrintWriter pw = new PrintWriter(st);
		PrologTermOutput pto = new PrologTermOutput(pw, false);

		pto.openTerm("disprover_test");
		predicateToProlog(pto, sequent.goal());

		pto.openList();
		for (Predicate p : sequent.hypIterable()) {
			predicateToProlog(pto, p);
		}
		pto.closeList();

		pto.printAtom("unknown");

		pto.closeTerm();

		if (pw != null) {
			pw.close();
		}

		// add new testcase terms to the additional information list
		int index = export.indexOf("],_Error)).");
		export.insert(index, "," + st.toString());
	}

	private void predicateToProlog(IPrologTermOutput pto, Predicate pred) {
		TranslationVisitor v = new TranslationVisitor();
		pred.accept(v);
		ASTProlog p = new ASTProlog(pto, null);
		v.getPredicate().apply(p);
	}

	private IPOSequent getSelectedSequent(final ExecutionEvent event) {
		final ISelection fSelection = HandlerUtil.getCurrentSelection(event);
		IPSStatus status = null;
		if (fSelection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) fSelection;
			if (ssel.size() == 1 && ssel.getFirstElement() instanceof IPSStatus) {
				status = (IPSStatus) ssel.getFirstElement();
			}
		}

		return status.getPOSequent();
	}

	private String askForExportFile(final Preferences prefs, final Shell shell,
			final IProverSequent sequent) {
		final String path = prefs.get("dir", System.getProperty("user.home"));

		final FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterExtensions(new String[] { "*.eventb" });

		dialog.setFilterPath(path);
		dialog.setFileName("disprover_testcase.eventb");
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
			if (!result.endsWith(".eventb")) {
				result += ".eventb";
			}
		}
		return result;
	}

	public static StringBuffer toDisproverMachine(final IProverSequent sequent)
			throws RodinDBException {
		AEventBContextParseUnit context = DisproverContextCreator
				.createDisproverContext(sequent);

		StringWriter st = new StringWriter();
		PrintWriter pw = new PrintWriter(st);
		PrologTermOutput pto = new PrologTermOutput(pw, false);

		pto.openTerm("package");
		pto.openTerm("load_event_b_project");
		pto.emptyList();

		pto.openList();
		ASTProlog modelAst = new ASTProlog(pto, null);
		context.apply(modelAst);
		pto.closeList();

		pto.openList();

		pto.openTerm("exporter_version");
		pto.printNumber(3);
		pto.closeTerm();

		// try to export theories if available
		IPOSequent x = (IPOSequent) sequent.getOrigin();
		IRodinProject project = x.getSources()[0].getRodinProject();

		pto.closeList();

		pto.printVariable("_Error");
		pto.closeTerm();
		pto.closeTerm();

		st.append(".");

		return st.getBuffer();
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
