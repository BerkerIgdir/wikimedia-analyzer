import business.ConcurrentQuerier;
import consumer.WikiMediaConsumerImpl;


import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class WikiMediaConsumerApp {
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final WikiMediaConsumerImpl consumer = new WikiMediaConsumerImpl();
    public static void main(String[] args) throws IOException {
        int port = 9099;
//        var querier = new ConcurrentQuerier<>(consumer.getCurrentServices(),port);
//        executorService.scheduleAtFixedRate(querier,20,5, TimeUnit.SECONDS);
        consumer.startConsuming();
        executorService.shutdown();
    }
}
