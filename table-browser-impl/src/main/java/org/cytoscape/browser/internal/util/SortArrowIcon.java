/* %% Ignore-License */
/*
 * $Archive: SourceJammer$
 * $FileName: SortArrowIcon.java$
 * $FileID: 3986$
 *
 * Last change:
 * $AuthorName: Timo Haberkern$
 * $Date: 2007-12-07 18:57:59 -0800 (Fri, 07 Dec 2007) $
 * $Comment: $
 *
 * $KeyWordsOff: $
 */

/*
 =====================================================================

 SortArrowIcon.java

 Created by Claude Duguay
 Copyright (c) 2002

 =====================================================================
 */
package org.cytoscape.browser.internal.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;


/**
 *
 */
public class SortArrowIcon implements Icon {
	/**
	 * 
	 */
	public static final int NONE = 0;

	/**
	 * 
	 */
	public static final int DECENDING = 1;

	/**
	 * 
	 */
	public static final int ASCENDING = 2;
	protected int direction;
	protected int width = 8;
	protected int height = 8;

	/**
	 * Creates a new SortArrowIcon object.
	 *
	 * @param direction  DOCUMENT ME!
	 */
	public SortArrowIcon(int direction) {
		this.direction = direction;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getIconWidth() {
		return width;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getIconHeight() {
		return height;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		Color bg = c.getBackground();
		Color light = Color.black;
		Color shade = bg.darker();
		
		int w = width;
		int h = height;
		int m = w / 2;

		if (direction == ASCENDING) {
			g.setColor(shade);
			g.drawLine(x, y, x + w, y);
			g.drawLine(x, y, x + m, y + h);
			g.setColor(light);
			g.drawLine(x + w, y, x + m, y + h);
		}

		if (direction == DECENDING) {
			g.setColor(shade);
			g.drawLine(x + m, y, x, y + h);
			g.setColor(light);
			g.drawLine(x, y + h, x + w, y + h);
			g.drawLine(x + m, y, x + w, y + h);
		}
	}
}
