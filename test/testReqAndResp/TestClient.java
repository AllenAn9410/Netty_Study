package testReqAndResp;


import reqAndResp.Client;

public class TestClient {

    public void testClient() throws Exception {
        new Client(8082).start();
    }

}
