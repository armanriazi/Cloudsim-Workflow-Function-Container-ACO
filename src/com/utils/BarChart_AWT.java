package com.utils;


import java.io.File;
import java.io.IOException;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.wfc.core.WFCConstants;

public class BarChart_AWT extends ApplicationFrame {

   public BarChart_AWT( String chartTitle,String xAxisLabel,String yAxisLabel,boolean legend,boolean tooltips,boolean noUrl,DefaultCategoryDataset dataset ) {
      super(chartTitle);
      JFreeChart barChart = ChartFactory.createBarChart(
         chartTitle,
         xAxisLabel,
         yAxisLabel,
         dataset,
         PlotOrientation.VERTICAL,
         legend,tooltips,noUrl);        
         try{
      //saveAsJPEG(lineChart,chartTitle,560,367);   
       File imageFile = File.createTempFile("WFC_By_ArmanRiazi_"+WFCConstants.WFCBrokerTypes+"_"+chartTitle, ".jpg");
       ChartUtilities.saveChartAsJPEG(imageFile, barChart, 560 , 367 );          
      }
      catch(IOException ex){
       throw new RuntimeException("Error saving a file",ex);
      }
      ChartPanel chartPanel = new ChartPanel( barChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
      setContentPane( chartPanel );
   }   
}