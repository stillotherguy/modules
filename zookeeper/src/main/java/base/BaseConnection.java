package base;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Ethan on 14-9-6.
 */
public class BaseConnection implements Watcher{
    private static final int SESSION_TIMEOUT = 5000;

    private CountDownLatch connectedSignal = new CountDownLatch(1);
    protected ZooKeeper zk;

    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
            connectedSignal.countDown();
        }
    }

    public void connect(String hosts) throws IOException, InterruptedException {
        zk = new ZooKeeper(hosts,SESSION_TIMEOUT,this);
        connectedSignal.await();
    }

    public void close() throws InterruptedException {
        zk.close();
    }

}
