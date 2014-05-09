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
public class PacketLog {
    
    private String pContent;
    private String srcAddr,dstAddr;
    private String proto,origProto;
    private float time;
    
    public PacketLog(String str){
        String content = str.trim();
        content = content.replaceAll(" +", " ");
        
        pContent = content;
        
        String[] token;
        token = content.split(" ");
        
 
        time = Float.parseFloat(token[0]);
        srcAddr = token[1];
        dstAddr = token[3];
        origProto = proto = token[4];
        
        srcAddr = srcAddr.replaceAll("Vmware_", "00:0c:29:");
        dstAddr = dstAddr.replaceAll("Vmware_", "00:0c:29:");
        
        if(proto.equals("DNS") || proto.equals("DHCP") || proto.equals("LLMNR")||
                proto.equals("UAUDP")||
                proto.equals("DHCPv6")||proto.equals("SAP/SDP")||proto.equals("DMP")||proto.equals("SSDP")){
            proto = "UDP";
        }else if(proto.equals("NBNS") || proto.equals("HTTP")||
                proto.equals("HTTP/XML")||proto.equals("BROWSER")
                ||proto.equals("SLiMP3")||proto.equals("SSLv2")||
                proto.equals("SSL")||proto.equals("BJNP")||
                proto.equals("TLSv1")||proto.equals("QUAKE3")||
                proto.equals("OCSP")||proto.equals("HART_IP")||proto.equals("TLSv1.2")){
            proto = "TCP";
        }
        
        //System.out.println(time+" "+srcAddr+" "+dstAddr+" "+proto);
        
        
        
        
    }
    
    public String getSrcAddr(){
        return srcAddr;
    }
    
    public String getDstAddr(){
        return dstAddr;
    }
    
    public String getProto(){
        return proto;
    }
    
    public String getOrigProto(){
        return origProto;
    }
    
    public float getTime(){
        return time;
    }
    
    @Override
    public String toString(){
        return pContent;
    }
    
}
