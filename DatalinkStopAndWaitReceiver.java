package exercise;

import protocol.CNDP12Tool;
import protocol.CNDatalinkP12R;
import protocol.CNP12Frame;
import protocol.CNPacket;

public class DatalinkStopAndWaitReceiver implements CNDatalinkP12R{
    @Override
    public void arrival(CNP12Frame frm, CNDP12Tool tool) {
        // for debug
        System.out.println("DatalinkStopAndWaitReciver.arrival()");
        CNPacket c = frm.getPayload();
        tool.deliver(c);
        CNP12Frame d = tool.createP12Frame();
        //d.setPayload(frm);
        tool.send(d);
    }
}
