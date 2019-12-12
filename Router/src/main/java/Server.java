import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Server implements Runnable {

    public final static String ADDRESS = "127.0.0.1";
    public static int port;
    public final static long TIMEOUT = 10000;
    public int id = 100000;
    ClientBroker broker = new ClientBroker();
    ClientMarket market = new ClientMarket();

    private ServerSocketChannel serverChannel;
    private Selector selector;
    private Map<SocketChannel,byte[]> dataTracking = new HashMap<SocketChannel, byte[]>();
     public Server(int port){
        this.port = port;
        init();
    }

    public static void main(String[] args) {
        new Server(port).run();
    }

    private void init(){
        System.out.println("Server Is Starting...");
        if (selector != null) return;
        if (serverChannel != null) return;
        int id = 100000;

        try {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(ADDRESS, port));
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Server Accepting Connections From Clients...");
        try{
           while (!Thread.currentThread().isInterrupted()){
                selector.select(TIMEOUT);
                id++;
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()){
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (!key.isValid()){
                        continue;
                    }
                    if (key.isAcceptable()){
                        System.out.println("Server Is Accepting Connections...");
                        accept(key);
                    }
                    if (key.isWritable()){
                        System.out.println("Server Is Writing");
                        write(key);
                    }
                    if (key.isReadable()){
                        System.out.println("Server Is Reading");
                        read(key);
                    }
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally{
            closeConnection();
        }

    }
    private void write(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel) key.channel();
        byte[] data = dataTracking.get(channel);
        dataTracking.remove(channel);
        channel.write(ByteBuffer.wrap(data));
        key.interestOps(SelectionKey.OP_READ);


    }
    private void closeConnection(){
        System.out.println("Server Shutdown");
        if (selector != null){
            try {
                selector.close();
                serverChannel.socket().close();
                serverChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void accept(SelectionKey key) throws IOException{
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        socketChannel.register(selector, SelectionKey.OP_WRITE);
        byte[] hello = new String("Message From Server").getBytes();
        dataTracking.put(socketChannel, hello);

        id++;
        new RoutingTable(id, serverChannel);
        System.out.println("Id is:" + id + " And Channel Is: " + serverChannel);
        List<RoutingTable> routingTables = new ArrayList<RoutingTable>();
        RoutingTable routingTable1 = new RoutingTable(id, serverChannel);
        routingTables.add(routingTable1);

        for(int i = 0;i < routingTables.size(); i++){
            RoutingTable rout = (RoutingTable)routingTables.get(i);
            System.out.println(routingTables.get(i));
        }

    }



    private void read(SelectionKey key) throws IOException{
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int numRead = -1;
            numRead = channel.read(buffer);

            if (numRead == -1) {
                Socket socket = channel.socket();
                SocketAddress remoteAddr = socket.getRemoteSocketAddress();
                System.out.println("Connection closed by client: " + remoteAddr);
                channel.close();
                key.cancel();
                return;
            }

            byte[] data = new byte[numRead];
            System.arraycopy(buffer.array(), 0, data, 0, numRead);
            System.out.println("Server: " + new String(data));

    }

}