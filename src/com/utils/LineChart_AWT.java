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

public class LineChart_AWT extends ApplicationFrame {

   public LineChart_AWT( String chartTitle,String xAxisLabel,String yAxisLabel,DefaultCategoryDataset dataset ) {
      super(chartTitle);
      JFreeChart lineChart = ChartFactory.createLineChart(
         chartTitle,
         xAxisLabel,
         yAxisLabel,
         dataset,
         PlotOrientation.VERTICAL,
         true,true,true);
      try{
      //saveAsJPEG(lineChart,chartTitle,560,367);   
       File imageFile = File.createTempFile("WFC_By_ArmanRiazi_"+WFCConstants.WFCBrokerTypes+"_"+chartTitle, ".jpg");
       ChartUtilities.saveChartAsJPEG(imageFile, lineChart, 560 , 367 );          
      }
      catch(IOException ex){
       throw new RuntimeException("Error saving a file",ex);
      }
      ChartPanel chartPanel = new ChartPanel( lineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
      setContentPane( chartPanel );
      
   }
   /*public File saveAsJPEG(JFreeChart chart,String chartTitle,int width, int height) throws IOException {
            File imageFile = File.createTempFile("WFC_By_ArmanRiazi_"+WFCConstants.WFCBrokerTypes+"_"+chartTitle, ".jpg");
            ChartUtilities.saveChartAsPNG(imageFile, chart, width, height);            
           return imageFile;
    }*/

}

