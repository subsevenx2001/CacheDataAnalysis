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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author jybowser
 */
public class FlowCacheGroup implements CacheSimulator{
    
    int GROUP_SIZE = 3;
    private String name;
    private int size;
    private FlowTableWindow ftw;
    
    ArrayList<FlowCache> caches;
    
    public FlowCacheGroup(FlowTableWindow ft,int size,String name){
        ftw = ft;
        this.size = size;
        this.name = name;
        caches = new ArrayList<FlowCache>();
        
        /*for(int i=0;i<GROUP_SIZE;i++){
            if(i!=GROUP_SIZE/2+1){
                caches.add(new FlowCache(ft, (int) ((int)size*Math.pow((double)2,(double)i-(GROUP_SIZE/2) )),name+"_"+(int) ((int)size*Math.pow((double)2,(double)i-(GROUP_SIZE/2) )),false));
            }else{
                caches.add(new FlowCache(ft,size,name+"_"+size));
            }
        }*/
        caches.add(new FlowCache(ft,size/2,name+"_"+size/2,false,1));
        caches.add(new FlowCache(ft,size,name+"_"+size,false,1));
        caches.add(new FlowCache(ft,size*2,name+"_"+size*2,false,1));
        
        
    }
    
    public void invalidate(){
        for(FlowCache fc : caches){
            fc.invalidate();
        }
    }
    
    public void lookup(PacketLog p){
        
        for(FlowCache fc : caches){
            fc.lookup(p);
        }
        
    }
    
    public ArrayList<XYSeries> exportHitRateData(){
    
        ArrayList<XYSeries> result = new ArrayList<>();
        
        for(FlowCache fc: caches){
            result.addAll(fc.exportHitRateData());
        }
        return result;
    }
    
    public XYSeries exportReportChart() throws IOException{
        
        ArrayList<XYSeries> series = new ArrayList<XYSeries>();
        
        for(FlowCache cache : caches){
            series.add(cache.exportReportChart());
        }
        
        XYSeriesCollection data = new XYSeriesCollection();
        
        for(XYSeries s : series){
            data.addSeries(s);
        }
        
        JFreeChart chart = ChartFactory.createXYLineChart("Performance by Size", "Second", "Hit Rate", data);
        ChartUtilities.saveChartAsJPEG(new File("report/"+name+"_hitRateComp"+size+".jpg"),chart,1500,900);
        
        return series.get(series.size()/2+1);
        
    }
    
}
