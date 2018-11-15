package reqAndResp;

import com.cs.esp.org.json.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ServerHandler extends ChannelInboundHandlerAdapter {


    public void channelRead(ChannelHandlerContext ctx, Object msgs) throws Exception {
        FullHttpRequest msg = (FullHttpRequest) msgs;

        returnResp(ctx,msg);

    }

    private void returnResp(ChannelHandlerContext ctx,FullHttpRequest msg) throws Exception {
        StringBuilder responseContent = new StringBuilder();
        FullHttpResponse response = null;
        boolean keepAlive = HttpUtil.isKeepAlive(msg);
        // String host = msg.headers().get("HOST");
        String host = "";

        JSONObject json = new JSONObject();
        URI uri = new URI(msg.uri());
        System.out.println(uri.toString());
        String body = getBody(msg);
        System.out.println(body);
        if(body.length() != 0){
        //if(true){
            json.put("status","pass");
            json.put("msg","ok");
        }
        responseContent.append(json.toString('"'));


        // System.out.println("keepAlive : " + keepAlive);
        response = new DefaultFullHttpResponse(
                    HTTP_1_1, msg.decoderResult().isSuccess()? OK : BAD_REQUEST,
                    Unpooled.copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set("Access-Control-Allow-Origin","*");
        response.headers().set("Access-Control-Allow-Methods","POST, GET, OPTIONS");
        response.headers().set("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, esp_token");

        if (keepAlive) {
                // Add 'Content-Length' header only for a keep-alive connection.
                response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
                // Add keep alive header as per:
                // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            String cookieString = msg.headers().get(HttpHeaderNames.COOKIE);
            if (cookieString != null) {
                Set<io.netty.handler.codec.http.cookie.Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieString);
                if (!cookies.isEmpty()) {
                    // Reset the cookies if necessary.
                    for (io.netty.handler.codec.http.cookie.Cookie cookie: cookies) {
                        response.headers().add(HttpHeaderNames.SET_COOKIE, io.netty.handler.codec.http.cookie.ServerCookieEncoder.STRICT.encode(cookie));
                    }
                }
            } else {
                // Browser sent no cookie.  Add some.
                response.headers().add(HttpHeaderNames.SET_COOKIE, io.netty.handler.codec.http.cookie.ServerCookieEncoder.STRICT.encode("key1", "value1"));
                response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode("key2", "value2"));
            }

        // Write the response.
        ctx.writeAndFlush(response);
        if(!keepAlive){
            // If keep-alive is off, close the connection once the content is fully written.
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }

    private String getBody(FullHttpRequest request){
        ByteBuf buf = request.content();
        return buf.toString(CharsetUtil.UTF_8);
    }

    private boolean isEmpty(String str){
        if(str == null || str.length() == 0){
            return true;
        }
        return false;
    }

}
