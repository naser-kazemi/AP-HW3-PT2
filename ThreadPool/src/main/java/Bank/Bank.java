package Bank;

import Tasks.Task;

import java.util.ArrayList;

public class Bank {
    protected ATM[] ATMs;
    private final BankThreadPool bankExecutor;
    private final int AtmCount;
    private boolean[] busy;

    public Bank(int AtmCount) {
        this.AtmCount = AtmCount;
        ATMs = new ATM[AtmCount];
        busy = new boolean[AtmCount];
        for (int i = 0; i < AtmCount; i++)
            ATMs[i] = new ATM();
        bankExecutor = new BankThreadPool(AtmCount);
    }


    public ArrayList<Object> runATM(ArrayList<Task> tasks, Handler handler) {
        ArrayList<Object> results = new ArrayList<>();
        bankExecutor.execute(() -> {
            long start = System.currentTimeMillis();
            int ATMIndex = getFirstFreeATMIndex();
            for (Task task : tasks) {
                try {
                    occupyATM(ATMIndex);
                    task.setATM(ATMs[ATMIndex]);
                    System.out.println("current task: " + task + " on ATM no." + ATMIndex + " " + ATMs[ATMIndex]);
                    results.add(task.run());
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    results.add(ex);
                }
            }
            freeATM(ATMIndex);
            handler.done();
            long end = System.currentTimeMillis();
            System.out.println("done tasks: " + tasks + " in " + (end - start) + "milliseconds on ATM no." + ATMIndex);
        });
        return results;
    }


    private synchronized void occupyATM(int index) {
        busy[index] = true;
    }

    private synchronized void freeATM(int index) {
        try {
            busy[index] = false;
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }


    private synchronized int getFirstFreeATMIndex() {
        for (int i = 0; i < AtmCount; i++) {
            if (!busy[i])
                return i;
        }
        return -1;
    }


}
