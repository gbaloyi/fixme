import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ChatServer {
    private final int port;

    public static void main(String[] args) {
        new ChatServer(5000).run();
    }
    public ChatServer(int port){
        this.port = port;
    }

    public void run(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();


        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap().group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChatServerInitializer());
            serverBootstrap.bind(port).sync().channel().closeFuture().sync();
            System.out.println("After");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            }
        }
    }

