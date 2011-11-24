package de.prob.ui.stateview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob.core.Animator;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.command.EvaluationInsertFormulaCommand;
import de.prob.core.domainobjects.EvaluationElement;
import de.prob.exceptions.ProBException;
import de.prob.parserbase.ProBParseException;
import de.prob.parserbase.ProBParserBaseAdapter;
import de.prob.prolog.term.PrologTerm;

public class AddFormulaHandler extends AbstractHandler implements IHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final Runnable runnable = new Runnable() {
			public void run() {
				Shell shell = HandlerUtil.getActiveShell(event);
				final String title = StateViewStrings.dialogTitleEnterNewFormula;
				final String msg = StateViewStrings.dialogMessageEnterNewFormula;
				Animator animator = Animator.getAnimator();
				final LanguageDependendAnimationPart ldp = animator
						.getLanguageDependendPart();
				final ProBParserBaseAdapter parser = new ProBParserBaseAdapter(
						ldp);
				final IInputValidator validator = new FormulaValidator(parser);
				InputDialog dialog = new InputDialog(shell, title, msg, "",
						validator);
				int button = dialog.open();
				if (button == InputDialog.OK) {
					final String entered = dialog.getValue();
					PrologTerm parsed = null;
					try {
						parsed = parser.parsePredicate(entered, false);
					} catch (ProBParseException pe) {
						try {
							parsed = parser.parseExpression(entered, false);
						} catch (ProBParseException ee) {
							MessageDialog.openError(shell, title,
									StateViewStrings.dialogSyntaxError);
						}
					}
					if (parsed != null) {
						try {
							final EvaluationElement staticElement = EvaluationInsertFormulaCommand
									.insertFormula(parsed);

							final IWorkbenchPage activePage = PlatformUI
									.getWorkbench().getActiveWorkbenchWindow()
									.getActivePage();
							final StateViewPart view = (StateViewPart) activePage
									.findView(StateViewPart.STATE_VIEW_ID);
							if (view != null) {
								view.addUserDefinedExpression(staticElement);
							} else {
								MessageDialog.openError(shell, "Error",
										"Unable to access state view");
							}
						} catch (ProBException e) {
							e.notifyUserOnce();
						}
					}
				}
			}
		};
		Display.getDefault().asyncExec(runnable);
		return null;
	}

	private static class FormulaValidator implements IInputValidator {
		private final ProBParserBaseAdapter parser;

		public FormulaValidator(final ProBParserBaseAdapter parser) {
			this.parser = parser;
		}

		public String isValid(final String input) {
			String error = null;
			if (input.trim().length() == 0) {
				error = "";
			} else {
				try {
					parser.parsePredicate(input, false);
				} catch (ProBParseException e) {
					final String msg = e.getMessage();
					try {
						parser.parseExpression(input, false);
					} catch (ProBParseException e2) {
						error = msg;
					}
				}
			}
			return error;
		}

	}

}
