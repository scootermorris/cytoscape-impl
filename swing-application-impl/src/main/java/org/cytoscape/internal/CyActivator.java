
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package org.cytoscape.internal;

import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.CytoscapeShutdown;
import org.cytoscape.application.events.CytoscapeShutdownListener;
import org.cytoscape.application.events.SetCurrentNetworkViewListener;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CytoPanelComponent;
import static org.cytoscape.application.swing.CytoPanelName.*;
import org.cytoscape.application.swing.ToolBarComponent;

import org.cytoscape.event.CyEventHelper;

import org.cytoscape.internal.CyOperatingContextImpl;
import org.cytoscape.internal.QuickStartStartup;
import org.cytoscape.internal.SessionShutdownHandler;
import org.cytoscape.internal.actions.BookmarkAction;
import org.cytoscape.internal.actions.CytoPanelAction;
import org.cytoscape.internal.actions.ExitAction;
import org.cytoscape.internal.actions.PreferenceAction;
import org.cytoscape.internal.actions.PrintAction;
import org.cytoscape.internal.actions.RecentSessionManager;
import org.cytoscape.internal.dialogs.AboutDialogFactoryImpl;
import org.cytoscape.internal.dialogs.BookmarkDialogFactoryImpl;
import org.cytoscape.internal.dialogs.PreferencesDialogFactoryImpl;
import org.cytoscape.internal.layout.ui.LayoutMenuPopulator;
import org.cytoscape.internal.layout.ui.SettingsAction;
import org.cytoscape.internal.select.RowViewTracker;
import org.cytoscape.internal.select.SelectEdgeViewUpdater;
import org.cytoscape.internal.select.SelectNodeViewUpdater;
import org.cytoscape.internal.shutdown.ConfigDirPropertyWriter;
import org.cytoscape.internal.undo.RedoAction;
import org.cytoscape.internal.undo.UndoAction;
import org.cytoscape.internal.util.undo.UndoMonitor;
import org.cytoscape.internal.view.BirdsEyeViewHandler;
import org.cytoscape.internal.view.CyHelpBrokerImpl;
import org.cytoscape.internal.view.CytoscapeDesktop;
import org.cytoscape.internal.view.CytoscapeMenuBar;
import org.cytoscape.internal.view.CytoscapeMenuPopulator;
import org.cytoscape.internal.view.CytoscapeMenus;
import org.cytoscape.internal.view.CytoscapeToolBar;
import static org.cytoscape.internal.view.CyDesktopManager.Arrange.*;
import org.cytoscape.internal.view.NetworkPanel;
import org.cytoscape.internal.view.NetworkViewManager;
import org.cytoscape.internal.view.ToolBarEnableUpdater;
import org.cytoscape.internal.view.help.ArrangeTaskFactory;
import org.cytoscape.internal.view.help.HelpAboutTaskFactory;
import org.cytoscape.internal.view.help.HelpContactHelpDeskTaskFactory;
import org.cytoscape.internal.view.help.HelpContentsTaskFactory;

import org.cytoscape.io.read.CySessionReaderManager;
import org.cytoscape.io.util.RecentlyOpenedTracker;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.io.write.CyPropertyWriterManager;

import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableManager;

import org.cytoscape.property.CyProperty;
import org.cytoscape.property.CyProperty;
import org.cytoscape.property.bookmark.BookmarksUtil;

import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;

import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.session.CySessionManager;

import org.cytoscape.task.NetworkCollectionTaskFactory;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.task.NetworkViewCollectionTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.TableTaskFactory;

import org.cytoscape.util.swing.OpenBrowser;

import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.presentation.RenderingEngineFactory;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.swing.GUITaskManager;
import org.cytoscape.work.swing.GUITunableInterceptor;
import org.cytoscape.work.undo.UndoSupport;

import org.osgi.framework.BundleContext;

import java.util.Properties;


/**
 *
 */
public class CyActivator extends AbstractCyActivator {
	/**
	 * Creates a new CyActivator object.
	 */
	public CyActivator() {
		super();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param bc DOCUMENT ME!
	 */
	public void start(BundleContext bc) {
		CytoscapeShutdown cytoscapeShutdownServiceRef = getService(bc, CytoscapeShutdown.class);
		CyApplicationConfiguration cyApplicationConfigurationServiceRef = getService(bc,
		                                                                             CyApplicationConfiguration.class);
		StreamUtil streamUtilServiceRef = getService(bc, StreamUtil.class);
		RecentlyOpenedTracker recentlyOpenedTrackerServiceRef = getService(bc,
		                                                                   RecentlyOpenedTracker.class);
		CyProperty cytoscapePropertiesServiceRef = getService(bc, CyProperty.class,
		                                                      "(cyPropertyName=cytoscape3.props)");
		CyApplicationManager cyApplicationManagerServiceRef = getService(bc,
		                                                                 CyApplicationManager.class);
		CySessionManager cySessionManagerServiceRef = getService(bc, CySessionManager.class);
		CySessionReaderManager sessionReaderManagerServiceRef = getService(bc,
		                                                                   CySessionReaderManager.class);
		CyPropertyWriterManager propertyWriterManagerRef = getService(bc,
		                                                              CyPropertyWriterManager.class);
		CyNetworkViewManager cyNetworkViewManagerServiceRef = getService(bc,
		                                                                 CyNetworkViewManager.class);
		CyNetworkManager cyNetworkManagerServiceRef = getService(bc, CyNetworkManager.class);
		CyNetworkNaming cyNetworkNamingServiceRef = getService(bc, CyNetworkNaming.class);
		GUITaskManager taskManagerServiceRef = getService(bc, GUITaskManager.class);
		RenderingEngineFactory dingRenderingEngineFactoryServiceRef = getService(bc,
		                                                                         RenderingEngineFactory.class,
		                                                                         "(id=ding)");
		RenderingEngineFactory dingNavigationPresentationFactoryServiceRef = getService(bc,
		                                                                                RenderingEngineFactory.class,
		                                                                                "(id=dingNavigation)");
		GUITunableInterceptor tunableGUIInterceptorServiceRef = getService(bc,
		                                                                   GUITunableInterceptor.class);
		CyProperty bookmarkServiceRef = getService(bc, CyProperty.class,
		                                           "(cyPropertyName=bookmarks)");
		BookmarksUtil bookmarksUtilServiceRef = getService(bc, BookmarksUtil.class);
		CyNetworkFactory cyNetworkFactoryServiceRef = getService(bc, CyNetworkFactory.class);
		CyNetworkViewFactory cyNetworkViewFactoryServiceRef = getService(bc,
		                                                                 CyNetworkViewFactory.class);
		CyLayoutAlgorithmManager cyLayoutsServiceRef = getService(bc, CyLayoutAlgorithmManager.class);
		UndoSupport undoSupportServiceRef = getService(bc, UndoSupport.class);
		CyEventHelper cyEventHelperServiceRef = getService(bc, CyEventHelper.class);
		CyTableManager cyTableManagerServiceRef = getService(bc, CyTableManager.class);
		GUITaskManager guiTaskManagerServiceRef = getService(bc, GUITaskManager.class);
		CyServiceRegistrar cyServiceRegistrarServiceRef = getService(bc, CyServiceRegistrar.class);
		OpenBrowser openBrowserServiceRef = getService(bc, OpenBrowser.class);
		TaskFactory quickStartRef = getService(bc, TaskFactory.class, "(id=QuickStart2)");

		UndoAction undoAction = new UndoAction(undoSupportServiceRef, cyApplicationManagerServiceRef);
		RedoAction redoAction = new RedoAction(undoSupportServiceRef, cyApplicationManagerServiceRef);
		ConfigDirPropertyWriter configDirPropertyWriter = new ConfigDirPropertyWriter(taskManagerServiceRef,
		                                                                              propertyWriterManagerRef,
		                                                                              cyApplicationConfigurationServiceRef);
		CyOperatingContextImpl cyOperatingContext = new CyOperatingContextImpl(cytoscapePropertiesServiceRef,
		                                                                       cyApplicationConfigurationServiceRef);
		CyHelpBrokerImpl cyHelpBroker = new CyHelpBrokerImpl();
		AboutDialogFactoryImpl aboutDialogFactory = new AboutDialogFactoryImpl(openBrowserServiceRef);
		PreferencesDialogFactoryImpl preferencesDialogFactory = new PreferencesDialogFactoryImpl(cyOperatingContext,
		                                                                                         cyEventHelperServiceRef);
		BookmarkDialogFactoryImpl bookmarkDialogFactory = new BookmarkDialogFactoryImpl(bookmarkServiceRef,
		                                                                                bookmarksUtilServiceRef);
		CytoscapeMenuBar cytoscapeMenuBar = new CytoscapeMenuBar();
		CytoscapeToolBar cytoscapeToolBar = new CytoscapeToolBar();
		CytoscapeMenus cytoscapeMenus = new CytoscapeMenus(cytoscapeMenuBar, cytoscapeToolBar);
		ToolBarEnableUpdater toolBarEnableUpdater = new ToolBarEnableUpdater(cytoscapeToolBar);
		NetworkViewManager networkViewManager = new NetworkViewManager(cyApplicationManagerServiceRef,
		                                                               cyNetworkViewManagerServiceRef,
		                                                               cytoscapePropertiesServiceRef,
		                                                               cyHelpBroker);
		BirdsEyeViewHandler birdsEyeViewHandler = new BirdsEyeViewHandler(cyApplicationManagerServiceRef,
		                                                                  networkViewManager,
		                                                                  dingNavigationPresentationFactoryServiceRef);
		NetworkPanel networkPanel = new NetworkPanel(cyApplicationManagerServiceRef,
		                                             cyNetworkManagerServiceRef,
		                                             cyNetworkViewManagerServiceRef,
		                                             birdsEyeViewHandler, guiTaskManagerServiceRef);
		CytoscapeDesktop cytoscapeDesktop = new CytoscapeDesktop(cytoscapeMenus,
		                                                         networkViewManager, networkPanel,
		                                                         cytoscapeShutdownServiceRef,
		                                                         cyEventHelperServiceRef,
		                                                         cyServiceRegistrarServiceRef,
		                                                         guiTaskManagerServiceRef);
		SessionShutdownHandler sessionShutdownHandler = new SessionShutdownHandler(cytoscapeDesktop,
		                                                                           cyNetworkManagerServiceRef);
		PrintAction printAction = new PrintAction(cyApplicationManagerServiceRef,
		                                          cytoscapePropertiesServiceRef);
		ExitAction exitAction = new ExitAction(cyApplicationManagerServiceRef,
		                                       cytoscapeShutdownServiceRef);
		PreferenceAction preferenceAction = new PreferenceAction(cytoscapeDesktop,
		                                                         cyApplicationManagerServiceRef,
		                                                         preferencesDialogFactory,
		                                                         bookmarksUtilServiceRef);
		BookmarkAction bookmarkAction = new BookmarkAction(cytoscapeDesktop,
		                                                   cyApplicationManagerServiceRef,
		                                                   bookmarkDialogFactory);
		LayoutMenuPopulator layoutMenuPopulator = new LayoutMenuPopulator(cytoscapeDesktop,
		                                                                  cyApplicationManagerServiceRef,
		                                                                  guiTaskManagerServiceRef);
		CytoscapeMenuPopulator cytoscapeMenuPopulator = new CytoscapeMenuPopulator(cytoscapeDesktop,
		                                                                           guiTaskManagerServiceRef,
		                                                                           cyApplicationManagerServiceRef,
		                                                                           cyServiceRegistrarServiceRef);
		SettingsAction settingsAction = new SettingsAction(cyLayoutsServiceRef, cytoscapeDesktop,
		                                                   cyApplicationManagerServiceRef,
		                                                   guiTaskManagerServiceRef);
		HelpContentsTaskFactory helpContentsTaskFactory = new HelpContentsTaskFactory(cyHelpBroker,
		                                                                              cytoscapeDesktop);
		HelpContactHelpDeskTaskFactory helpContactHelpDeskTaskFactory = new HelpContactHelpDeskTaskFactory(openBrowserServiceRef);
		HelpAboutTaskFactory helpAboutTaskFactory = new HelpAboutTaskFactory();
		ArrangeTaskFactory arrangeGridTaskFactory = new ArrangeTaskFactory((CytoscapeDesktop)cytoscapeDesktop, GRID);
		ArrangeTaskFactory arrangeCascadeTaskFactory = new ArrangeTaskFactory((CytoscapeDesktop)cytoscapeDesktop,
		                                                                      CASCADE);
		ArrangeTaskFactory arrangeHorizontalTaskFactory = new ArrangeTaskFactory((CytoscapeDesktop)cytoscapeDesktop,
		                                                                         HORIZONTAL);
		ArrangeTaskFactory arrangeVerticalTaskFactory = new ArrangeTaskFactory((CytoscapeDesktop)cytoscapeDesktop,
		                                                                       VERTICAL);
		CytoPanelAction cytoPanelWestAction = new CytoPanelAction(WEST, true, cytoscapeDesktop,
		                                                          cyApplicationManagerServiceRef,
		                                                          1.0f);
		CytoPanelAction cytoPanelSouthAction = new CytoPanelAction(SOUTH, true, cytoscapeDesktop,
		                                                           cyApplicationManagerServiceRef,
		                                                           1.1f);
		CytoPanelAction cytoPanelEastAction = new CytoPanelAction(EAST, false, cytoscapeDesktop,
		                                                          cyApplicationManagerServiceRef,
		                                                          1.2f);
		CytoPanelAction cytoPanelSouthWestAction = new CytoPanelAction(SOUTH_WEST, false,
		                                                               cytoscapeDesktop,
		                                                               cyApplicationManagerServiceRef,
		                                                               1.3f);
		UndoMonitor undoMonitor = new UndoMonitor(undoSupportServiceRef,
		                                          cytoscapePropertiesServiceRef);
		RowViewTracker rowViewTracker = new RowViewTracker();
		SelectEdgeViewUpdater selecteEdgeViewUpdater = new SelectEdgeViewUpdater(rowViewTracker);
		SelectNodeViewUpdater selecteNodeViewUpdater = new SelectNodeViewUpdater(rowViewTracker);
		QuickStartStartup quickStartStartup = new QuickStartStartup(quickStartRef,
		                                                            taskManagerServiceRef,
		                                                            cytoscapeDesktop);
		RecentSessionManager recentSessionManager = new RecentSessionManager(recentlyOpenedTrackerServiceRef,
		                                                                     cyServiceRegistrarServiceRef,
		                                                                     cySessionManagerServiceRef,
		                                                                     sessionReaderManagerServiceRef,
		                                                                     cyApplicationManagerServiceRef);

		registerService(bc, undoAction, CyAction.class, new Properties());
		registerService(bc, redoAction, CyAction.class, new Properties());
		registerService(bc, printAction, CyAction.class, new Properties());
		registerService(bc, exitAction, CyAction.class, new Properties());
		registerService(bc, preferenceAction, CyAction.class, new Properties());
		registerService(bc, bookmarkAction, CyAction.class, new Properties());
		registerService(bc, settingsAction, CyAction.class, new Properties());
		registerService(bc, cytoPanelWestAction, CyAction.class, new Properties());
		registerService(bc, cytoPanelSouthAction, CyAction.class, new Properties());
		registerService(bc, cytoPanelEastAction, CyAction.class, new Properties());
		registerService(bc, cytoPanelSouthWestAction, CyAction.class, new Properties());

		Properties helpContentsTaskFactoryProps = new Properties();
		helpContentsTaskFactoryProps.setProperty("preferredMenu", "Help");
		helpContentsTaskFactoryProps.setProperty("iconName", "/images/ximian/stock_help.png");
		helpContentsTaskFactoryProps.setProperty("title", "Contents...");
		helpContentsTaskFactoryProps.setProperty("tooltip", "Show Help Contents...");
		helpContentsTaskFactoryProps.setProperty("toolBarGravity", "7.0f");
		helpContentsTaskFactoryProps.setProperty("inToolBar", "true");
		registerService(bc, helpContentsTaskFactory, TaskFactory.class, helpContentsTaskFactoryProps);

		Properties helpContactHelpDeskTaskFactoryProps = new Properties();
		helpContactHelpDeskTaskFactoryProps.setProperty("preferredMenu", "Help");
		helpContactHelpDeskTaskFactoryProps.setProperty("title", "Contact Help Desk...");
		registerService(bc, helpContactHelpDeskTaskFactory, TaskFactory.class,
		                helpContactHelpDeskTaskFactoryProps);

		Properties helpAboutTaskFactoryProps = new Properties();
		helpAboutTaskFactoryProps.setProperty("preferredMenu", "Help");
		helpAboutTaskFactoryProps.setProperty("title", "About...");
		registerService(bc, helpAboutTaskFactory, TaskFactory.class, helpAboutTaskFactoryProps);

		Properties arrangeGridTaskFactoryProps = new Properties();
		arrangeGridTaskFactoryProps.setProperty("enableFor", "networkAndView");
		arrangeGridTaskFactoryProps.setProperty("preferredMenu", "View.Arrange Network Windows[110]");
		arrangeGridTaskFactoryProps.setProperty("title", "Grid");
		registerService(bc, arrangeGridTaskFactory, TaskFactory.class, arrangeGridTaskFactoryProps);

		Properties arrangeCascadeTaskFactoryProps = new Properties();
		arrangeCascadeTaskFactoryProps.setProperty("enableFor", "networkAndView");
		arrangeCascadeTaskFactoryProps.setProperty("preferredMenu",
		                                           "View.Arrange Network Windows[110]");
		arrangeCascadeTaskFactoryProps.setProperty("title", "Cascade");
		registerService(bc, arrangeCascadeTaskFactory, TaskFactory.class,
		                arrangeCascadeTaskFactoryProps);

		Properties arrangeHorizontalTaskFactoryProps = new Properties();
		arrangeHorizontalTaskFactoryProps.setProperty("enableFor", "networkAndView");
		arrangeHorizontalTaskFactoryProps.setProperty("preferredMenu",
		                                              "View.Arrange Network Windows[110]");
		arrangeHorizontalTaskFactoryProps.setProperty("title", "Horizontal");
		registerService(bc, arrangeHorizontalTaskFactory, TaskFactory.class,
		                arrangeHorizontalTaskFactoryProps);

		Properties arrangeVerticalTaskFactoryProps = new Properties();
		arrangeVerticalTaskFactoryProps.setProperty("enableFor", "networkAndView");
		arrangeVerticalTaskFactoryProps.setProperty("preferredMenu",
		                                            "View.Arrange Network Windows[110]");
		arrangeVerticalTaskFactoryProps.setProperty("title", "Vertical");
		registerService(bc, arrangeVerticalTaskFactory, TaskFactory.class,
		                arrangeVerticalTaskFactoryProps);
		registerAllServices(bc, cytoscapeDesktop, new Properties());
		registerAllServices(bc, networkPanel, new Properties());
		registerAllServices(bc, networkViewManager, new Properties());
		registerAllServices(bc, birdsEyeViewHandler, new Properties());
		registerService(bc, undoMonitor, SetCurrentNetworkViewListener.class, new Properties());
		registerAllServices(bc, rowViewTracker, new Properties());
		registerAllServices(bc, selecteEdgeViewUpdater, new Properties());
		registerAllServices(bc, selecteNodeViewUpdater, new Properties());
		registerService(bc, sessionShutdownHandler, CytoscapeShutdownListener.class,
		                new Properties());
		registerAllServices(bc, toolBarEnableUpdater, new Properties());
		registerService(bc, configDirPropertyWriter, CytoscapeShutdownListener.class,
		                new Properties());
		registerAllServices(bc, quickStartStartup, new Properties());
		registerAllServices(bc, recentSessionManager, new Properties());

		registerServiceListener(bc, cytoscapeDesktop, "addAction", "removeAction", CyAction.class);
		registerServiceListener(bc, preferenceAction, "addCyProperty", "removeCyProperty",
		                        CyProperty.class);
		registerServiceListener(bc, cytoscapeDesktop, "addCytoPanelComponent",
		                        "removeCytoPanelComponent", CytoPanelComponent.class);
		registerServiceListener(bc, cytoscapeDesktop, "addToolBarComponent",
		                        "removeToolBarComponent", ToolBarComponent.class);
		registerServiceListener(bc, cytoscapeMenuPopulator, "addTaskFactory", "removeTaskFactory",
		                        TaskFactory.class);
		registerServiceListener(bc, cytoscapeMenuPopulator, "addNetworkTaskFactory",
		                        "removeNetworkTaskFactory", NetworkTaskFactory.class);
		registerServiceListener(bc, cytoscapeMenuPopulator, "addNetworkViewTaskFactory",
		                        "removeNetworkViewTaskFactory", NetworkViewTaskFactory.class);
		registerServiceListener(bc, cytoscapeMenuPopulator, "addNetworkCollectionTaskFactory",
		                        "removeNetworkCollectionTaskFactory",
		                        NetworkCollectionTaskFactory.class);
		registerServiceListener(bc, cytoscapeMenuPopulator, "addNetworkViewCollectionTaskFactory",
		                        "removeNetworkViewCollectionTaskFactory",
		                        NetworkViewCollectionTaskFactory.class);
		registerServiceListener(bc, cytoscapeMenuPopulator, "addTableTaskFactory",
		                        "removeTableTaskFactory", TableTaskFactory.class);
		registerServiceListener(bc, networkViewManager, "addPresentationFactory",
		                        "removePresentationFactory", RenderingEngineFactory.class);
		registerServiceListener(bc, networkPanel, "addNetworkViewTaskFactory",
		                        "removeNetworkViewTaskFactory", NetworkViewTaskFactory.class);
		registerServiceListener(bc, networkPanel, "addNetworkTaskFactory",
		                        "removeNetworkTaskFactory", NetworkTaskFactory.class);
		registerServiceListener(bc, networkPanel, "addNetworkViewCollectionTaskFactory",
		                        "removeNetworkViewCollectionTaskFactory",
		                        NetworkViewCollectionTaskFactory.class);
		registerServiceListener(bc, networkPanel, "addNetworkCollectionTaskFactory",
		                        "removeNetworkCollectionTaskFactory",
		                        NetworkCollectionTaskFactory.class);
		registerServiceListener(bc, configDirPropertyWriter, "addCyProperty", "removeCyProperty",
		                        CyProperty.class);
		registerServiceListener(bc, layoutMenuPopulator, "addLayout", "removeLayout",
		                        CyLayoutAlgorithm.class);
	}
}