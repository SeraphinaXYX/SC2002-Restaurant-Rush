# SC2002 Restaurant Rush

A turn-based restaurant management game for the command line, written in Java.
You are the manager: decide who gets seated, whose order goes first and what the
kitchen cooks, and try to hit the targets within 18 turns.

Repository: https://github.com/SeraphinaXYX/SC2002-Restaurant-Rush

## Build and run

Needs JDK 17 or newer.

```
javac -d bin src/restaurantrush/*.java
java -cp bin restaurantrush.RestaurantRush
```

(On Windows CMD use `src\restaurantrush\*.java`.) You pick the stage, then a random seed;
pressing Enter uses 42. The seed decides what each customer orders, so the same
seed and the same choices always give the same game.

## Stage 1 (tag `stage-1`)

Base setting from the brief: 18 turns, 2 tables, one Waiter, one Chef and one
Cashier, Regular customers arriving on turns 1, 4, 7, 10, 13 and 16.

- Waiter: one task per turn (seat a queued customer, take an order, or serve a READY dish)
- Chef: one preparation unit per turn
- Cashier: automatic, one payment per turn, oldest first

Win at the end of turn 18 with at least 4 paid customers, at least $45 revenue and an
average satisfaction (of paid customers) of at least 60.

## Stage 2 (tag `stage-2`)

Two new customer types arrive in place of the Regular customers on turns 7 and 13,
so there are still six arrivals:

| Type | Loss per waiting turn | Bill | Special behaviour |
|------|----|------|----|
| Regular | 8 | full price | seeded random dish |
| VIP | 5 | 10% off | seeded random dish |
| Critic | 12 | full price | always orders the most expensive dish; served within 2 turns of ordering gives +5 satisfaction, 3 or more turns gives -10 |

The differences live in `VIPCustomer` and `CriticCustomer` (overriding
`getSatisfactionLoss`, `applyDiscount`, `chooseItem`, `onServed`), so the
controller has no type checks.

Changes from Stage 1 ([compare](https://github.com/SeraphinaXYX/SC2002-Restaurant-Rush/compare/stage-1...stage-2)):

- New: `VIPCustomer.java`, `CriticCustomer.java`
- `Customer.java`: added the `onServed(int waitTurns)` hook; `receiveDish` now takes the wait time and calls it. Needed for the Critic's reaction.
- `Waiter.java`: `serve` passes the wait time and reports a satisfaction change if the customer reacts.
- `GameConfig.java`: now knows the stage; `createArrival` returns a VIP on turn 7 and a Critic on turn 13 from Stage 2 (a small registration change).
- `GameView.java` / `RestaurantRush.java`: stage selection prompt.
- Nothing deleted, turn rules untouched: the Stage 1 runs in `test-runs/` replay with identical output.

Reproducible runs are in `test-runs/`, see `TESTING.md`.
