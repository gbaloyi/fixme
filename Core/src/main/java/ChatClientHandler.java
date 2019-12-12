import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;


public class ChatClientHandler extends ChannelInboundMessageHandlerAdapter<String> {
    private static final ChannelGroup channels = new DefaultChannelGroup();

    @Override
    public void messageReceived(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println(s);
    }

}
