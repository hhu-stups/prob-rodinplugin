package de.prob.ui.historyview;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eventb.core.IEventBRoot;

import de.prob.core.Animator;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.langdep.EventBAnimatorPart;
import de.prob.ui.explorer.TraceFiles;
import de.prob.ui.explorer.TraceStatus;
import de.prob.ui.explorer.TracesLabelProvider;

/**
 * The saved traces of the animated machine or context in the drop-down menu
 * of the History view's Replay Trace button, each with the icon of its
 * replay status; choosing one replays it. The menu's first entry, "Other
 * Trace...", chooses a file instead.
 */
public class ReplayTraceMenu extends CompoundContributionItem {

	@Override
	protected IContributionItem[] getContributionItems() {
		final Animator animator = Animator.getAnimator();
		final LanguageDependendAnimationPart ldp = animator
				.getLanguageDependendPart();
		if (!animator.isMachineLoaded() || !(ldp instanceof EventBAnimatorPart))
			return new IContributionItem[0];
		final IEventBRoot root = ((EventBAnimatorPart) ldp).getRoot();
		final List<IFile> traces = TraceFiles.of(root);
		if (traces.isEmpty()) {
			final Action none = new Action("No saved traces of "
					+ root.getComponentName()) {
			};
			none.setEnabled(false);
			return new IContributionItem[] { new ActionContributionItem(none) };
		}
		final IContributionItem[] items = new IContributionItem[traces.size()];
		for (int i = 0; i < items.length; i++)
			items[i] = new ActionContributionItem(replayAction(root,
					traces.get(i)));
		return items;
	}

	private static Action replayAction(final IEventBRoot root,
			final IFile trace) {
		final Action action = new Action(trace.getName()) {
			@Override
			public void run() {
				final IWorkbenchWindow window = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				ReplayTraceHandler.replay(window == null ? null : window
						.getShell(), historyStatusLine(window), trace
						.getLocation().toFile(), root);
			}
		};
		action.setImageDescriptor(TracesLabelProvider
				.descriptor(TraceStatus.get(trace, root)));
		final String detail = TraceStatus.getDetail(trace);
		if (detail != null)
			action.setToolTipText(detail);
		return action;
	}

	/** the History view's status line, for the result of a perfect replay */
	private static IStatusLineManager historyStatusLine(
			final IWorkbenchWindow window) {
		final IWorkbenchPage page = window == null ? null : window
				.getActivePage();
		final IViewPart view = page == null ? null : page
				.findView(HistoryView.VIEW_ID);
		return view == null ? null : SaveTraceHandler.statusLine(view);
	}
}
