/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cachedataanalysis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.CategoryTableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author ljybowser
 */
public class FlowCache implements CacheSimulator{
    
    protected int policy = 0;
    boolean fullReport;
    
    ArrayList<FlowEntry> cache;
    
    
    HashMap<String, Integer> hitProto,missProto;
    ArrayList<HashMap<String,Integer>> hitProtoRec, missProtoRec;
    ArrayList<Integer> hitCount,allCount;
    ArrayList<Float> hitRateRecord;
    
    final FlowTableWindow flowTable;
    protected final int size;
    protected final String name;
    
    public int hit=0,miss=0;
    
    
    
    public FlowCache(FlowTableWindow ft,int size,String name,boolean full){
        flowTable = ft;
        cache = new ArrayList<>();
        this.size = size;
        this.name = name;
        hitProto = new HashMap<>();
        missProto = new HashMap<>();
        hitCount = new ArrayList<>();
        allCount = new ArrayList<>();
        hitRateRecord = new ArrayList<>();
        hitProtoRec = new ArrayList<HashMap<String,Integer>>();
        missProtoRec = new ArrayList<HashMap<String,Integer>>();
        fullReport = full;
        
    }
    
    public FlowCache(FlowTableWindow ft,int size,String name){
        this(ft,size,name,true);
    }
    
    public FlowCache(FlowTableWindow ft,int size,String name,boolean full,int policy){
        this(ft,size,name,full);
        this.policy = policy;
    }
    
    protected void update(PacketLog packet){
        
        if(cache.size()<size){
            if(flowTable!=null){
                FlowEntry e = flowTable.getFlowEntry(packet);
                if(e!=null)
                    cache.add(e);
            }else{
                //cache.add(new FlowEntry(packet.toString()));
            }
        }else{
            cache.remove(findVictim());
            if(flowTable!=null){
                FlowEntry e = flowTable.getFlowEntry(packet);
            
                if(e!=null)
                    cache.add(e);
            }else{
                //cache.add(new FlowEntry(packet.toString()));
            }
        }
        
    }
    
    public static final int LFU = 0;
    public static final int LRU = 1;
    
    private int findVictim(){
        
        if(policy==0){//LFU
            Iterator it = cache.iterator();
        
            int minVal = Integer.MAX_VALUE;
            int minIndex = 0,count=0;
        
            while(it.hasNext()){
            
                FlowEntry entry = (FlowEntry)it.next();
                if(entry.getPrior()<minVal){
                    minVal = entry.getPrior();
                    minIndex = count;
                }
            
                count++;
            }
        
            return minIndex;
        }else if(policy==1){//LRU
            
            Iterator it = cache.iterator();
        
            float minVal = Integer.MAX_VALUE;
            int minIndex = 0,count=0;
        
            while(it.hasNext()){
            
                FlowEntry entry = (FlowEntry)it.next();
                if(entry.getRecentHit()<minVal){
                    minVal = entry.getRecentHit();
                    minIndex = count;
                }
            
                count++;
            }
        
            return minIndex;
            
        }
        
        return 0;
        
    }
    
    public void lookup(PacketLog packet){
        
        if(!cache.isEmpty()){
            for (FlowEntry entry : cache) {
                if(entry!=null && entry.flowMatch(packet)){
                    hit++;
                    if(fullReport){
                        if(hitProto.get(packet.getOrigProto())==null){
                            hitProto.put(packet.getOrigProto(),new Integer(1));
                        }else{
                            Integer i = hitProto.get(packet.getOrigProto());
                            hitProto.put(packet.getOrigProto(),new Integer(1+i.intValue()));
                        }
                    }
                    return;
                }
            }
            
        }
        miss++;
        
        if(fullReport){
            if(missProto.get(packet.getOrigProto())==null){
                missProto.put(packet.getOrigProto(),new Integer(1));
            }else{
                Integer i = missProto.get(packet.getOrigProto());
                missProto.put(packet.getOrigProto(),new Integer(1+i.intValue()));
            }
        }
        
        update(packet);
        return;
    }
    
    public void invalidate(){
        ArrayList<FlowEntry> removeList = new ArrayList<FlowEntry>();
        if(flowTable!=null){
            for(FlowEntry entry : cache){
                if(!flowTable.isStillThere(entry)){
                    removeList.add(entry);
                }
            }
        }
        
        cache.removeAll(removeList);
        
        hitRateRecord.add((float)hit/(hit+miss));
        
        if(!fullReport){
            return;
        }
        
        hitCount.add(hit);
        allCount.add(hit+miss);
        
        
        HashMap<String,Integer> _newHitProtoRec = new HashMap<>();
        for(String _str : hitProto.keySet()){
            int _a = hitProto.get(_str);
            _newHitProtoRec.put(_str,_a);
        }
        hitProtoRec.add(_newHitProtoRec);
        
        HashMap<String,Integer> _newMissProtoRec = new HashMap<>();
        for(String _str : missProto.keySet()){
            int _a = missProto.get(_str);
            _newMissProtoRec.put(_str,_a);
        }
        missProtoRec.add(_newMissProtoRec);
        

        
    }
    
    public ArrayList<XYSeries> exportHitRateData(){
        
        XYSeries hitRateCount = new XYSeries(name+"("+size+" entries)");
        
        for(int i=0;i<hitRateRecord.size();i++){
            hitRateCount.add(i,hitRateRecord.get(i));
        }
        
        XYSeriesCollection HitRateData = new XYSeriesCollection();
        HitRateData.addSeries(hitRateCount);
        
        ArrayList<XYSeries> result = new ArrayList<XYSeries>();
        result.add(hitRateCount);
        
        return result;
    }
    
    public XYSeries exportReportChart() throws IOException{
        
        File outputHitRate = new File("report/"+name+"_"+policy+"_hitrate.txt");
        
        FileWriter fw = new FileWriter(outputHitRate);
        
        XYSeries hitRateCount = new XYSeries(name+"("+size+" entries)");
        
        for(int i=0;i<hitRateRecord.size();i++){
            hitRateCount.add(i,hitRateRecord.get(i));
            fw.write(i+","+hitRateRecord.get(i)+"\n");
            fw.flush();
        }
        fw.close();
        
        XYSeriesCollection HitRateData = new XYSeriesCollection();
        HitRateData.addSeries(hitRateCount);
        JFreeChart hitRateStat = ChartFactory.createXYLineChart("Hit Rate variatoion throughout recording in "+name,
                "Seconds",
                "HitRate",
                HitRateData,
                PlotOrientation.
                        VERTICAL,
                true,
                true,
                false);
        
        ChartUtilities.saveChartAsJPEG(new File("report/"+name+"_hitRate"+size+".jpg"),hitRateStat,1500,900);
        
        
        
        //-------------------------------
        
        if(!fullReport){
            return hitRateCount;
        }
        
        DefaultPieDataset hitProtoData = new DefaultPieDataset();
        DefaultPieDataset missProtoData = new DefaultPieDataset();
        
        CategoryTableXYDataset hitProtoDataRec = new CategoryTableXYDataset();
        for(int i=0;i<hitProtoRec.size();i++){
            if(i%120!=0){
                continue;
            }
            HashMap<String,Integer> _map = hitProtoRec.get(i);
            for(String _str : _map.keySet()){
                hitProtoDataRec.add(i,_map.get(_str),_str);
            }
            
        }
        
        CategoryTableXYDataset missProtoDataRec = new CategoryTableXYDataset();
        for(int i=0;i<missProtoRec.size();i++){
            if(i%120!=0){
                continue;
            }
            HashMap<String,Integer> _map = missProtoRec.get(i);
            for(String _str : _map.keySet()){
                missProtoDataRec.add(i,_map.get(_str),_str);
            }
            
        }
        
        
        
        XYSeries hitCountData = new XYSeries("Hit");
        XYSeries allCountData = new XYSeries("All");
        
        
        for(int i=0;i<hitCount.size();i++){
            hitCountData.add(i,hitCount.get(i));
            //hitRateCount.add(i,(float)hitCount.get(i)/(hitCount.get(i)+allCount.get(i)));
        }
        
        for(int i=0;i<allCount.size();i++){
            allCountData.add(i,allCount.get(i));
            
        }
        
        XYSeriesCollection CountStatData = new XYSeriesCollection();
        
        
        CountStatData.addSeries(hitCountData);
        CountStatData.addSeries(allCountData);
        
        
        for(String protocol : hitProto.keySet()){
            hitProtoData.setValue(protocol,hitProto.get(protocol));
        }
        
        for(String protocol : missProto.keySet()){
            missProtoData.setValue(protocol,missProto.get(protocol));
        }
        
        JFreeChart hitStatRec = ChartFactory.createStackedXYAreaChart("Hit Protocol Fraction throughout record in "+name,
                "Seconds",
                "Count",
                hitProtoDataRec);
        
        JFreeChart missStatRec = ChartFactory.createStackedXYAreaChart("Miss Protocol Fraction throughout record in "+name,
                "Seconds",
                "Count",
                missProtoDataRec);
        
        JFreeChart hitStat = ChartFactory.createPieChart3D("Protocal Fraction of Hit Flows in "+name,hitProtoData,true,true,false);
        JFreeChart missStat = ChartFactory.createPieChart3D("Protocal Fraction of Miss Flows in "+name,missProtoData,true,true,false);
        JFreeChart countStat = ChartFactory.createXYAreaChart("Hit Count to All Packet lookup in "+name,
                "Seconds",
                "Count",
                CountStatData,
                PlotOrientation.
                        VERTICAL,
                true,
                true,
                false);
        
        
        
        ChartUtilities.saveChartAsJPEG(new File("report/"+name+"_hitProto"+size+".jpg"),hitStat,1500,900);
        ChartUtilities.saveChartAsJPEG(new File("report/"+name+"_missProto"+size+".jpg"),missStat,1500,900);
        ChartUtilities.saveChartAsJPEG(new File("report/"+name+"_hitCount"+size+".jpg"),countStat,1500,900);
        
        ChartUtilities.saveChartAsJPEG(new File("report/"+name+"_hitProtoRecord"+size+".jpg"),hitStatRec,1500,900);
        ChartUtilities.saveChartAsJPEG(new File("report/"+name+"_missProtoRecord"+size+".jpg"),missStatRec,1500,900);
        
        
        
        
        
        
        
        return hitRateCount;
    }
    
    
    
}
