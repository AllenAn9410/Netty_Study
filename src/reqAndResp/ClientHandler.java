package reqAndResp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.CharsetUtil;

public class ClientHandler extends SimpleChannelInboundHandler<HttpObject> {
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        FullHttpResponse response = (FullHttpResponse) msg;
        String res = response.content().toString(CharsetUtil.UTF_8);
        System.err.println(res);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
