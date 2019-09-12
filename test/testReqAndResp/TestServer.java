package testReqAndResp;

import reqAndResp.Server;

public class TestServer {
    public void start() throws Exception {
        new Server(8082).start();
    }
}
