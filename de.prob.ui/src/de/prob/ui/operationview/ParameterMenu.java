package de.prob.ui.operationview;

import java.util.*;

import org.eclipse.swt.SWT;
import org.eclipse.ui.menus.*;
import org.eclipse.ui.services.IServiceLocator;

import de.prob.core.domainobjects.Operation;

public class ParameterMenu extends ExtensionContributionFactory {

	private int nondetcounter;

	public ParameterMenu() {
	}

	@Override
	public void createContributionItems(final IServiceLocator serviceLocator,
			final IContributionRoot additions) {

		List<Operation> operations = OperationTableViewer.getInstance()
				.getSelectedOperations();

		if (operations.isEmpty())
			return;

		nondetcounter = 0;

		for (Operation operation : operations) {
			long id = operation.getId();
			additions.addContributionItem(
					createEntry(serviceLocator, operation, id), null);

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
		contributionCustomGuard.label = "Execute with Custom Guard ...";
		CommandContributionItem customGuardDialogItem = new CommandContributionItem(
				contributionCustomGuard);
		additions.addContributionItem(customGuardDialogItem, null);

	}

	private CommandContributionItem createEntry(
			final IServiceLocator serviceLocator, final Operation op,
			final long id) {

		String itemText;
		if (op.getArguments() != null && op.getArguments().size() > 0) {
			itemText = OperationTableViewer.convertParamsToString(15, 25,
					op.getArguments());
		} else {
			itemText = "Non-deterministic choice #" + (++nondetcounter);
		}

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
}
