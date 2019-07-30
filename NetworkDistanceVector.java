package exercise;

import java.util.ArrayList;
import protocol.CNNDistanceVector;
import protocol.CNNetworkTool;
import protocol.CNPacket;
import protocol.CNRoutingTable;
import protocol.CNRoutingTableEntry;

public class NetworkDistanceVector extends NetworkForwarding implements CNNDistanceVector{
    @Override
    public void arrival(int arrival_line, CNPacket pkt, CNNetworkTool tool) {
        // for debug
        System.out.println("NetworkDistanceVector.arrival()");

	if( pkt.isUserPacket()){
	    super.arrival(arrival_line, pkt, tool);
	}else{
	    CNRoutingTable rt = tool.getRoutingTable();
	    ArrayList<CNRoutingTableEntry> arte = pkt.getDistanceVector();
	    int cost = tool.getLinkCost(arrival_line);

	    for(CNRoutingTableEntry rte : arte){
		CNRoutingTableEntry tmp = rt.getEntry(rte.getDestination());
		if(tmp == null){
		    rt.addOrUpdateEntry(rte.getDestination(), arrival_line, cost+rte.getCost());
		}else if( tmp != null &&  tmp.getCost() > cost+rte.getCost() ){
		    tmp.setCost(cost+rte.getCost());
		    tmp.setLine(arrival_line);  
		}
	    }
	}
    }
    
    @Override
    public void routingTimerExpired(CNNetworkTool tool) {
        // for debug
        System.out.println("NetworkDistanceVector.routingTimerExpired()");

	int num_interface = tool.getNumberofInterfaces();
	CNPacket pkt = tool.createDVPacket();
	CNRoutingTable rtb = tool.getRoutingTable();
		    
	for(int i=0; i<num_interface; i++){
	    String nid = tool.getNetworkID(i);
	    if( tool.isNetworkAvailable(i) ){
		rtb.addOrUpdateEntry(nid, tool.getLineIDByAddress(nid), 0);
	    }else{
		rtb.removeEntry(nid);
	    }   
	}
	
	pkt.setDistanceVector(rtb.getAllEntries());
	for(int i=0; i<num_interface; i++){
	    tool.send(i, pkt);
	}
	for( CNRoutingTableEntry re : rtb.getAllEntries() ){
	    re.setCost(re.getCost()+1);
	}
    }
}
