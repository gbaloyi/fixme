import java.nio.channels.ServerSocketChannel;

public class RoutingTable {

    int id;
    ServerSocketChannel channel;

    public RoutingTable(int id, ServerSocketChannel channel){
        this.id = id;
        this.channel = channel;
    }

//    public int setChannel(int channel) {
//        this.channel = channel;
//        return channel;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public int getChannel() {
//        return channel;
//    }
//
//    public int getId() {
//        return id;
//    }
}
