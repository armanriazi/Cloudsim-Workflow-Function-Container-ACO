package com.utils;


import java.awt.Color;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Day;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

public class TimeSeriesChart_AWT extends ApplicationFrame {

   public TimeSeriesChart_AWT( String chartTitle ) {
      super(chartTitle);
      ChartPanel chartPanel = (ChartPanel) createDemoPanel();          
      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
      chartPanel.setMouseZoomable(true, false);
      setContentPane( chartPanel );
   }
   
    /**
     * Creates a chart.
     * 
     * @param dataset  a dataset.
     * 
     * @return A chart.
     */
    private static JFreeChart createChart(XYDataset dataset) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Legal & General Unit Trust Prices",  // title
            "Date",             // x-axis label
            "Price Per Unit",   // y-axis label
            dataset,            // data
            true,               // create legend?
            true,               // generate tooltips?
            false               // generate URLs?
        );

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        
        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
        }
        
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
        
        return chart;

    }
    
    /**
     * Creates a dataset, consisting of two series of monthly data.
     *
     * @return The dataset.
     */
    private static XYDataset createDataset() {

        TimeSeries s1 = new TimeSeries("L&G European Index Trust", Millisecond.class);
        s1.add(new Millisecond(2,3,6,4,2,01, 2001), 181.8);
        s1.add(new Millisecond(2,3,6,4,3,01, 2001), 167.3);
        s1.add(new Millisecond(2,3,6,4,4, 01, 2001), 153.8);
        s1.add(new Millisecond(2,3,6,4,5, 01, 2001), 167.6);
        s1.add(new Millisecond(2,3,6,4,6, 01, 2001), 158.8);
        s1.add(new Millisecond(2,3,6,4,7, 01, 2001), 148.3);
        s1.add(new Millisecond(2,3,6,4,8, 01, 2001), 153.9);
        s1.add(new Millisecond(2,3,6,4,9, 01, 2001), 142.7);
        s1.add(new Millisecond(2,3,6,4,10, 01, 2001), 123.2);
        s1.add(new Millisecond(2,3,6,4,11, 01, 2001), 131.8);
        s1.add(new Millisecond(2,3,6,4,12, 01, 2001), 139.6);
       

        TimeSeries s2 = new TimeSeries("L&G UK Index Trust", Millisecond.class);
        s2.add(new Millisecond(2,3,6,4,2, 01, 2001), 129.6);
        s2.add(new Millisecond(2,3,6,4,3, 01, 2001), 123.2);
        s2.add(new Millisecond(2,3,6,4,4, 01, 2001), 117.2);
        s2.add(new Millisecond(2,3,6,4,5, 01, 2001), 124.1);
        s2.add(new Millisecond(2,3,6,4,6, 01, 2001), 122.6);
        s2.add(new Millisecond(2,3,6,4,7, 01, 2001), 119.2);
        s2.add(new Millisecond(2,3,6,4,8, 01, 2001), 116.5);
        s2.add(new Millisecond(2,3,6,4,9, 01, 2001), 112.7);
        s2.add(new Millisecond(2,3,6,4,10, 01, 2001), 101.5);
        s2.add(new Millisecond(2,3,6,4,11, 01, 2001), 106.1);
        s2.add(new Millisecond(2,3,6,4,12, 01, 2001), 110.3);
        

        // ******************************************************************
        //  More than 150 demo applications are included with the JFreeChart
        //  Developer Guide...for more information, see:
        //
        //  >   http://www.object-refinery.com/jfreechart/guide.html
        //
        // ******************************************************************
        
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);
        dataset.addSeries(s2);
        
        return dataset;

    }

    /**
     * Creates a panel for the demo (used by SuperDemo.java).
     * 
     * @return A panel.
     */
    public static JPanel createDemoPanel() {
        JFreeChart chart = createChart(createDataset());
        return new ChartPanel(chart);
    }
}