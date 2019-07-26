package exercise;

import protocol.CNDP12Tool;
import protocol.CNDatalinkP12;
import protocol.CNP12Frame;
import protocol.CNPacket;

public class DatalinkStopAndWaitSender implements CNDatalinkP12{
    boolean waiting_flag;
    @Override
    public boolean isReadyForDeparture() {
        //waiting_flag = departure(pkt, tool);
        // If you are waiting an acknowledgement, return false;
        // Otherwise, return true;
        if(waiting_flag){
            return false;
        }
        else{
            return true;
        }
    }
    
    @Override
    public void arrival(CNP12Frame frm, CNDP12Tool tool) {
        // for debug
        System.out.println("DatalinkStopAndWaitSender.arrival()");
        CNPacket a = frm.getPayload();
        tool.deliver(a);
        waiting_flag = false;
        
    }

    @Override
    public void departure(CNPacket pkt, CNDP12Tool tool) {
        // for debug
        System.out.println("DatalinkStopAndWaitSender.departure()");
        CNP12Frame b = tool.createP12Frame();
        // set packet to the frame's payload field;
        // pass the frame to physical layer by calling
        b.setPayload(pkt);
        // tool.send();
        tool.send(b);
        waiting_flag = true;

    }
}
