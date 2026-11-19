package de.prob.ui.explorer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesMachine;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import de.prob.ui.historyview.SaveTraceHandler;

/**
 * The "Copy Trace To" sub-menu of a trace in the Event-B Explorer: the
 * machines refining the trace's machine (or the contexts extending its
 * context), directly or indirectly, and "Other Model..." to choose any
 * machine or context of the project. The copy is put in the chosen model's
 * folder as ModelName_Nr.prob2trace, so it can be replayed for that model;
 * it has no replay status yet.
 */
public class CopyTraceMenu extends CompoundContributionItem {

	private static final String TITLE = "Copy Trace";

	@Override
	protected IContributionItem[] getContributionItems() {
		final IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		final TraceNode trace = selectedTrace(window);
		if (trace == null)
			return new IContributionItem[0];
		final IEventBRoot source = trace.getParent().getRoot();
		final List<IContributionItem> items = new ArrayList<IContributionItem>();
		final List<IEventBRoot> refinements = refinements(source);
		for (IEventBRoot target : refinements)
			items.add(new ActionContributionItem(copyAction(window, trace,
					target, target.getComponentName())));
		if (refinements.isEmpty()) {
			final Action none = new Action(
					source instanceof IContextRoot ? "No extending contexts"
							: "No refinements") {
			};
			none.setEnabled(false);
			items.add(new ActionContributionItem(none));
		}
		items.add(new Separator());
		items.add(new ActionContributionItem(new Action("Other Model...") {
			@Override
			public void run() {
				final IEventBRoot target = chooseModel(window.getShell(),
						source);
				if (target != null)
					copy(window, trace, target);
			}
		}));
		return items.toArray(new IContributionItem[items.size()]);
	}

	private static TraceNode selectedTrace(final IWorkbenchWindow window) {
		if (window == null)
			return null;
		final ISelection selection = window.getSelectionService()
				.getSelection();
		if (!(selection instanceof IStructuredSelection)
				|| ((IStructuredSelection) selection).size() != 1)
			return null;
		final Object element = ((IStructuredSelection) selection)
				.getFirstElement();
		return element instanceof TraceNode ? (TraceNode) element : null;
	}

	private static Action copyAction(final IWorkbenchWindow window,
			final TraceNode trace, final IEventBRoot target, final String label) {
		return new Action(label) {
			@Override
			public void run() {
				copy(window, trace, target);
			}
		};
	}

	/**
	 * @return the machines refining the machine, or the contexts extending
	 *         the context, in the same project: direct ones first, then
	 *         those refining these, and so on
	 */
	static List<IEventBRoot> refinements(final IEventBRoot source) {
		final List<IEventBRoot> all = models(source.getRodinProject());
		final Set<IEventBRoot> result = new LinkedHashSet<IEventBRoot>();
		List<IEventBRoot> level = new ArrayList<IEventBRoot>();
		level.add(source);
		while (!level.isEmpty()) {
			final List<IEventBRoot> next = new ArrayList<IEventBRoot>();
			for (IEventBRoot model : all)
				if (!model.equals(source) && !result.contains(model)
						&& refinesAny(model, level)) {
					result.add(model);
					next.add(model);
				}
			level = next;
		}
		return new ArrayList<IEventBRoot>(result);
	}

	/** whether the model directly refines or extends one of the others */
	private static boolean refinesAny(final IEventBRoot model,
			final List<IEventBRoot> others) {
		final Set<String> names = new LinkedHashSet<String>();
		for (IEventBRoot other : others)
			names.add(other.getComponentName());
		try {
			if (model instanceof IMachineRoot && others.get(0) instanceof IMachineRoot) {
				for (IRefinesMachine refines : ((IMachineRoot) model)
						.getRefinesClauses())
					if (refines.hasAbstractMachineName()
							&& names.contains(refines.getAbstractMachineName()))
						return true;
			} else if (model instanceof IContextRoot
					&& others.get(0) instanceof IContextRoot) {
				for (IExtendsContext ext : ((IContextRoot) model)
						.getExtendsClauses())
					if (ext.hasAbstractContextName()
							&& names.contains(ext.getAbstractContextName()))
						return true;
			}
		} catch (RodinDBException e) {
			// not listed then
		}
		return false;
	}

	/** @return the machines and then the contexts of the project, by name */
	private static List<IEventBRoot> models(final IRodinProject project) {
		final List<IEventBRoot> result = new ArrayList<IEventBRoot>();
		try {
			final List<IEventBRoot> machines = new ArrayList<IEventBRoot>();
			for (IMachineRoot machine : project
					.getRootElementsOfType(IMachineRoot.ELEMENT_TYPE))
				machines.add(machine);
			final List<IEventBRoot> contexts = new ArrayList<IEventBRoot>();
			for (IContextRoot context : project
					.getRootElementsOfType(IContextRoot.ELEMENT_TYPE))
				contexts.add(context);
			sortByName(machines);
			sortByName(contexts);
			result.addAll(machines);
			result.addAll(contexts);
		} catch (RodinDBException e) {
			// no models then
		}
		return result;
	}

	private static void sortByName(final List<IEventBRoot> models) {
		Collections.sort(models, new Comparator<IEventBRoot>() {
			@Override
			public int compare(final IEventBRoot a, final IEventBRoot b) {
				return a.getComponentName().compareToIgnoreCase(
						b.getComponentName());
			}
		});
	}

	/** @return the machine or context chosen in a dialog, or null */
	private static IEventBRoot chooseModel(final Shell shell,
			final IEventBRoot source) {
		final List<IEventBRoot> models = models(source.getRodinProject());
		models.remove(source);
		if (models.isEmpty()) {
			MessageDialog.openInformation(shell, TITLE,
					"The project has no other machine or context.");
			return null;
		}
		final ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				shell, new LabelProvider() {
					@Override
					public String getText(final Object element) {
						final IEventBRoot model = (IEventBRoot) element;
						return model.getComponentName()
								+ (model instanceof IMachineRoot ? " (machine)"
										: " (context)");
					}
				});
		dialog.setTitle(TITLE);
		dialog.setMessage("Copy the trace to the machine or context:");
		dialog.setElements(models.toArray());
		dialog.setMultipleSelection(false);
		if (dialog.open() != Window.OK || dialog.getFirstResult() == null)
			return null;
		return (IEventBRoot) dialog.getFirstResult();
	}

	/**
	 * copies the trace to the target's folder and shows the copy in the
	 * Explorer
	 */
	static void copy(final IWorkbenchWindow window, final TraceNode trace,
			final IEventBRoot target) {
		final IFolder folder = TraceFiles.folder(target);
		final IFile copy;
		try {
			if (!folder.exists())
				folder.create(true, true, null);
			copy = TraceFiles.nextFreeFile(target);
			trace.getFile().copy(copy.getFullPath(), true, null);
			// a copy may carry the original's status, not valid for the target
			TraceStatus.clear(copy);
		} catch (CoreException e) {
			MessageDialog.openError(window.getShell(), TITLE,
					"Could not copy " + trace.getFile().getFullPath() + " to "
							+ folder.getFullPath() + ": " + e.getMessage());
			return;
		}
		// queued after the Explorer's refresh for the new file
		window.getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				SaveTraceHandler.revealInExplorer(window, target, copy);
			}
		});
	}
}
