package ibm.mq;

import java.util.concurrent.ExecutorService;

public class MQServer {
    private ExecutorService threadPool;

    private static MQServer instance;

    public static MQServer getInstance(){
        if(instance == null){
            instance = new MQServer();
        }
        return instance;
    }
}
