package de.prob.eventb.disprover.ui;

import java.io.*;
import java.util.*;

import org.eclipse.core.commands.*;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.*;
import org.eventb.core.ast.*;
import org.eventb.core.seqprover.IConfidence;
import org.osgi.service.prefs.*;
import org.rodinp.core.RodinDBException;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.prob.core.translator.TranslationFailedException;
import de.prob.eventb.translator.*;
import de.prob.eventb.translator.internal.EProofStatus;
import de.prob.logging.Logger;
import de.prob.prolog.output.*;

public class ExportAsTestHandler extends AbstractHandler implements IHandler {

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
				StringBuffer sb = toClassic(root);
				appendDisproverTestCases(sb, root);
				dumpStringBufferToFile(sb, fileName);
			}
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

	private void appendDisproverTestCases(StringBuffer export, IEventBRoot root) {
		StringWriter st = new StringWriter();
		PrintWriter pw = new PrintWriter(st);
		PrologTermOutput pto = new PrologTermOutput(pw, false);

		try {
			FormulaFactory factory = root.getFormulaFactory();

			// itereate through all proof obligations and generate
			// disprover_test(Goal,Hypotheses,ExpectedResult)
			// terms
			IPSRoot proofStatus = root.getPSRoot();
			IPSStatus[] statuses = proofStatus.getStatuses();

			for (IPSStatus status : statuses) {
				final int confidence = status.getConfidence();
				boolean broken = status.isBroken();

				EProofStatus pstatus = EProofStatus.UNPROVEN;

				if (!broken && confidence == IConfidence.DISCHARGED_MAX)
					pstatus = EProofStatus.PROVEN;

				IPOSequent sequent = status.getPOSequent();
				IPOPredicate[] goals = sequent.getGoals();
				IPOPredicateSet[] hypotheses = sequent.getHypotheses();

				// collect all hypotheses in one set, which will be send to
				// prolog
				// TODO: investigate, whether there are hypotheses not
				// belonging to this PO
				Set<IPOPredicate> allHypotheses = new HashSet<IPOPredicate>();
				for (IPOPredicateSet s : hypotheses) {
					allHypotheses.addAll(Arrays.asList(s.getPredicates()));
				}

				// can there be several goals?
				assert (goals.length == 1);

				pto.openTerm("disprover_test");
				predicateToProlog(pto, goals[0].getPredicate(factory));

				pto.openList();
				for (IPOPredicate p : allHypotheses) {
					predicateToProlog(pto, p.getPredicate(factory));
				}
				pto.closeList();

				if (pstatus == EProofStatus.PROVEN) {
					pto.printAtom("contradiction_found");
				} else {
					pto.printAtom("unknown");
				}

				pto.closeTerm();
			}

		} catch (RodinDBException e) {
			Logger.notifyUser("A RodinDBException occured when trying to access the proof obligations");
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

		// add new testcase terms to the additional information list
		int index = export.indexOf("],_Error)).");
		export.insert(index, "," + st.toString());
	}

	private void predicateToProlog(IPrologTermOutput pto, Predicate pred) {
		PredicateVisitor v = new PredicateVisitor();
		pred.accept(v);
		ASTProlog p = new ASTProlog(pto, null);
		v.getPredicate().apply(p);
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
		dialog.setFilterExtensions(new String[] { "*.eventb" });

		dialog.setFilterPath(path);
		final String subext = (root instanceof IMachineRoot) ? "_mch" : "_ctx";
		dialog.setFileName(root.getComponentName() + subext + ".eventb");
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

	public static StringBuffer toClassic(final IEventBRoot root) {
		StringWriter st = new StringWriter();
		PrintWriter pw = new PrintWriter(st);

		try {
			TranslatorFactory.translate(root, pw);

		} catch (TranslationFailedException e) {
			e.notifyUserOnce();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

		return st.getBuffer();

	}

	// private static String serialize(Project project, String maincomponent) {
	// NewCoreModelTranslation translation = new NewCoreModelTranslation();
	// Model model = translation.translate(project, maincomponent);
	// // XStream xstream = new XStream(new JettisonMappedXmlDriver());
	// XStream xstream = new XStream();
	// String xml = xstream.toXML(model);
	// ByteArrayOutputStream out = new ByteArrayOutputStream();
	// GZIPOutputStream gzip;
	// byte[] bytes;
	// try {
	// gzip = new GZIPOutputStream(out);
	// gzip.write(xml.getBytes());
	// gzip.close();
	// bytes = out.toByteArray();
	// } catch (IOException e) {
	// bytes = xml.getBytes();
	// }
	// return Base64.encodeBase64String(bytes);
	// }

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
