package http;

import com.cs.esp.org.json.JSONObject;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestSend {
    private String  path = "http://10.39.101.30:8082";
    @Test
    public void testSend() throws Exception {
        String res = post(path + "/ESP/mfvr", "{1111/aaaaaaa/11111}");
        System.err.println(res);
    }


    public static String post(String url, Object param) throws Exception {
        String data;
        String ctype;
        if (param == null) {
            data = "";
            ctype = ContentType.TEXT_PLAIN.getMimeType();
        } else if (param instanceof JSONObject) {
            data = ((JSONObject) param).toString('"');
            ctype = ContentType.APPLICATION_JSON.getMimeType();
        } else {
            data = param.toString();
            ctype = ContentType.TEXT_PLAIN.getMimeType();
        }
        String fullurl = url;

        String output = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost(fullurl);
            StringEntity input = new StringEntity(data);
            input.setContentType(ctype);
            post.setHeader("connection","close");
            // post.setHeader(HTTP_HEADER_ESP_TOKEN, getInstance().getToken());
            post.setEntity(input);

            response = httpclient.execute(post);

            // @author ben.pan @date 2017-05-23 @ref EE-8728 : if code > 400 mean error edit_s
            int httpStatus = response.getStatusLine().getStatusCode();
            if (httpStatus == HttpStatus.SC_UNAUTHORIZED) {
                // may server restart, so try more time
                response.close();
                //getInstance().espToken = null;
               // post.setHeader(HTTP_HEADER_ESP_TOKEN, getInstance().getToken());
                response = httpclient.execute(post);
                httpStatus = response.getStatusLine().getStatusCode();
            }
            if (httpStatus > HttpStatus.SC_BAD_REQUEST) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().toString());
            }
            // @author ben.pan @date 2017-05-23 @ref EE-8728 edit_s
            output = EntityUtils.toString(response.getEntity());
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                httpclient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return output;
    }
}
