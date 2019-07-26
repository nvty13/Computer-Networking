package exercise;

import protocol.CNDP3Tool;
import protocol.CNDatalinkP3R;
import protocol.CNP345Frame;
import protocol.CNPacket;

public class DatalinkAckRetranReceiver implements CNDatalinkP3R{
	    private int seqNum = 0;
    @Override
	public void arrival(CNP345Frame frm, CNDP3Tool tool) {
	CNP345Frame res;
	if(frm.getSeq() == seqNum){
	    tool.deliver(frm.getPayload());	    
	    seqNum++;
	    seqNum %= 2;
	}
	res = tool.createP345Frame();
	res.setAck(frm.getSeq());
	tool.send(res);



        // for debug
        System.out.println("DatalinkAckRetranReceiver.arrival()");
        // TODO Auto-generated method stub

    }
}
