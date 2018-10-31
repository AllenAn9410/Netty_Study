package testReqAndResp;

import org.junit.jupiter.api.Test;
import reqAndResp.Server;

public class TestServer {
    @Test
    public void start() throws Exception {
        new Server(8082).start();
    }
}
