package de.prob.ui.operationview;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;

import de.prob.core.Animator;
import de.prob.core.domainobjects.History;
import de.prob.core.domainobjects.HistoryItem;
import de.prob.core.domainobjects.Operation;

public class HistoryForwardMenu extends ExtensionContributionFactory {

	public HistoryForwardMenu() {
	}

	@Override
	public void createContributionItems(final IServiceLocator serviceLocator,
			final IContributionRoot additions) {

		Animator animator = Animator.getAnimator();
		History history = animator.getHistory();
		if (history.isEmpty())
			return;

		Iterator<HistoryItem> iterator = history.iterator();
		String lastItem = "(root)";
		int pos = 0;
		
		int currentPosition = history.getCurrentPosition();

		
		while (iterator.hasNext()) {
			HistoryItem historyItem = iterator.next();
			if (pos > currentPosition)
				additions.addContributionItem(
						createEntry(serviceLocator, lastItem, pos), null);
				Operation operation = historyItem.getOperation();
				lastItem = operation != null ? operation.toString()
						: historyItem.getState().toString();
			pos++;
		}

	}

	private CommandContributionItem createEntry(
			final IServiceLocator serviceLocator, final String lastItem,
			final int pos) {
		CommandContributionItemParameter p = new CommandContributionItemParameter(
				serviceLocator, "", "de.prob.ui.history.forward", SWT.PUSH);

		HashMap<String, String> map = new HashMap<String, String>(1);
		map.put("de.prob.ui.history.pos", Integer.toString(pos));
		p.parameters = map;

		p.label = lastItem;
		CommandContributionItem item = new CommandContributionItem(p);
		item.setVisible(true);
		return item;
	}

}
