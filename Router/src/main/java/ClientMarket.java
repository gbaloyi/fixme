import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ClientMarket {

    public static void main(String[] args) {


        String string = "Message From Market";
        Market market = new Market(string);
        Thread thread = new Thread(market);
        thread.start();

    }

    static class Market implements Runnable {

        private String message = "";
        private Selector selector;


        public Market(String message){
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
                channel.connect(new InetSocketAddress("127.0.0.1", 5001));

                while (!Thread.interrupted()){

                    selector.select(1000);
                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                    while (keys.hasNext()){
                        SelectionKey key = keys.next();
                        keys.remove();

                        if (!key.isValid()) continue;

                        if (key.isConnectable()){
                            System.out.println("Market Connected To Server");
                            marketConnect(key);
                        }
                        if (key.isWritable()){
                            System.out.println("Market Writing To Server");
                            marketWrite(key);
                        }
                        if (key.isReadable()){
                            System.out.println("Market Reading From Server");
                            marketRead(key);
                        }
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                marketClose();
            }
        }

        private void marketClose(){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void marketRead (SelectionKey key) throws IOException {

            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int numRead = -1;
            numRead = channel.read(buffer);

            if (numRead == -1) {
                key.cancel();
                return;
            }

            byte[] data = new byte[numRead];
            System.arraycopy(buffer.array(), 0, data, 0, numRead);
            System.out.println("Market: " + new String(data));
        }

        private void marketWrite(SelectionKey key) throws IOException {
            SocketChannel channel = (SocketChannel) key.channel();
            channel.write(ByteBuffer.wrap(message.getBytes()));
            key.interestOps(SelectionKey.OP_READ);
        }

        private void marketConnect(SelectionKey key) throws IOException {
            SocketChannel channel = (SocketChannel) key.channel();
            if (channel.isConnectionPending()){
                channel.finishConnect();
            }
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_WRITE);
        }
    }
}
