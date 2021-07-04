package Tasks;

import Bank.ATM;
import Bank.Card;
import Model.Exceptions.InvalidCardNoException;
import Model.Exceptions.InvalidCashAmountException;
import Model.Exceptions.NoCardInsertedException;
import Model.Exceptions.NotEnoughBalanceException;
import Results.BalanceMovedResult;
import Results.Result;

public class MoveBalanceTask implements Task {
    private ATM atm;
    private final int amount;
    private final String destCardNo;

    public MoveBalanceTask(int amount, String destCardNo) {
        this.amount = amount;
        this.destCardNo = destCardNo;
    }

    @Override
    public Result run() throws Exception {
        Thread.sleep(1000);
        if (atm.isNoCardInserted())
            throw new NoCardInsertedException();
        if (atm.getCard().getBalance() < amount)
            throw new NotEnoughBalanceException();
        if (amount < 0)
            throw new InvalidCashAmountException();
        Card dest = Card.getCardByCardNo(destCardNo);
        if (dest == null)
            throw new InvalidCardNoException();
        atm.getCard().decreaseBalance(amount);
        dest.increaseBalance(amount);
        return new BalanceMovedResult();
    }

    @Override
    public void setATM(ATM atm) {
        this.atm = atm;
    }
}
