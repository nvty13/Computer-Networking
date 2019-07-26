package exercise;

import protocol.CNDP5Tool;
import protocol.CNDatalinkP5;
import protocol.CNP345Frame;
import protocol.CNPacket;

public class DatalinkGoBakeN implements CNDatalinkP5{
    int dummy_seq_num = -1;
    
    @Override
    public boolean isReadyForDeparture(CNDP5Tool tool) {
        // Temporary code
        return tool.isSendingWindowReadyToOpen();
    }
    @Override
    public void arrival(CNP345Frame frm, CNDP5Tool tool) {
        // for debug
        System.out.println("DatalinkGoBakeN.arrival()");
        // To process frm's payload
        if(frm.getPayload() != null){
            if(tool.bufferFrame(frm)){
                tool.deliver(frm.getPayload());
                tool.advanceWindow();
            }
            tool.startAckTimer(20000, frm.getSeq());
        }
        
        // To process frm's ack_num
        int[] array = tool.getSendingWindow();
        int ack_num = frm.getAck();
        if(array.length != 0){
            if(array[0] == ack_num){
                dummy_seq_num = ack_num;
                tool.stopResendTimer(ack_num);
                tool.closeWindow(ack_num);
            }
        }
    }

    @Override
    public void departure(CNPacket pkt, CNDP5Tool tool) {
        // for debug
        System.out.println("DatalinkGoBakeN.departure()");
        CNP345Frame frm = tool.createP345Frame();
        int a = tool.openWindow();
        int ack_num = tool.getEarliestAckTimer();
        frm.setSeq(a);
        frm.setPayload(pkt);
        frm.setAck(ack_num);
        tool.send(frm);
        tool.startResendTimer(20000, frm);

    }

    @Override
    public void resendTimerExpired(CNP345Frame frame, CNDP5Tool tool) {
        // for debug
        System.out.println("DatalinkGoBakeN.resendTimerExpired()");
        
        //Call tool.getEarliestAckTimer()
        int t = tool.getEarliestAckTimer();
        frame.setAck(t);
        tool.send(frame);
        tool.startResendTimer(20000, frame);

    }

    @Override
    public void ackTimerExpired(int ack_num, CNDP5Tool tool) {
        // for debug
        System.out.println("DatalinkGoBakeN.ackTimerExpired()");
        
        if(dummy_seq_num < 0){
            dummy_seq_num = tool.getMaximumSeqNum();
        }
        
        CNP345Frame frm = tool.stopFirstResendTimer();
        if(frm == null){
            frm = tool.createP345Frame();
            frm.setSeq(dummy_seq_num);
        }
        frm.setAck(ack_num);
        tool.send(frm);
        
        if(frm.getPayload() != null){
            tool.startResendTimer(20000, frm);
        }
    }
}
