package org.cytoscape.ding.impl;

/*
 * #%L
 * Cytoscape Ding View/Presentation Impl (ding-presentation-impl)
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2013 The Cytoscape Consortium
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */


import java.awt.BorderLayout;
import java.util.Properties;

import javax.swing.JComponent;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.events.UpdateNetworkPresentationEvent;
import org.cytoscape.view.model.events.UpdateNetworkPresentationListener;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * RenderingEngineFactory for Navigation.
 */
public class DingNavigationRenderingEngineFactory implements RenderingEngineFactory<CyNetwork> {
	
	private static final Logger logger = LoggerFactory.getLogger(DingNavigationRenderingEngineFactory.class);
	
	private final RenderingEngineManager renderingEngineManager;
	private final VisualLexicon dingLexicon;
	private final CyServiceRegistrar registrar;
	
	
	private final CyApplicationManager appManager;

	public DingNavigationRenderingEngineFactory(final CyServiceRegistrar registrar, final VisualLexicon dingLexicon,
			final RenderingEngineManager renderingEngineManager, final CyApplicationManager appManager) {
		this.dingLexicon = dingLexicon;
		this.renderingEngineManager = renderingEngineManager;
		this.appManager = appManager;
		this.registrar = registrar;
	}

	@Override
	public RenderingEngine<CyNetwork> createRenderingEngine(final Object visualizationContainer, final View<CyNetwork> view) {
		if (visualizationContainer == null)
			throw new IllegalArgumentException(
					"Visualization container is null.  This should be an JComponent for this rendering engine.");
		if (view == null)
			throw new IllegalArgumentException("View Model is null.");

		// Check data type compatibility.
		
		if (!(visualizationContainer instanceof JComponent) || !(view instanceof CyNetworkView))
			throw new IllegalArgumentException("Visualization Container object is not of type Component, "
					+ "which is invalid for this implementation of PresentationFactory");
		
		if (!(view instanceof DGraphView))
			throw new IllegalArgumentException("This rendering engine needs DING view model as its view model.");

		// Shared instance of the view.
		final DGraphView dgv = (DGraphView) view;
		
		logger.debug("Start adding BEV.");
		final JComponent container = (JComponent) visualizationContainer;
		//final RenderingEngine<CyNetwork> engine = appManager.getCurrentRenderingEngine();
		
		// Create instance of an engine.
		final BirdsEyeView bev = new BirdsEyeView(dgv, registrar);

		container.setLayout(new BorderLayout());
		container.add(bev, BorderLayout.CENTER);

		// Register this rendering engine as service.
		bev.registerServices();

		logger.debug("Bird's Eye View had been set to the component.  Network Model = " + view.getModel().getSUID());
		return bev;
	}


	@Override
	public VisualLexicon getVisualLexicon() {
		return dingLexicon;
	}
}
