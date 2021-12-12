package com.utils;


import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.*;
import org.jfree.data.general.*;

public class GanttChart_AWT extends ApplicationFrame {

   public GanttChart_AWT( String chartTitle,String xAxisLabel,String yAxisLabel,IntervalCategoryDataset dataset ) {
      super(chartTitle);
      JFreeChart lineChart = ChartFactory.createGanttChart(
         chartTitle,
         xAxisLabel,
         yAxisLabel,
         dataset,         
         true,true,true);
         
      ChartPanel chartPanel = new ChartPanel( lineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
      setContentPane( chartPanel );
   }
}