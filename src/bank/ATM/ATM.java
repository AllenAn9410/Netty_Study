package bank.ATM;

import bank.card.Card;

public interface ATM {
    void show();

    void service();

    void getCard(Card c);
}
