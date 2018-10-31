package testReqAndResp;

import org.junit.jupiter.api.Test;
import reqAndResp.Client;

public class TestClient {
    @Test
    public void testClient() throws Exception {
        new Client(8082).start();
    }

}
