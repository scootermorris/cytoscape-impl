package org.cytoscape.task.internal.welcome;

import java.io.File;
import java.net.URL;
import java.util.Map;

import javax.swing.JComboBox;

import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class DownloadBiogridDataTaskFactory implements TaskFactory {

	private final JComboBox list;
	
	private DownloadBiogridDataTask task;
	
	private final File settingFile;
	
	DownloadBiogridDataTaskFactory(final JComboBox list, final CyApplicationConfiguration config) {
		settingFile = config.getSettingLocation();
		this.list = list;
	}
	
	@Override
	public TaskIterator getTaskIterator() {
		task = new DownloadBiogridDataTask(settingFile, list);
		return new TaskIterator(task);
	}
	
	Map<String, URL> getMap() {
		return task.getSourceMap();
	}

}