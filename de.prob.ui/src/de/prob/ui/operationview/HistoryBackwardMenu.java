package de.prob.ui.operationview;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

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

public class HistoryBackwardMenu extends ExtensionContributionFactory {

	public HistoryBackwardMenu() {
	}

	private final Stack<CommandContributionItem> items = new Stack<CommandContributionItem>();

	@Override
	public void createContributionItems(final IServiceLocator serviceLocator,
			final IContributionRoot additions) {

		Animator animator = Animator.getAnimator();
		History history = animator.getHistory();
		if (history.isEmpty())
			return;

		int pos = 0;
		String lastItem = "(root)";
		Iterator<HistoryItem> iterator = history.iterator();

		int currentPosition = history.getCurrentPosition();
		while (iterator.hasNext()) {
			HistoryItem historyItem = iterator.next();
			if (pos < currentPosition) {
				items.push(createEntry(serviceLocator, lastItem, pos));
				Operation operation = historyItem.getOperation();
				lastItem = operation != null ? operation.toString()
						: historyItem.getState().toString();
			} else
				break;
			pos++;
		}
		while (!items.isEmpty()) {
			additions.addContributionItem(items.pop(), null);
		}
	}

	private CommandContributionItem createEntry(
			final IServiceLocator serviceLocator, final String lastItem,
			final int pos) {
		CommandContributionItemParameter p = new CommandContributionItemParameter(
				serviceLocator, "", "de.prob.ui.history.back", SWT.PUSH);

		HashMap<String, String> map = new HashMap<String, String>(1);
		map.put("de.prob.ui.history.pos", Integer.toString(pos));
		p.parameters = map;

		p.label = lastItem;
		CommandContributionItem item = new CommandContributionItem(p);
		item.setVisible(true);
		return item;
	}

}
