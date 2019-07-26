package exercise;

import protocol.CNRoutingTable;
import protocol.CNRoutingTableEntry;
import protocol.CNSegment;
import protocol.CNNetworkForwarding;
import protocol.CNPacket;
import protocol.CNNetworkTool;

public class NetworkForwarding implements CNNetworkForwarding{
    @Override
    public void arrival(int arrival_line, CNPacket pkt, CNNetworkTool tool) {
        
    // for debug
    System.out.println("NetworkForwarding.arrival()");

        String dest_address = pkt.getDestinationAddress();
        int TTL;
        
        if(tool.isOneOfMyAddress(dest_address)){//Call tool.isOneOfMyAddress() with the destination address in packet to check
            tool.deliver(pkt.getPayload());//Call the tool.deliver() to pass the payload to the upper layer
        }
        else {
            TTL = pkt.reduceTTL();//Reduce the TTL of packet
            pkt.setTTL(TTL);
            if(TTL > 0){
                CNRoutingTable r_table = tool.getRoutingTable();//Get Routing table
                CNRoutingTableEntry table_entry = r_table.getEntry(dest_address);
                if(table_entry != null && table_entry.getLine() != arrival_line){
                    tool.send(table_entry.getLine(),pkt);//Send packet using the line given by the entry
                }
            }
        }

    }
    
    @Override
    public void departure(String dest, int protocol, CNSegment smt, CNNetworkTool tool) {
        // for debug
        System.out.println("NetworkForwarding.departure()");
        //Create a user packet by tool.createUserPacket();
        CNPacket user_pkt = tool.createUserPacket();
        
        //Set the payload, protocol ID and destination address of the packet
        user_pkt.setPayload(smt);
        user_pkt.setProtocolID(protocol);
        user_pkt.setDestinationAddress(dest);
        
        //Tack IP address tool.getMyIPAdress() and set it to the source address of the packet
        String ip = tool.getMyIPAddress();
        user_pkt.setSourceAddress(ip);
        
        //Send the packet by tool.send()
        tool.send(0, user_pkt);
    }
}
