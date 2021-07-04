package Tasks;

import Bank.ATM;
import Results.Result;

public interface Task {
    Result run() throws Exception;

    void setATM(ATM atm);
}
