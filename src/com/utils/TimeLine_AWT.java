package com.utils;


import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.Timeline;
import org.jfree.chart.labels.HighLowItemLabelGenerator;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.OHLCDataset;

public class TimeLine_AWT extends ApplicationFrame {

   public TimeLine_AWT(
                                            String chartTitle,
                                            String timeAxisLabel,
                                            String valueAxisLabel,
                                            OHLCDataset dataset,
                                            Timeline timeline,
                                            boolean legend) {
    super(chartTitle);
    DateAxis timeAxis = new DateAxis(timeAxisLabel);
    timeAxis.setTimeline(timeline);
    NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
    HighLowRenderer renderer = new HighLowRenderer();
    renderer.setBaseToolTipGenerator(new HighLowItemLabelGenerator());
    XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, renderer);
    JFreeChart timelineChart = new JFreeChart(chartTitle, JFreeChart.DEFAULT_TITLE_FONT,plot, legend);             
    ChartPanel chartPanel = new ChartPanel( timelineChart );
    chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
    setContentPane( chartPanel );
   }
}