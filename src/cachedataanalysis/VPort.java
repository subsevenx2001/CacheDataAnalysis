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

/**
 *
 * @author ljybowser
 */
public class VPort {
    
    private final BufferedReader bf;
    private ArrayList<PacketLog> packets = new ArrayList<PacketLog>();
    private String name;
    private int time = 0;
    private String lastLine="";
    
    public VPort(File f) throws FileNotFoundException{
        
        name = f.getName();
        
        bf = new BufferedReader(new FileReader(f));
        
    }
    
    public boolean nextSecond() throws IOException{
        
        if(!bf.ready()){
            return false;
        }else{
            packets.clear();
            
            if(!lastLine.equals("")){
                PacketLog lastLog = new PacketLog(lastLine);
                if((int)lastLog.getTime()==time){
                    packets.add(lastLog);
                }
            }
            
            String line = bf.readLine();
            //System.err.println(line);
            PacketLog log = new PacketLog(line);
            while((int)log.getTime()==time){
                packets.add(log);
                line = bf.readLine();
                log = new PacketLog(line);
                //System.out.println(line);
                //System.out.println(time+" "+log.getTime());
                
                
                
            }
            
            lastLine = line;
            
        }
        time++;
        return true;
    }
    
    public ArrayList<PacketLog> getPackets(){
        return packets;
    }
    
    public String getFileName(){
        return name;
    }
    
}
