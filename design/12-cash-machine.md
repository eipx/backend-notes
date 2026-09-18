# Cash dispensing machine

## Requirements
- Card insert, PIN entry, balance inquiry, withdrawal, deposit.
- Dispense physical cash using the fewest notes across a set of cassettes, one cassette per denomination.
- The debit (account balance) and the dispense (physical cash) must never both happen without the other completing: no dispensing without a successful debit, and no debit that silently loses the cash if the dispenser jams.
- Every session and every dispense attempt is recorded for audit.

## Core abstractions
- `SessionState` (the state pattern): `IdleState`, `CardInsertedState`, `PinVerifiedState`, each restricting which actions are valid.
- `AtmSession`: holds the current `SessionState` and delegates action requests to it.
- `DenominationHandler` (chain of responsibility): one per cassette, each contributes as many notes of its denomination as it can toward the remaining amount and passes the remainder down the chain.
- `CashCassette`: tracks a denomination and a note count.
- `TransactionLedger`: appends an audit record for every debit and dispense attempt, including partial failures.

## Java 8 skeleton
```java
public interface SessionState {
    SessionState insertCard(AtmSession session, String cardId);
    SessionState enterPin(AtmSession session, String pin);
    SessionState withdraw(AtmSession session, long amountCents);
}

public class AtmSession {
    private SessionState state;
    private String cardId;

    public AtmSession(SessionState initialState) { this.state = initialState; }
    public void insertCard(String cardId) { this.state = state.insertCard(this, cardId); }
    public void enterPin(String pin) { this.state = state.enterPin(this, pin); }
    public void withdraw(long amountCents) { this.state = state.withdraw(this, amountCents); }
    public void setCardId(String cardId) { this.cardId = cardId; }
    public String getCardId() { return cardId; }
}

public class IdleState implements SessionState {
    public SessionState insertCard(AtmSession session, String cardId) {
        session.setCardId(cardId);
        return new CardInsertedState();
    }
    public SessionState enterPin(AtmSession session, String pin) {
        throw new IllegalStateException("no card inserted");
    }
    public SessionState withdraw(AtmSession session, long amountCents) {
        throw new IllegalStateException("no card inserted");
    }
}

public class CardInsertedState implements SessionState {
    public SessionState insertCard(AtmSession session, String cardId) {
        throw new IllegalStateException("card already inserted");
    }
    public SessionState enterPin(AtmSession session, String pin) {
        return PinValidator.isValid(session.getCardId(), pin) ? new PinVerifiedState() : new CardInsertedState();
    }
    public SessionState withdraw(AtmSession session, long amountCents) {
        throw new IllegalStateException("pin not verified");
    }
}

public interface DenominationHandler {
    void setNext(DenominationHandler next);
    long dispense(long remainingCents);
}

public class CassetteHandler implements DenominationHandler {
    private final CashCassette cassette;
    private DenominationHandler next;

    public CassetteHandler(CashCassette cassette) { this.cassette = cassette; }
    public void setNext(DenominationHandler next) { this.next = next; }

    public long dispense(long remainingCents) {
        long denomination = cassette.getDenominationCents();
        long notesNeeded = remainingCents / denomination;
        long notesAvailable = Math.min(notesNeeded, cassette.getNoteCount());
        cassette.dispense(notesAvailable);
        long remainder = remainingCents - (notesAvailable * denomination);
        return (next == null || remainder == 0) ? remainder : next.dispense(remainder);
    }
}
```

## Design patterns used and why
The session is the **State** pattern: each state (card inserted, PIN verified, and so on) exposes the same interface and only the valid transitions are implemented, so an out-of-order action (withdraw before PIN entry) fails by construction rather than an `if` chain scattered through one class. Dispensing is **Chain of Responsibility**: each `CassetteHandler` takes what it can of the remaining amount and passes the rest along, so the machine does not need a single method that knows about every denomination at once.

## Extension points
A new denomination is a new `CashCassette` plus a new `CassetteHandler` link added to the chain; no change to the handlers already in the chain. A new session step (a receipt preference prompt) is a new `SessionState` implementation slotted between existing ones, without changing the states on either side beyond returning the new one from a transition.

## Concurrency
A physical machine typically serves one session at a time, so the state machine itself needs no locking. What does need care is the **debit and dispense boundary**: debit the account first inside a transaction, then attempt to dispense; if the dispenser jams or reports a mechanical failure after the debit succeeded, the machine must not eat the money. Reverse the debit with a compensating credit if the dispense confirms failure, and if the dispense outcome is unknown (the machine cannot tell if notes came out before it lost power), hold the funds and flag the transaction for manual reconciliation rather than guessing either way.

## Testing approach
Unit test each `SessionState` transition, including the illegal ones (`withdraw` from `IdleState` throws). Test the denomination chain against edge cases: an amount that cannot be made exactly with the cassettes on hand, a cassette that runs out mid-chain, and confirm the chain always passes the correct remainder down rather than losing cents. Test the debit-dispense boundary with a fake dispenser that reports jam, success, and unknown outcomes, and assert the ledger records the correct compensating action for each.

## Follow-up questions
1. What happens if the dispenser jams after the debit succeeds? The ledger already recorded the debit; issue a compensating credit once the jam is confirmed, or hold the case for manual reconciliation if the outcome is ambiguous.
2. Why state pattern instead of a boolean flag per step? Flags allow invalid combinations (PIN verified but no card) that the compiler cannot catch; a state object only exposes the transitions valid from that state.
3. How do you guarantee an amount that cannot be dispensed exactly (not divisible by the smallest denomination) is rejected up front? The chain returns a nonzero remainder at the end; check the requested amount against cassette denominations before attempting any dispense.
4. How would you add a deposit flow? A new state for deposit-in-progress and a note-counting device abstraction parallel to `CashCassette`, without changing the withdrawal chain.
5. How is the audit log kept tamper-evident? Append-only storage, each record referencing the previous record's hash, and a separate read-only path for auditors that cannot write.
6. Why chain of responsibility instead of just sorting cassettes and looping? The chain generalizes better once a cassette can refuse to dispense for reasons beyond running out (a maintenance lockout on one cassette), without changing the calling code.
