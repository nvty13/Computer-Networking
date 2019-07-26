package exercise;

import protocol.CNDP3Tool;
import protocol.CNDatalinkP3;
import protocol.CNP345Frame;
import protocol.CNPacket;

public class DatalinkAckRetranSender implements CNDatalinkP3{
    private boolean waiting_flag = false;
    private int current_seq = 0;


    @Override
    public void resendTimerExpired(CNP345Frame frame, CNDP3Tool tool) {
        //Pass the frame to the physical layer by calling send()
        tool.send(frame);
        
        //start a resend-timer for the frame again
        tool.startResendTimer(20000, frame);
        // for debug
        System.out.println("DatalinkAckRetranSender.resendTimerExpired()");
    }

    @Override
    public void arrival(CNP345Frame frm, CNDP3Tool tool) {
        //if the number of ack is right:
        if(frm.getAck() == current_seq%2){
            //Clear the waiting flag
            waiting_flag = false;
            
            //stop the resend-timer
            tool.stopResendTimer(frm.getAck());
            current_seq++;
        }
        // for debug
        System.out.println("DatalinkAckRetranSender.arrival(), frm.ack = " + frm.getAck());

        
    }

    @Override
    public void departure(CNPacket pkt, CNDP3Tool tool) {
        //create a frame called 'f'
        CNP345Frame f = tool.createP345Frame();
        f.setPayload(pkt);
        
        //give the frame a current number
        f.setSeq(current_seq%2);
        
        //pass the frame by calling send()
        tool.send(f);
        
        //Start resend_timer for the frame
        tool.startResendTimer(20000, f);
        
        //Set the waiting flag
        waiting_flag = true;
        // for debug
        System.out.println("DatalinkAckRetranSender.departure()");

    }

    @Override
    public boolean isReadyForDeparture() {
        // If you are waiting an acknowledgement, return false;
        // Otherwise, return true;
        return !waiting_flag;   // temporary code
    }
}
