package Bank;

import Tasks.Task;

import java.util.ArrayList;

public class Bank {
    protected ATM[] ATMs;
    private final BankThreadPool bankExecutor;
    private int size = -1;
    private final int AtmCount;

    public Bank(int AtmCount) {
        this.AtmCount = AtmCount;
        ATMs = new ATM[AtmCount];
        for (int i = 0; i < AtmCount; i++)
            ATMs[i] = new ATM();
        bankExecutor = new BankThreadPool(AtmCount);
    }


    public synchronized void increaseSize() {
        size++;
    }

    public synchronized void decreaseSize() {
        size--;
    }


    public ArrayList<Object> runATM(ArrayList<Task> tasks, Handler handler) {
        ArrayList<Object> results = new ArrayList<>();
        bankExecutor.execute(() -> {
            long start = System.currentTimeMillis();
            increaseSize();
            int ATMIndex = this.size;
            for (Task task : tasks) {
                try {
                    task.setATM(ATMs[ATMIndex]);
                    System.out.println("current task: " + task + "on ATM no." + ATMIndex + " " + ATMs[ATMIndex]);
                    results.add(task.run());
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    results.add(ex);
                }
            }
            decreaseSize();
            handler.done();
            long end = System.currentTimeMillis();
            System.out.println("done tasks: " + tasks + " in " + (end - start) + "milliseconds");
        });
        return results;
    }

}
