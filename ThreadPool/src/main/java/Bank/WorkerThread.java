package Bank;

import java.util.concurrent.BlockingQueue;

public class WorkerThread extends Thread {

    private final BlockingQueue<Runnable> taskQueue;
    private boolean isStopped = false;


    public WorkerThread(BlockingQueue<Runnable> queue) {
        this.taskQueue = queue;
    }


    @Override
    public void run() {
        while (!isStopped) {
            try {
                Runnable runnable = taskQueue.take();
                runnable.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public synchronized void stopThread() {
        isStopped = true;
    }


    public synchronized boolean isStopped() {
        return this.isStopped;
    }


}
