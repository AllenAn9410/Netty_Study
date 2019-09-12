package ibm.mq;

import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;

public class MQSendTask implements Runnable{

    private static final Logger logger  = LoggerFactory.getLogger(MQSendTask.class);
    private MQMessage receiveMsg;
    private MQQueue sendQueue;

    public MQSendTask(MQMessage receiveMsg, MQQueue sendQueue){
        this.receiveMsg = receiveMsg;
        this.sendQueue = sendQueue;
    }

    @Override
    public void run() {
        //String charset = MQConfig.getInstance().getProperty("mqcharset");
        String mqxml="";
        try {
            mqxml = receiveMsg.readStringOfByteLength(receiveMsg.getDataLength());
            System.out.println("mq message : " + mqxml);
        } catch (EOFException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
