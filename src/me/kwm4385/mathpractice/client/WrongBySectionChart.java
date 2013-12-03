package me.kwm4385.mathpractice.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import me.kwm4385.mathpractice.client.model.Question;
import me.kwm4385.mathpractice.client.model.Result;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart.Type;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart.PieOptions;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;

public class WrongBySectionChart extends DockPanel {

	private Result result;

	public WrongBySectionChart(Result result) {
		this.result = result;

		Runnable onLoadCallback = new Runnable() {
			public void run() {
				PieChart pie = new PieChart(createTable(), createOptions());
				WrongBySectionChart.this.add(pie, DockPanel.CENTER);
			}
		};

		VisualizationUtils.loadVisualizationApi(onLoadCallback, PieChart.PACKAGE);
	}

	private PieOptions createOptions() {
		PieOptions options = PieOptions.create();
		options.setWidth(580);
		options.setHeight(365);
		options.setTitle("Incorrect Questions by Section");
		options.setType(Type.PIE);
		return options;
	}

	private AbstractDataTable createTable() {
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Section");
		data.addColumn(ColumnType.NUMBER, "Number of questions");
		HashMap<String, Integer> sections = new HashMap<String, Integer>();
		for(Question q : result.getWrongQuestions()) {
			String sec = new StringBuilder().append("Section " ).append(q.getChapter()).append(".").append(q.getSection()).toString();
			if(!sections.keySet().contains(sec)) {
				sections.put(sec, 1);
			} else {
				sections.put(sec, sections.get(sec) + 1);
			}
		}
		data.addRows(sections.keySet().size());
		int i = 0;
		List<String> secs = new ArrayList<String>(sections.keySet());
		Collections.sort(secs);
		for(String s : secs) {
			data.setValue(i, 0, s);
			data.setValue(i, 1, sections.get(s).intValue());
			i++;
		}
		return data;
	}
}