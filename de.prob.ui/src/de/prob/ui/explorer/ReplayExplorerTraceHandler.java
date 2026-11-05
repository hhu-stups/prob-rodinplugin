package de.prob.ui.explorer;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IEventBRoot;

import de.prob.core.Animator;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.langdep.EventBAnimatorPart;
import de.prob.exceptions.ProBException;
import de.prob.ui.eventb.StartAnimationHandler;
import de.prob.ui.historyview.ReplayTraceHandler;

/**
 * Replays the trace selected in the Event-B Explorer. If its machine or
 * context is not being animated, or was changed, the animation is started
 * first.
 */
public class ReplayExplorerTraceHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final TraceNode trace = selectedTrace(event);
		if (trace != null)
			replay(HandlerUtil.getActiveShell(event), trace);
		return null;
	}

	/**
	 * Replays a trace of the Explorer (from its right-click menu or by a
	 * double-click), starting the animation of its model first if necessary
	 */
	static void replay(final Shell shell, final TraceNode trace) {
		final IEventBRoot root = trace.getParent().getRoot();
		if (!isAnimated(root)) {
			try {
				StartAnimationHandler.startAnimation(root);
			} catch (ProBException e) {
				e.notifyUserOnce();
				return;
			}
		}
		ReplayTraceHandler.replay(shell, null, trace.getFile().getLocation()
				.toFile());
	}

	/**
	 * @return the trace of the Explorer's right-click menu, or
	 *         <code>null</code>
	 */
	static TraceNode selectedTrace(final ExecutionEvent event) {
		// the selection of the tree the context menu belongs to
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection == null)
			selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		final Object element = ((IStructuredSelection) selection)
				.getFirstElement();
		return element instanceof TraceNode ? (TraceNode) element : null;
	}

	/** whether the model is animated, unchanged since it was loaded */
	private static boolean isAnimated(final IEventBRoot root) {
		final Animator animator = Animator.getAnimator();
		final LanguageDependendAnimationPart ldp = animator
				.getLanguageDependendPart();
		return animator.isMachineLoaded() && !animator.isDirty()
				&& ldp instanceof EventBAnimatorPart
				&& root.equals(((EventBAnimatorPart) ldp).getRoot());
	}
}
