import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Router {
    public static void main(String[] args) throws Exception{
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new Server(5000));
        executorService.execute(new Server(5001));
        executorService.shutdown();
    }
}
