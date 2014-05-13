/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cachedataanalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
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
    
    private static Properties props;
    
    private static void loadProperties() {
         props = new Properties();
         try {
              props.load(new FileInputStream("config.properties"));
         } catch (FileNotFoundException e) {
              e.printStackTrace();
         } catch (IOException e) {
              e.printStackTrace();
         }
    }

    private static String getConfig(String key) {
        if (props == null) {
            loadProperties();
        }
        return props.getProperty(key);
    }
    
    public  static class CacheSetting{
        public final static int level = Integer.valueOf(getConfig("CACHE_LEVEL"));
        public final static int size = Integer.valueOf(getConfig("CACHE_SIZE"));
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        System.out.println(getConfig("POLICY"));
        
        int size = 16;
        
        //bear
       //new hello
        
        //FlowTableWindow flowWindow = new FlowTableWindow(new File("flowlog.txt"));
        FlowTableWindow flowWindow = null;
        ArrayList<VPort> ports = new ArrayList<VPort>();
        HashMap<String, CacheSimulator> caches = new HashMap<String, CacheSimulator>();
        ArrayList<PacketLog> packets;
        File logDir = new File("intflog");
        
        File[] interfaces = logDir.listFiles();
        
       if(interfaces!=null){
           caches.put("central_cache",new FlowCache(flowWindow,CacheSetting.size*(interfaces.length-1),"Central Cache")); 
           for(File log : interfaces){
               if(log.isHidden()){
                   continue;
               }
               ports.add(new VPort(log));
               if(CacheSetting.level == 1){
                    caches.put(log.getName(),new FlowCache(flowWindow,CacheSetting.size,log.getName()));
               }else{
                   caches.put(log.getName(),new Level2_FlowCache(flowWindow,CacheSetting.size,log.getName()));
               }
           }                    
       }else{
           System.err.println("No logs found!");
           return;
       }
        int count =1 ;
        
        
        while(ports.get(0).nextSecond()){
            
            //Test for the level 1 cache
            if(CacheSetting.level == 1){
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
            }
            //Test for the level 2 cache
            else if(CacheSetting.level == 2){
                for(String cacheName : caches.keySet()){
                    CacheSimulator cache = caches.get(cacheName);
                    cache.invalidate();
                }
                for(VPort port : ports){
                    packets = port.getPackets();
                    for(PacketLog log : packets){
                        Level2_FlowCache cache = (Level2_FlowCache) caches.get(port.getFileName());
                        if(!cache.lookup_with_return(log)){
                            CacheSimulator cache_central =  caches.get("central_cache");
                            cache_central.lookup(log);
                        }
                    }
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
