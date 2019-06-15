package alitest;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;

public class AliWriter implements Runnable {
    private String str;
    private Collection<String> collection;
    private int loopCount;
    private int counter = 0;
    private CountDownLatch signal;
    private Thread currentThread;
    private Thread nextThread;

    public AliWriter(
            String str,
            Collection<String> collection,
            int loopCount,
            CountDownLatch signal
    ) {
        this.str = str;
        this.collection = collection;
        this.loopCount = loopCount;
        this.signal = signal;
    }

    public void run() {
        while (counter < loopCount) {
            //Wait for the notification to start working
            LockSupport.park(currentThread);

            //Do work
            System.out.println(String.format("Writing: %s", str));
            this.collection.add(str);
            counter++;

            //Notify next thread to start working
            LockSupport.unpark(nextThread);
        }
        signal.countDown();
    }

    public Thread getCurrentThread() {
        return this.currentThread;
    }

    public void setCurrentThread(Thread thread) {
        this.currentThread = thread;
    }

    public void setNextThread(Thread thread) {
        this.nextThread = thread;
    }
}
