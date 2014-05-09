/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cachedataanalysis;

/**
 *
 * @author ljybowser
 */
public class FlowEntry{
        
        private final int MAC_LENGTH = 17;
        
        //private String content;
        
        private float recentHit=0;
        private String srcIP,srcMac,dstIP,dstMac;
        private String proto;
        private int prior = 0;
        
        public FlowEntry(String str){
            
            String content = str;
            
            int srcMacIndex = content.indexOf("src=");
            srcMac = content.substring(srcMacIndex+4,srcMacIndex+4+MAC_LENGTH);
            int dstMacIndex = content.indexOf("dst=");
            dstMac = content.substring(dstMacIndex+4,dstMacIndex+4+MAC_LENGTH);
            
            int srcIPIndex = content.indexOf("sip");
            int dstIPIndex = content.indexOf("tip");
            int endIPIndex = content.indexOf("op=");
            
            if(srcIPIndex == -1){
                
                int ethTypeIndex = content.indexOf("eth_type");
                
                srcIPIndex = content.indexOf("src=",ethTypeIndex);
                dstIPIndex = content.indexOf("dst=",ethTypeIndex);
                endIPIndex = content.indexOf("proto=");
                
                int endProtoIndex = content.indexOf(",",endIPIndex);
                
                int protoNum = Integer.parseInt(content.substring(endIPIndex+6,endProtoIndex));
                
                switch(protoNum){
                    case 1:
                        proto = "ICMP";
                        break;
                    case 2:
                        proto = "IGMP";
                        break;
                    case 6:
                        proto = "TCP";
                        break;
                    case 17:
                        proto = "UDP";
                        break;
                    case 58:
                        proto = "ICMPv6";
                        break;
                    default:
                        proto = "IPv4";
                }
                
            }else{
                proto = "ARP";
            }
            
            srcIP = content.substring(srcIPIndex+4,dstIPIndex-1);
            dstIP = content.substring(dstIPIndex+4,endIPIndex-1);
            
            //System.out.println(srcMac+" "+dstMac+" "+srcIP+" "+dstIP+" "+proto);
            
            
            
        }
        
        public boolean flowMatch(PacketLog packet){
            
            
            if(packet.getSrcAddr().equals(srcIP) &&
                    packet.getDstAddr().equals(dstIP) &&
                    packet.getProto().equals(proto)){
                
                prior++;
                recentHit = packet.getTime();
                return true;
                
            }
            
            prior--;
            return false;
        }
        
        
        public String getSrcMac(){
            return srcMac;
        }
        
        public String getDstMac(){
            return dstMac;
        }
        
        public String getSrcIP(){
            return srcIP;
        }
        
        public String getDstIP(){
            return dstIP;
        }
        
        public String getProto(){
            return proto;
        }
        
        public int getPrior(){
            return prior;
        }
        
        public float getRecentHit(){
            return recentHit;
        }

    /**
     *
     * @param entry
     * @return
     */
    public boolean equals(FlowEntry entry){
            
            return srcMac.equals(entry.srcMac)&&
                    dstMac.equals(entry.dstMac)&&
                    srcIP.equals(entry.srcIP)&&
                    dstIP.equals(entry.dstIP)&&
                    proto.equals(entry.proto);
        }

    
        
        
        
        
    }
