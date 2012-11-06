/*
 Copyright (c) 2008, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.internal.select;

import java.util.Map;

import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.RowSetRecord;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;

/**
 * A utility class that provides an implementation of {@link RowsSetListener}
 * for a particular column and {@link VisualProperty}.
 * @param <S> The generic type of this ViewUpdater.
 * @CyAPI.Abstract.Class
 */
public abstract class AbstractViewUpdater<S> implements RowsSetListener {

	/** The {@link VisualProperty} that the {@link RowsSetListener} is provided for. */
	protected final VisualProperty<S> vp;
	/** The name of the column that the {@link RowsSetListener} is provided for.*/
	protected final String columnName;

	private final RowViewTracker tracker;

	/**
	 * Constructor.
	 * 
	 * @param vp The visual property that should be set on the view when the row
	 * is changed.
	 * @param columnName The name of the column within the row that is being
	 * listened to.
	 * @param tracker a map between the row that is being listened to and the
	 * view that the visual property should be set when the row is changed.
	 */
	public AbstractViewUpdater(VisualProperty<S> vp, String columnName, RowViewTracker tracker) {
		this.vp = vp;
		this.columnName = columnName;
		this.tracker = tracker;
	}

	/**
	 * Called whenever {@link CyRow}s are changed. Will attempt to set the
	 * visual property on the view with the new value that has been set in the
	 * row.
	 * 
	 * @param e The {@link RowsSetEvent} to be processed.
	 */
	@SuppressWarnings("unchecked")
	public void handleEvent(RowsSetEvent e) {
		CyTable cyTable = e.getSource();

		for (CyNetworkView networkView : tracker.getAffectedNetworkViews(cyTable)) {
			Map<CyRow, View<?>> rowViewMap = tracker.getRowViewMap(networkView);
			for (RowSetRecord record : e.getColumnRecords(columnName)) {
				View<?> v = rowViewMap.get(record.getRow());
	
				if (v != null)
					v.setVisualProperty(vp, (S) record.getValue());
			}
		}
	}
}