/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cachedataanalysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author jybowser
 */
public class ReportChartGenerator {
    
    public static void DrawXYLineChart(String chartName, String xAxis,String yAxis,ArrayList<XYSeries> series) throws IOException{
        
        XYSeriesCollection serCol = new XYSeriesCollection();
        

        for(XYSeries _xys : series){
                serCol.addSeries(_xys);
         }

        
        JFreeChart hitRateStat = ChartFactory.createXYLineChart("Hit Rate variatoion throughout recording in "+chartName,
                "Seconds",
                "HitRate",
                serCol,
                PlotOrientation.
                        VERTICAL,
                true,
                true,
                false);
        
        ChartUtilities.saveChartAsJPEG(new File("report/"+chartName+".jpg"),hitRateStat,1500,900);
        
    }
    
}
