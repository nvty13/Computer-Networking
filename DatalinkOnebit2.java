package exercise;

import protocol.CNDP4Tool;
import protocol.CNDatalinkP4;
import protocol.CNP345Frame;
import protocol.CNPacket;

public final class DatalinkOnebit2 implements CNDatalinkP4{

    private int seqout = 0;
    private int seqnext = 0;
    private boolean waiting_flag = false;
    
    @Override
    public boolean isReadyForDeparture() {
	if(waiting_flag){
	    return false;
	}
        // If you are waiting an acknowledgement, return false;
        // Otherwise, return true;
        return true;   // temporary code
    }
    @Override
    public void resendTimerExpired(CNP345Frame frame, CNDP4Tool tool) {
	int acknum = tool.getEarliestAckTimer();
	if(acknum==-1){
	    frame.setAck(this.seqout);
	}
	else{
	    frame.setAck(acknum);
	}
	tool.send(frame);
	tool.startResendTimer(20000, frame);
        // for debug
        System.out.println("DatalinkOnebit.resendTimerExpired()");
    }
    @Override
    public void ackTimerExpired(int ack_num, CNDP4Tool tool) {
	CNP345Frame frm = tool.stopFirstResendTimer();
	if(frm == null){
	    frm = tool.createP345Frame();
	    frm.setSeq(seqout);
	}
	frm.setAck(ack_num);
	tool.send(frm);
	if(frm != null){
	    tool.startResendTimer(20000, frm);
	}
        // for debug
        System.out.println("DatalinkOnebit.ackTimerExpired()");
    }

    @Override
    public void arrival(CNP345Frame frm, CNDP4Tool tool) {
	if(frm.getAck() == seqout){
	    waiting_flag = false;
	    tool.stopResendTimer(frm.getAck());
	    seqout = 1 - seqout;
	}


	if(frm.getSeq() == seqnext){
	    tool.deliver(frm.getPayload());
	    seqnext = 1 - seqnext;
	}

	if(frm.getPayload() != null){
	    tool.startAckTimer(2000, frm.getSeq());
	}
	
        // for debug
        System.out.println("DatalinkOnebit.arrival()");      
    }

    @Override
    public void departure(CNPacket pkt, CNDP4Tool tool) {
	CNP345Frame f = tool.createP345Frame();
	f.setPayload(pkt);
	f.setSeq(seqout);

	int ack = tool.getEarliestAckTimer();
	if(ack == -1){
	    ack = 1 - seqout;
	}
	f.setAck(ack);
	tool.send(f);
	tool.startResendTimer(20000, f);
	waiting_flag = true;
        // for debug
        System.out.println("DatalinkOnebit.departure()");

    }
}

