package de.prob.ui.operationview;

import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
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
		contributionCustomGuard.label = "Execute with additional Guard ...";
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
