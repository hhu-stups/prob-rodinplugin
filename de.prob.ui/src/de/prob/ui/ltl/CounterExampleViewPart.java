package de.prob.ui.ltl;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.services.ISourceProviderService;

import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.logging.Logger;
import de.prob.ui.StateBasedViewPart;

/***
 * Provides a view for a counter-example
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExampleViewPart extends StateBasedViewPart {
	private static final String ID = "de.prob.ui.ltl.CounterExampleView";

	private static final String DATA_KEY = "tabdata";

	public static enum ViewType {
		INTERACTIVE, TREE, TABLE
	};

	private CTabFolder tabFolder;
	private ViewType viewType = ViewType.INTERACTIVE;
	private int currentIndex = -1;

	public static CounterExampleViewPart showDefault() {
		final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		CounterExampleViewPart counterExampleView = null;
		try {
			counterExampleView = (CounterExampleViewPart) workbenchPage
					.showView(ID);
		} catch (PartInitException e) {
			Logger.notifyUser("Failed to show the LTL view.", e);
		}
		return counterExampleView;
	}

	public static CounterExampleViewPart getDefault() {
		final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		return (CounterExampleViewPart) workbenchPage.findView(ID);
	}

	@Override
	protected Control createStatePartControl(Composite parent) {
		tabFolder = new CTabFolder(parent, SWT.BORDER);

		tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			@Override
			public void close(CTabFolderEvent event) {
				if (tabFolder.getItemCount() == 1)
					updateCounterExampleLoadedProvider(false);
			}
		});

		return tabFolder;
	}

	public void addCounterExample(final CounterExample counterExample) {
		initializeMenuSetting();
		updateCounterExampleLoadedProvider(true);

		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				final CTabItem tabItem = createTabItem(counterExample);
				tabFolder.setSelection(tabItem);
				tabFolder.update();
			}
		};

		Display.getDefault().asyncExec(runnable);
	}

	private void initializeMenuSetting() {
		if (tabFolder.getItemCount() == 0) {
			MenuManager manager = (MenuManager) getViewSite().getActionBars()
					.getMenuManager();

			if (manager.getSize() > 0) {
				if (manager.getItems()[0] instanceof MenuManager) {
					manager = (MenuManager) manager.getItems()[0];

					if (manager.getSize() > 0)
						if (manager.getItems()[0] instanceof CommandContributionItem) {
							CommandContributionItem item = (CommandContributionItem) manager
									.getItems()[0];

							ParameterizedCommand parameterizedCommand = item
									.getCommand();

							Command command = parameterizedCommand.getCommand();

							try {
								HandlerUtil.updateRadioState(command,
										viewType.name());
							} catch (ExecutionException e) {
							}
						}
				}
			}
		}
	}

	public void zoomInCounterExample() {
		final CounterExampleTab tab = getCurrentTab();
		if (tab != null) {
			tab.zoomIn();
		}
	}

	public void zoomOutCounterExample() {
		final CounterExampleTab tab = getCurrentTab();
		if (tab != null) {
			tab.zoomOut();
		}
	}

	public void printCounterExample() {
		final CounterExampleTab tab = getCurrentTab();
		if (tab != null) {
			tab.printCounterExample(getTitle());
		}
	}

	public void setViewType(ViewType viewType) {
		this.viewType = viewType;
		for (CTabItem tabItem : tabFolder.getItems()) {
			final CounterExampleTab tab = getTab(tabItem);
			tab.updateTopControl(viewType);
		}
	}

	public int getCurrentIndex() {
		final CounterExampleTab tab = getCurrentTab();
		return tab != null ? tab.getCurrentIndex() : -1;
	}

	@Override
	protected void stateChanged(final State activeState,
			final Operation operation) {
		final CounterExampleTab tab = getCurrentTab();
		if (tab != null) {
			tab.stateChanged(activeState, operation);
		}
	}

	@Override
	protected void stateReset() {
		super.stateReset();
		updateCounterExampleLoadedProvider(false);
	}

	@Override
	public void dispose() {
		super.dispose();
		updateCounterExampleLoadedProvider(false);
	}

	public CounterExample getCurrentCounterExample() {
		final CounterExampleTab data = getCurrentTab();
		return data == null ? null : data.getCounterExample();
	}

	private void updateCounterExampleLoadedProvider(boolean enabled) {
		ISourceProviderService service = (ISourceProviderService) getSite()
				.getService(ISourceProviderService.class);
		CounterExampleLoadedProvider provider = (CounterExampleLoadedProvider) service
				.getSourceProvider(CounterExampleLoadedProvider.SERVICE);

		provider.setEnabled(enabled);
	}

	private CTabItem createTabItem(final CounterExample counterExample) {
		final CounterExampleTab ceTab = new CounterExampleTab(tabFolder,
				counterExample);
		final CTabItem tabItem = ceTab.getTabitem();
		tabItem.setData(DATA_KEY, ceTab);
		ceTab.updateTopControl(viewType);

		return tabItem;
	}

	private CounterExampleTab getCurrentTab() {
		final CounterExampleTab tab;
		if (tabFolder != null) {
			final CTabItem selection = tabFolder.getSelection();
			tab = getTab(selection);
		} else {
			tab = null;
		}
		return tab;
	}

	private CounterExampleTab getTab(final CTabItem item) {
		return item == null ? null : (CounterExampleTab) item.getData(DATA_KEY);
	}
}