package ibm.mq;

import com.ibm.mq.*;
import com.ibm.mq.constants.MQConstants;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Hashtable;

public class MQ implements Runnable {

    /**
     * 1.请求方false(默认false) 服务方true
     */
    private Boolean MQType;

    /**
     * 2.队列管理器名字
     */
    private String queueManagerName;

    /**
     * 3.主机地址
     */
    private String hostName;

    /**
     * 4.端口
     */
    private int port;

    /**
     * 5.通道
     */
    private String channel;

    /**
     * 6.发送队列名字
     */
    private String sendQueueName;

    /**
     * 7.接收队列名字
     */
    private String recvQueueName;

    /**
     * 8
     */
    public static final int sendOpenOptions = MQConstants.MQOO_OUTPUT | MQConstants.MQOO_FAIL_IF_QUIESCING;
    /**
     * 9
     */
    public static final int recvOpenOptions = MQConstants.MQOO_INPUT_AS_Q_DEF | MQConstants.MQOO_FAIL_IF_QUIESCING | MQConstants.MQOO_INQUIRE;

    /**
     * 10.队列管理器
     */
    private MQQueueManager mqMgr;

    /**
     * 11.发送队列
     */
    private MQQueue sendQueue;

    /**
     *12.接受队列
     */
    private MQQueue recvQueue;

   // public static final String ccsid = MQConfig.getInstance().getProperty("ccsid");

    private static final Logger logger  = LoggerFactory.getLogger(MQ.class);

    public MQ(){};

    public MQ(Boolean MQType,String queueManagerName,String hostName,int port,String channel,String sendQueueName,String recvQueueName){
        this.MQType = MQType;
        this.queueManagerName = queueManagerName;
        this.hostName = hostName;
        this.port = port;
        this.channel = channel;
        this.sendQueueName = sendQueueName;
        this.recvQueueName = recvQueueName;
        init();
    }

    public void init() {
        logger.info("初始化队列开始,队列管理器:{}",queueManagerName);
        Hashtable<String, Object> mqmProperties = new Hashtable<String, Object>();
        mqmProperties.put(MQConstants.HOST_NAME_PROPERTY, hostName);
        mqmProperties.put(MQConstants.PORT_PROPERTY, port);
        //mqmProperties.put(MQConstants.CCSID_PROPERTY, ccsid);
        mqmProperties.put(MQConstants.CHANNEL_PROPERTY, channel);
        mqmProperties.put(MQConstants.TRANSPORT_PROPERTY,MQConstants.TRANSPORT_MQSERIES);

        try {
            mqMgr = new MQQueueManager(queueManagerName,mqmProperties);
            sendQueue = mqMgr.accessQueue(sendQueueName, sendOpenOptions, null, null, null);
            recvQueue = mqMgr.accessQueue(recvQueueName, recvOpenOptions, null, null, null);
            logger.info("初始化队列结束,队列管理器:{}",queueManagerName);
        } catch (MQException e) {
            logger.warn("初始化失败,队列管理器:{}",queueManagerName);
            destroy();
            e.printStackTrace();
        } finally{}
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (true) {
            int depth = 0;
            // 查看队列深度
            try {
                depth = recvQueue.getCurrentDepth();
            } catch (MQException e) {
                logger.warn("查看队列深度异常:{}",e.toString());
                break;
            }
            if (depth == 0) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            // 有消息取出来 直到取到0
            logger.info("从队列获取消息数:{}",depth);
            while (depth-- > 0) {
                MQMessage msg = new MQMessage();
                MQGetMessageOptions gmo = new MQGetMessageOptions();
                if (MQType) {
                    try {
                        recvQueue.get(msg, gmo);
                    } catch (MQException e) {
                        logger.warn("当前服务获取队列消息异常，可忽略！");
                        try {
                            if(recvQueue.getCurrentDepth()== 0){
                                break;
                            }
                        } catch (MQException ee) {
                        }
                        continue;
                    }
                    MQSendTask pTask = new MQSendTask(msg, sendQueue);
                    //MQServer.getInstance().getThreadPool().execute(pTask);
                }
                if(!MQType){
                    try {
                        recvQueue.get(msg, gmo);
                        if(msg.getDataLength() > 0){
                            String response = msg.readStringOfByteLength(msg.getDataLength());
                            String newJson = StringEscapeUtils.unescapeHtml(response);
                            logger.info("作为请求方获取相应报文："+newJson);
                        }
                    } catch (MQException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    public void destroy(){
        try{
            if (mqMgr != null) {
                mqMgr.close();
                mqMgr.disconnect();
                mqMgr = null;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean getMQType() {
        return MQType;
    }

    public void setMQType(Boolean MQType) {
        this.MQType = MQType;
    }

    public String getQueueManagerName() {
        return queueManagerName;
    }

    public void setQueueManagerName(String queueManagerName) {
        this.queueManagerName = queueManagerName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSendQueueName() {
        return sendQueueName;
    }

    public void setSendQueueName(String sendQueueName) {
        this.sendQueueName = sendQueueName;
    }

    public String getRecvQueueName() {
        return recvQueueName;
    }

    public void setRecvQueueName(String recvQueueName) {
        this.recvQueueName = recvQueueName;
    }

    public MQQueueManager getMqMgr() {
        return mqMgr;
    }

    public void setMqMgr(MQQueueManager mqMgr) {
        this.mqMgr = mqMgr;
    }

    public MQQueue getSendQueue() {
        return sendQueue;
    }

    public void setSendQueue(MQQueue sendQueue) {
        this.sendQueue = sendQueue;
    }

    public MQQueue getRecvQueue() {
        return recvQueue;
    }

    public void setRecvQueue(MQQueue recvQueue) {
        this.recvQueue = recvQueue;
    }
}
