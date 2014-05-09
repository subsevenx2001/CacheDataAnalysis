/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cachedataanalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author ljybowser
 */
public class FlowTableWindow {
    
    private final File flowLog;
    private final BufferedReader bf;
    private int sec =0;
    
    ArrayList<FlowEntry> flowTable = new ArrayList<FlowEntry>();
    
    public FlowTableWindow(File f) throws FileNotFoundException{
        
        flowLog = f;
        bf = new BufferedReader(new FileReader(flowLog));
        
        
    }
    
    boolean nexFlowTable() throws IOException{
        
        flowTable.clear();
        String line;
        
        if(!bf.ready()){
            return false;
        }else{
            
            line = bf.readLine();
            while(bf.ready()&&line.indexOf("-------------------------")==-1 ){
                flowTable.add(new FlowEntry(line));
                line = bf.readLine();
            }
            
        }
               
        sec++;
       
        return true;
    }
    
    FlowEntry getFlowEntry(PacketLog log){
        
        Iterator it = flowTable.iterator();
        
        while(it.hasNext()){
            
            FlowEntry entry = (FlowEntry)it.next();
                  
            
            if(!(log.getSrcAddr().equals(entry.getSrcIP())||log.getSrcAddr().equals(entry.getSrcMac()) ) || !(log.getDstAddr().equals(entry.getDstIP())||log.getDstAddr().equals(entry.getDstMac())|| log.getDstAddr().equals("Broadcast"))){
                continue;
            }
            
            if(log.getProto().equals(entry.getProto())){
                //System.out.println("H!");
                return entry;
            }
            
        }
        
        return null;
    }
    
    
    boolean isStillThere(FlowEntry e){
        
        for(FlowEntry entry : flowTable){
            if(entry.equals(e)){
                return true;
            }
        }
        
        return false;
    }
    
    
}
