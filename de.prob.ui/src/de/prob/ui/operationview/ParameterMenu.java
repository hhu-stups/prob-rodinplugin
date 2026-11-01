package de.prob.ui.operationview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;

import de.prob.core.Animator;
import de.prob.core.domainobjects.Operation;

public class ParameterMenu extends ExtensionContributionFactory {

	// maximum lengths of a single assignment and of a whole menu entry for
	// non-deterministic choices
	private static final int ASSIGNMENT_LENGTH = 25;
	private static final int LABEL_LENGTH = 60;

	private Map<Operation, String> labels;

	public ParameterMenu() {
	}

	@Override
	public void createContributionItems(final IServiceLocator serviceLocator,
			final IContributionRoot additions) {

		List<Operation> operations = OperationTableViewer.getInstance()
				.getSelectedOperations();

		if (operations.isEmpty())
			return;

		labels = createLabels(operations);

		if (operations.size()<=10) {
			// if number of operations small: show enabled operations first
			for (Operation operation : operations) {
				long id = operation.getId();
				additions.addContributionItem(
						createEntry(serviceLocator, operation, id), null);

			}
		}

		CommandContributionItemParameter contributionParameters = new CommandContributionItemParameter(
				serviceLocator, "", "de.prob.ui.show_parameter_dialog",
				SWT.PUSH);
		contributionParameters.label = "Show Parameter Dialog ...";
		CommandContributionItem dialogItem = new CommandContributionItem(
				contributionParameters);
		additions.addContributionItem(dialogItem, null);

		CommandContributionItemParameter contributionCustomGuard = new CommandContributionItemParameter(
				serviceLocator, "",
				"de.prob.ui.show_custom_precondition_dialog", SWT.PUSH);
		contributionCustomGuard.label = "Execute with additional Guard Constraint ...";
		CommandContributionItem customGuardDialogItem = new CommandContributionItem(
				contributionCustomGuard);
		additions.addContributionItem(customGuardDialogItem, null);

		if (operations.size()>10) {
			// if number of operations large: show enabled operations last
			// TODO: nested view according to parameters or group entries together into submenus of 20 operations or so
			for (Operation operation : operations) {
				long id = operation.getId();
				additions.addContributionItem(
						createEntry(serviceLocator, operation, id), null);

			}
		}

	}

	private CommandContributionItem createEntry(
			final IServiceLocator serviceLocator, final Operation op,
			final long id) {

		final String itemText = labels.get(op);

		CommandContributionItemParameter p = new CommandContributionItemParameter(
				serviceLocator, "", "de.prob.ui.execute_event", SWT.PUSH);
		p.label = itemText;

		HashMap<String, String> map = new HashMap<String, String>(1);
		map.put("de.prob.ui.operation", Long.toString(id));
		p.parameters = map;

		CommandContributionItem item = new CommandContributionItem(p);
		item.setVisible(true);
		return item;
	}

	private static Map<Operation, String> createLabels(
			final List<Operation> operations) {
		// operations without parameters (non-deterministic assignments) are
		// described by the variables they change
		final Map<Operation, String> descriptions = NondeterministicChoices
				.describe(Animator.getAnimator(), operations,
						ASSIGNMENT_LENGTH, LABEL_LENGTH);
		final List<Operation> choices = new ArrayList<Operation>();
		final List<String> choiceLabels = new ArrayList<String>();
		final Map<Operation, String> labels = new HashMap<Operation, String>();
		for (Operation op : operations) {
			if (NondeterministicChoices.isWithoutParameters(op)) {
				choices.add(op);
				final String description = descriptions.get(op);
				choiceLabels.add(description != null ? description
						: "Non-deterministic choice");
			} else {
				labels.put(op, OperationTableViewer.convertParamsToString(15,
						25, op.getArguments()));
			}
		}
		// truncated descriptions may coincide; number those
		NondeterministicChoices.makeUnique(choiceLabels);
		for (int i = 0; i < choices.size(); i++) {
			labels.put(choices.get(i), choiceLabels.get(i));
		}
		return labels;
	}
}
