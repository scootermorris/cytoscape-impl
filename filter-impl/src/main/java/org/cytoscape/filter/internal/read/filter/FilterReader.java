package org.cytoscape.filter.internal.read.filter;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.cytoscape.property.CyProperty;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;
import org.cytoscape.session.CySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cytoscape.filter.internal.filters.CompositeFilter;
import org.cytoscape.filter.internal.filters.FilterPlugin;
import java.util.Vector;
import org.cytoscape.filter.internal.filters.FilterIO;

public final class FilterReader implements CyProperty<Vector<CompositeFilter>>, SessionLoadedListener {
	private static final Logger logger = LoggerFactory.getLogger(FilterReader.class);

	// This is in the resource file (jar)
	private static final String DEF_PROP_FILE_NAME = "default_filters.props";

	private Vector<CompositeFilter> FilterVect;

	/**
	 * Creates a new BookmarkReader object.
	 */
	public FilterReader(String resourceLocation) {
		
		InputStream is = null;
		this.FilterVect = new Vector<CompositeFilter>();

		try {
			if ( resourceLocation == null )
				throw new NullPointerException("resourceLocation is null");

			is = this.getClass().getClassLoader().getResourceAsStream(resourceLocation);
						
			if (is == null) {
				// Load the default filter
				is = this.getClass().getClassLoader().getResourceAsStream(DEF_PROP_FILE_NAME);

				if (is == null) {
					System.err.println("FilterPlugin: Failed to read default filters from \""
					                   + DEF_PROP_FILE_NAME + "\" in the plugin's jar file!");
					return;
				}

				final InputStreamReader inputStreamReader = new InputStreamReader(is);

				this.FilterVect = FilterIO.getFilterVectFromPropFile(inputStreamReader);
			}
			else {
				// load filters from prop file
				System.out.println("\n\nFilterReader constructor: Load filters frm prop file.....\n");
				
				
				
			}

			
		} catch (Exception e) {
			logger.warn("Could not read filter file.", e);
		} finally {
			if (is != null) {
				try { is.close(); } catch (IOException ioe) {}
				is = null;
			}
		}
		
//		System.out.println("\n\n\tFilterReader  this.FilterVect.size()="+this.FilterVect.size()+ "\n");
	}

	
	@Override
	public Vector<CompositeFilter> getProperties() {
		return this.FilterVect;
	}


	@Override
	public CyProperty.SavePolicy getSavePolicy() {
		return CyProperty.SavePolicy.SESSION_FILE_AND_CONFIG_DIR;
	}
	
	
	@Override
	public void handleEvent(SessionLoadedEvent e) {
		// TODO
//		logger.debug("Updating filters from loaded session...");
//		
//		Vector<CompositeFilter> newFilterVect = null;
//		CySession sess = e.getLoadedSession();
//		
//		if (sess != null)
//			this.FilterVect = sess.getBookmarks();
//		else
//			logger.warn("Loaded session is null.");
//		
//		if (newFilterVect == null) {
//			logger.warn("Could not get new filters from loaded session - using empty filters.");
//			this.FilterVect = null;
//		}
//		
//		this.FilterVect = newFilterVect;
	}
}