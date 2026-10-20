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

(On Windows CMD use `src\restaurantrush\*.java`.) You are asked for a random seed;
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

Reproducible runs are in `test-runs/`, see `TESTING.md`.
