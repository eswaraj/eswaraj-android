package com.next.eswaraj.widgets;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;

import com.next.eswaraj.models.ComplaintCounter;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.util.List;
import java.util.Map;

public class PieChartView extends GraphicalView {

	private PieChartView(Context context, AbstractChart arg1) {
		super(context, arg1);
	}

	public static GraphicalView getNewInstance(Context context, List<ComplaintCounter> complaintCounters, List<CategoryWithChildCategoryDto> categoryDtoList, Map<Long, Integer> colorMap) {
		GraphicalView pieChartView = ChartFactory.getPieChartView(context, getDataSet(context, complaintCounters, categoryDtoList), getRenderer(context, complaintCounters, categoryDtoList, colorMap));
		pieChartView.zoomIn();
		return pieChartView;
	}

	private static DefaultRenderer getRenderer(Context context, List<ComplaintCounter> complaintCounters, List<CategoryWithChildCategoryDto> categoryDtoList, Map<Long, Integer> colorMap) {

		DefaultRenderer defaultRenderer = new DefaultRenderer();
        for(int i = 0; i < complaintCounters.size(); i++) {
            int color = 0;
            for(CategoryWithChildCategoryDto categoryDto : categoryDtoList) {
                if(categoryDto.getId().equals(complaintCounters.get(i).getId())) {
                    if(categoryDto.getColor() != null) {
                        color = Color.parseColor("#" + categoryDto.getColor());
                    }
                    else {
                        color = colorMap.get(categoryDto.getId());
                    }
                }
            }
			SimpleSeriesRenderer simpleRenderer = new SimpleSeriesRenderer();
			simpleRenderer.setColor(color);
			defaultRenderer.addSeriesRenderer(simpleRenderer);
		}
		defaultRenderer.setShowLabels(false);
		defaultRenderer.setShowLegend(false);
		defaultRenderer.setScale(1.33f);
		defaultRenderer.setSelectableBuffer(0);
        defaultRenderer.setPanEnabled(false);
        defaultRenderer.setZoomEnabled(false);
		
		defaultRenderer.setInScroll(true);
		return defaultRenderer;
	}

	private static CategorySeries getDataSet(Context context, List<ComplaintCounter> complaintCounters, List<CategoryWithChildCategoryDto> categoryDtoList) {
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
