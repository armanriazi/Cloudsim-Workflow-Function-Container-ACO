/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils;

import java.io.File;
import java.io.IOException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.Title;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.VerticalAlignment;
import org.wfc.core.WFCConstants;

public class PieChart_AWT extends ApplicationFrame {
    public PieChart_AWT(String chartTitle,boolean legend,boolean tooltips,boolean noUrl,DefaultPieDataset dataset ) {
      super(chartTitle);
      JFreeChart  pieChart=ChartFactory.createPieChart(
         chartTitle, // chart title
         dataset, //Dataset
         legend, // legend displayed
         tooltips, // tooltips displayed
         noUrl ); // no URLs      
         try{
      //saveAsJPEG(lineChart,chartTitle,560,367);   
       File imageFile = File.createTempFile("WFC_By_ArmanRiazi_"+WFCConstants.WFCBrokerTypes+"_"+chartTitle, ".jpg");
       ChartUtilities.saveChartAsJPEG(imageFile, pieChart, 560 , 367 );          
      }
      catch(IOException ex){
       throw new RuntimeException("Error saving a file",ex);
      }
      ChartPanel chartPanel = new ChartPanel( pieChart );      
      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
      setContentPane( chartPanel );
   }     
}
