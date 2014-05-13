/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cachedataanalysis;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author bear
 */
public class Level2_FlowCache extends FlowCache{
    
    public Level2_FlowCache(FlowTableWindow ft,int size,String name,boolean full){
        super(ft, size, name, full);
        
    }
    
    public Level2_FlowCache(FlowTableWindow ft,int size,String name){
        this(ft,size,name,true);
    }
    
    public Level2_FlowCache(FlowTableWindow ft,int size,String name,boolean full,int policy){
        this(ft,size,name,full);
        this.policy = policy;
    }
    
   
    public boolean lookup_with_return(PacketLog packet){
        
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
                    return true;
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
        
        super.update(packet);
        return false;
    }
}
