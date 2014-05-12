/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cachedataanalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.jfree.data.xy.XYSeries;



/*
Chart Content

1. X-Y Spline : Hit&(Miss+Hit) for each intf
2. PieChart : Protocols in Hit instances and Miss instances

*/


/**
 *
 * @author ljybowser
 */
public class CacheDataAnalysis {
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        int size = 16;
        
        //bear
       //new bear
        
        //FlowTableWindow flowWindow = new FlowTableWindow(new File("flowlog.txt"));
        FlowTableWindow flowWindow = null;
        ArrayList<VPort> ports = new ArrayList<VPort>();
        HashMap<String, CacheSimulator> caches = new HashMap<String, CacheSimulator>();
        ArrayList<PacketLog> packets;
        File logDir = new File("intflog");
        
        File[] interfaces = logDir.listFiles();
        
       if(interfaces!=null){
           caches.put("central_cache",new FlowCacheGroup(flowWindow,size*(interfaces.length-1),"Central Cache")); 
           for(File log : interfaces){
               if(log.isHidden()){
                   continue;
               }
               ports.add(new VPort(log));
               caches.put(log.getName(),new FlowCacheGroup(flowWindow,size,log.getName()));
           }                    
       }else{
           System.err.println("No logs found!");
           return;
       }
        int count =1 ;
        
        
        while(ports.get(0).nextSecond()){
          
            for(VPort eth : ports){
                //eth.nextSecond();
            }
            
            for(String cacheName : caches.keySet()){
                CacheSimulator cache = caches.get(cacheName);
                cache.invalidate();
            }
                  
            
            for(VPort port : ports){
                packets = port.getPackets();
                for(PacketLog log : packets){
                    CacheSimulator cache = caches.get(port.getFileName());
                    cache.lookup(log);
                    cache = caches.get("central_cache");
                    cache.lookup(log);
                }
            }
            
            
            count++;
        }
        
        ArrayList<ArrayList<XYSeries>> series = new ArrayList<ArrayList<XYSeries>>();
       
        for(String cacheName : caches.keySet()){
            CacheSimulator cache = caches.get(cacheName);
            cache.exportReportChart();
            //System.out.println(cacheName+" Hit = "+cache.hit+" , All = "+(cache.miss+cache.hit)+" Hit/Miss = "+(float)(cache.hit)/(cache.hit+cache.miss));
            series.add(cache.exportHitRateData());
        } 
        
        for(int i=1; i<series.size();i++){
            ArrayList<XYSeries> _ser = series.get(i);
            _ser.addAll(series.get(0));
        }
        
        for(int i=1;i<series.size();i++){
            ReportChartGenerator.DrawXYLineChart("HitRate"+i, "Seconds", "HitRate", series.get(i));
        }
        
    }
    
}
