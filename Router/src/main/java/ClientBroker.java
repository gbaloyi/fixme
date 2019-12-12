import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ClientBroker {

    public static void main(String[] args) {


        String string = "Message From Broker";
        Broker broker = new Broker(string);
        Thread thread = new Thread(broker);
        thread.start();

    }

    static class Broker implements Runnable {

        private String message = "";
        private Selector selector;

        public Broker(String message){
            this.message = message;
        }

        @Override
        public void run() {
            SocketChannel channel;
            try {
                selector = Selector.open();
                channel = SocketChannel.open();
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_CONNECT);
                channel.connect(new InetSocketAddress("127.0.0.1", 5000));

                while (!Thread.interrupted()){

                    selector.select(1000);
                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                    while (keys.hasNext()){
                        SelectionKey key = keys.next();
                        keys.remove();

                        if (!key.isValid()) continue;

                        if (key.isConnectable()){
                            System.out.println("Broker Connected To Server");
                            brokerConnect(key);
                        }
                        if (key.isWritable()){
                            System.out.println("Broker Writing To Server");
                            brokerWrite(key);
                        }
                        if (key.isReadable()){
                            System.out.println("Broker Reading From Server");
                            brokerRead(key);
                        }
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                brokerClose();
            }
        }

        private void brokerClose(){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void brokerRead (SelectionKey key) throws IOException {

            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int numRead = -1;
            numRead = channel.read(buffer);

            if (numRead == -1) {

                channel.close();
                key.cancel();
                return;
            }

            byte[] data = new byte[numRead];
            System.arraycopy(buffer.array(), 0, data, 0, numRead);
            System.out.println("Broker: " + new String(data));

        }

        private void brokerWrite(SelectionKey key) throws IOException {
            SocketChannel channel = (SocketChannel) key.channel();
            channel.write(ByteBuffer.wrap(message.getBytes()));
            ByteBuffer buffer = ByteBuffer.allocate(74);
            buffer.clear();
            key.interestOps(SelectionKey.OP_READ);
        }

        private void brokerConnect(SelectionKey key) throws IOException {
            SocketChannel channel = (SocketChannel) key.channel();
            if (channel.isConnectionPending()){
                channel.finishConnect();
            }
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_WRITE);
        }
    }
}
