package exercise;

import protocol.CNDP12Tool;
import protocol.CNDatalinkP12;
import protocol.CNP12Frame;
import protocol.CNPacket;

public class DatalinkUtopian implements CNDatalinkP12{
   @Override
    public void arrival(CNP12Frame frm, CNDP12Tool tool) {
        // for debug
        System.out.println("DatalinkUtopian.arrival()");
        //getPayload(frm);
        CNPacket a = frm.getPayload();
        // get the packet (a CNPacket object) from frame's payload;
        // pass the packet to the network layer by calling
        // tool.deliver();
        tool.deliver(a);
    }
	
    @Override
    public void departure(CNPacket packet, CNDP12Tool tool) {
        // for debug
        System.out.println("DatalinkUtopian.departure()");
        // create a frame (a CNP12Frame object);
        CNP12Frame b = tool.createP12Frame();
        // set packet to the frame's payload field;
        // pass the frame to physical layer by calling
        b.setPayload(packet);
        // tool.send();
        tool.send(b);
    }

    @Override
    public boolean isReadyForDeparture() {
        // In this protocol, it is always ready.
        return true;
    }
}
