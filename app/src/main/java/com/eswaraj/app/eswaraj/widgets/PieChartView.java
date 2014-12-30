package com.eswaraj.app.eswaraj.widgets;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.view.MotionEvent;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.models.ComplaintCounter;

import java.util.List;

public class PieChartView extends GraphicalView {

	private PieChartView(Context context, AbstractChart arg1) {
		super(context, arg1);
	}

	public static GraphicalView getNewInstance(Context context, List<ComplaintCounter> complaintCounters) {
		GraphicalView pieChartView = ChartFactory.getPieChartView(context, getDataSet(context, complaintCounters), getRenderer(context, complaintCounters));
		pieChartView.zoomIn();
		return pieChartView;
	}

	private static DefaultRenderer getRenderer(Context context, List<ComplaintCounter> complaintCounters) {
		int[] colors = context.getResources().getIntArray(R.array.rainbow);

		DefaultRenderer defaultRenderer = new DefaultRenderer();
        for(int i = 0; i < complaintCounters.size(); i++) {
            int color = colors[i];
			SimpleSeriesRenderer simpleRenderer = new SimpleSeriesRenderer();
			simpleRenderer.setColor(color);
			defaultRenderer.addSeriesRenderer(simpleRenderer);
		}
		defaultRenderer.setShowLabels(false);
		defaultRenderer.setShowLegend(false);
		defaultRenderer.setScale(1.33f);
		defaultRenderer.setSelectableBuffer(0);
		
		defaultRenderer.setInScroll(true);
		return defaultRenderer;
	}

	private static CategorySeries getDataSet(Context context, List<ComplaintCounter> complaintCounters) {
		CategorySeries series = new CategorySeries("Chart");
		for (ComplaintCounter complaintCounter : complaintCounters) {
			series.add(complaintCounter.getCount());
		}
		return series;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}
}
