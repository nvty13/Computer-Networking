package exercise;

import protocol.CNDP4Tool;
import protocol.CNDatalinkP4;
import protocol.CNP345Frame;
import protocol.CNPacket;

public final class DatalinkOnebit implements CNDatalinkP4{
    private boolean waiting_flag = false;
    private int exp_seq_num = 0;
    private int out_seq_num = 0;
     @Override
    public boolean isReadyForDeparture() {
        // If you are waiting an acknowledgement, return false;
        // Otherwise, return true;
        return !waiting_flag;   // temporary code
    }
    @Override
    public void resendTimerExpired(CNP345Frame frame, CNDP4Tool tool) {
        // for debug
        System.out.println("DatalinkOnebit.resendTimerExpired()");
        //Check if the ack_timer is running use tool.getEarliestAckTimer() to check
        //if getEarliestAckTimer() return an ack_num
        int g = tool.getEarliestAckTimer();
        int ack_timer;
        if(g == -1){
            //Use it
            //ack_timer = g;
            frame.setAck(this.out_seq_num);
        }
        
        //else, use ack_num before
        else{
            //ack_timer = ack_last;
            frame.setAck(g);
        }
        
        //frame.setAck(ack_timer);
       // ack_last = ack_timer;
        //pass the frame to the physical layer by calling send()
        tool.send(frame);
        
        //Start resend-timer for the frame again, as in the method departure()
        tool.startResendTimer(20000, frame);
    }
    @Override
    public void ackTimerExpired(int ack_num, CNDP4Tool tool) {
        // for debug
        System.out.println("DatalinkOnebit.ackTimerExpired()");
        
        //If any resend-timer is running, call tool.stopFirstResendTimer() to check and stop the first one
        CNP345Frame a = tool.stopFirstResendTimer();
        if(a == null){
            a = tool.createP345Frame();
            a.setSeq(out_seq_num);
        }
        else{
            a.setAck(ack_num);
            tool.send(a);
            //tool.startResendTimer(20000, a);
        }
        if(a!=null){
            tool.startResendTimer(20000,a);
        }
    }

    @Override
    public void arrival(CNP345Frame frm, CNDP4Tool tool) {
        // for debug
        System.out.println("DatalinkOnebit.arrival()");
        
        //If the ack_number of frm == the seq_num
        if(frm.getAck() == out_seq_num){
            //clear the waiting_flag
            waiting_flag = false;
            
            //stop resend_timer of frm by calling the stopResendTimer()
            tool.stopResendTimer(frm.getAck());
            
            //Increase the seq_number
            out_seq_num = 1 - out_seq_num;
        }
        
        //If the seq_num of frm is that you are waiting for
        if(frm.getSeq() == exp_seq_num){
            //Pass the packet in the received frame to the network layer
            tool.deliver(frm.getPayload());
            
            //manage the seq_num
            exp_seq_num = 1 - exp_seq_num;
        }
        
        //if frm has a payload, create an ack-timer for its seq_num
        if(frm.getPayload() != null){
            //create an ack-timer for its seq_num
            tool.startAckTimer(2000, frm.getSeq());
        }
        
        
    }

    @Override
    public void departure(CNPacket pkt, CNDP4Tool tool) {
        // for debug
        System.out.println("DatalinkOnebit.departure()");
        
        //Create a frame for pkt and give it current seq_num
        CNP345Frame f = tool.createP345Frame();
        f.setPayload(pkt);
        f.setSeq(out_seq_num);
        
        //Give the frame a proper ack_num as in the method resendTimerExpired()
        //f.resendTimerExpired(ack_num);
        int ack = tool.getEarliestAckTimer();
        if(ack == -1){
            ack = 1 - out_seq_num;
        }
        //Pass the frame to the physical layer by calling send()
        f.setAck(ack);
        tool.send(f);
        
        //Start a resend-timer for the frame by calling startResendTimer()
        tool.startResendTimer(20000, f);
        
        //Set waiting_flag
        waiting_flag = true;

    }
}
