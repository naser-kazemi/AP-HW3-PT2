package Bank;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BankThreadPool {

    private final BlockingQueue<Runnable> workerQueue;
    private final List<WorkerThread> workerThreads = new ArrayList<>();
    private final int poolSize;
    private boolean isStopped = false;

    public BankThreadPool(int poolSize) {
        workerQueue = new ArrayBlockingQueue<>(poolSize);
        this.poolSize = poolSize;
        for (int i = 0; i < poolSize; i++) {
            workerThreads.add(new WorkerThread(workerQueue));
        }
        for (WorkerThread workerThread : workerThreads)
            workerThread.start();
    }


    public void execute(Runnable runnable) {
        if (this.isStopped)
            throw new IllegalStateException("BankTreadPool is Stopped");
        try {
            this.workerQueue.put(runnable);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            this.stop();
        }
    }


    public int getPoolSize() {
        return this.poolSize;
    }

    public List<WorkerThread> getWorkerThreads() {
        return this.workerThreads;
    }

    public BlockingQueue<Runnable> getWorkerQueue() {
        return this.workerQueue;
    }

    public synchronized void stop() {
        this.isStopped = true;
        for (WorkerThread workerThread : workerThreads)
            workerThread.stopThread();
    }


}
