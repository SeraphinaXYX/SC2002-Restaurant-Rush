# SC2002 Restaurant Rush

A turn-based restaurant management game for the command line, written in Java.
You are the manager: staff time is limited, so you decide who gets seated, whose
order is taken and served first, and what the kitchen works on. Hit the targets
by the end of turn 18 to win.

Repository: https://github.com/SeraphinaXYX/SC2002-Restaurant-Rush

## Team

| Name | Matric No. | Main parts |
|------|-----------|------------|
| Member A | U2XXXXXXX | Turn loop and game rules (GameController, GameConfig) |
| Member B | U2XXXXXXX | Domain model (Customer, Order, Table, Restaurant) |
| Member C | U2XXXXXXX | Staff classes, ActionResult; Stage 2 customers |
| Member D | U2XXXXXXX | Stage 3 (Host, ComboMeal, Menu, HappyHour) |
| Member E | U2XXXXXXX | CLI (GameView), UML diagrams, report, video |

## Build and run

Needs JDK 17 or newer (it uses switch expressions, so Java 11 is not enough), no other libraries and no network access.

Linux / macOS:
```
javac -d bin src/restaurantrush/*.java
java -cp bin restaurantrush.RestaurantRush
```
Windows (CMD or PowerShell):
```
javac -d bin src\restaurantrush\*.java
java -cp bin restaurantrush.RestaurantRush
```

You pick a stage (1, 2 or 3) and a random seed (Enter = 42). The seed only decides
what customers order, so the same stage, seed and choices always replay the same game.

## How a turn works

1. **Arrivals** - on turns 1, 4, 7, 10, 13 and 16 one customer joins the queue at satisfaction 100.
2. **Manager choices** - Host (Stage 3) may seat one queued customer; the Waiter does one task
   (seat, take an order, or serve a READY dish); the Chef does one preparation unit.
   An impossible choice (occupied table, dish not ready, ...) prints the reason and asks again
   without using up that staff member's task.
3. **Kitchen and serving** - the Waiter acts before the Chef, so a dish finished this turn is served next turn at the earliest.
4. **Eating and payment** - a customer served on an earlier turn finishes eating and becomes READY_TO_PAY;
   the Cashier takes one payment per turn, oldest first, never from someone who became READY_TO_PAY this turn.
5. **Waiting and abandonment** - everyone still waiting for a table or food loses satisfaction; at 0 they leave
   without paying and their table is freed.
6. **Result** - after turn 18: Victory with at least 4 paid customers, at least $45 revenue and an average
   satisfaction (recorded at payment) of at least 60. Otherwise Defeat.

Menu: Salad $9.00 (1 unit), Burger $15.00 (1 unit), Pasta $18.00 (2 units). All money is stored
as integer cents. The customer arriving on turn 16 can never pay in time; that is intended.

## Stage 1 (tag `stage-1`)

Regular customers, the three dishes, Waiter / Chef / Cashier, tables, waiting queue,
the full six-step turn and both endings.

## Stage 2 (tag `stage-2`)

The turn 7 and turn 13 arrivals become a VIP and a Critic (still six arrivals).

| Type | Loss per waiting turn | Bill | Ordering / reaction |
|------|----|------|----|
| Regular | 8 | full price | seeded random |
| VIP | 5 | 10% off | seeded random |
| Critic | 12 | full price | always the most expensive single dish (Pasta); served within 2 turns of ordering: +5 satisfaction, 3 or more turns: -10 |

All three go through the same order and service flow. The differences are overridden
methods (`getSatisfactionLoss`, `applyDiscount`, `chooseItem`, `onServed`), so the
controller contains no type checks.

Changes from Stage 1 ([compare stage-1...stage-2](https://github.com/SeraphinaXYX/SC2002-Restaurant-Rush/compare/stage-1...stage-2)):

| File | Change | Why |
|------|--------|-----|
| `VIPCustomer.java`, `CriticCustomer.java` | new | the two new customer types |
| `Customer.java` | added `onServed(int waitTurns)` hook; `receiveDish` takes the wait time | the Critic reacts to service speed |
| `Waiter.java` | `serve` passes the wait time and reports any reaction | so the player sees the Critic's reaction |
| `GameConfig.java` | stores the stage; `createArrival` returns VIP on turn 7 and Critic on turn 13 | small registration change |
| `GameView.java`, `RestaurantRush.java` | stage selection prompt | |

Nothing was deleted and no turn rule changed: the Stage 1 runs replay with identical output.

## Stage 3 (tag `stage-3`)

- **Host** - seats at most one queued customer per turn, before and in addition to the Waiter.
- **ComboMeal** - two existing dishes; prep units = sum, price = 90% of the sum rounded to the nearest cent
  (Salad + Pasta = $24.30, 3 units). Order rule: when combos are on the menu, a customer has a 30% seeded
  chance of ordering a random combo, otherwise a random single dish. Critics never order combos.
- **Happy Hour** - can be switched on once per game at the start of a turn; lasts that turn and the next
  (remaining turns are shown). Orders taken while it is on get 20% off, applied after the VIP discount, and the
  price is locked into the order (VIP Burger: $15 x 0.9 x 0.8 = $10.80). At the start of each active turn,
  customers still waiting for a table or food recover 15 satisfaction (max 100). Paid or departed customers
  and orders already placed are not affected.

Changes from Stage 2 ([compare stage-2...stage-3](https://github.com/SeraphinaXYX/SC2002-Restaurant-Rush/compare/stage-2...stage-3)):

| File | Change | Why |
|------|--------|-----|
| `Host.java`, `ComboMeal.java`, `HappyHour.java` | new | the three Stage 3 features |
| `Menu.java` | constructor takes `includeCombos`; added `getCombos`, `hasCombos`, `getItems` | combos are built from the existing dishes |
| `Customer.java` | `pickDefault` adds the 30% combo rule; added `recoverSatisfaction` | combo choice is the customer's, Happy Hour recovery |
| `Waiter.java` | `takeOrder` takes the (possibly null) `HappyHour` and applies it after the customer discount | price is locked when the order is taken |
| `GameConfig.java` | `hasHost`, `hasHappyHour`, `hasCombos` | stage switches |
| `GameController.java` | Host phase before the Waiter; Happy Hour prompt, recovery and `endTurn` | wiring of the new features |
| `GameView.java` | Happy Hour prompt, status line and recovery message; combos in the menu listing | display only |
| `Staff.java`, `Host.java`, `Waiter.java` | new protected `Staff.seatCustomer` used by both `Waiter.seat` and `Host.seat` | after review we noticed the two seat methods were copies of each other |
| `Customer.java`, `RegularCustomer.java`, `VIPCustomer.java`, `CriticCustomer.java` | constructors no longer take the arrival turn | the field was never read |
| `Order.java`, `WaitingQueue.java` | removed `getPrepDone` and `size` | unused |

Nothing was deleted. The combo rule only draws an extra random number when combos exist, so Stage 1 and 2 games
(and their recorded runs) are unchanged in the Stage 3 build. The last commit on the `stage-3` tag is a tidy-up
after our own code review: on Happy Hour turns the state is now printed after the +15 recovery, so the numbers
on screen are the ones the turn really uses.

## Design overview

| Class | Kind | Responsibility |
|-------|------|----------------|
| `RestaurantRush` | boundary | entry point |
| `GameView` | boundary | all console input/output, no rules |
| `GameController` | control | runs the six turn steps, asks the view for choices, hands them to staff |
| `GameConfig` | control | base setting, stage switches, who arrives when |
| `Restaurant` | entity | tables, queue, customers, orders, revenue and the statistics |
| `Staff` / `Host`, `Waiter`, `Chef`, `Cashier` | entity | one class per role; actions return `ActionResult` |
| `ActionResult` | entity | success message or the reason an action is not allowed |
| `Customer` / `RegularCustomer`, `VIPCustomer`, `CriticCustomer` | entity | satisfaction, discount, order choice, reaction to service |
| `CustomerState`, `OrderStatus` | enums | life cycles of a visit and of an order |
| `OrderItem` / `MenuItem`, `ComboMeal` | interface, enum, class | anything that can be ordered |
| `Menu`, `Order`, `Table`, `WaitingQueue`, `HappyHour` | entity | as named |

Full diagrams and discussion are in the [PDF report](report/SC2002_Restaurant_Rush_Report.pdf).

## Reproducible runs

See [TESTING.md](TESTING.md). Each file in `test-runs/` is the exact input for one game:

```
java -cp bin restaurantrush.RestaurantRush --echo < test-runs/stage3-victory.txt
```

| Run | What it shows | Result |
|-----|---------------|--------|
| stage1-victory | the Pasta example from the brief (92 / 84 / 76, paid $18.00 on turn 6) | Victory: 5 paid, $63.00, avg 74.4 |
| stage1-defeat | Chef never cooks, two customers walk out | Defeat: 0 paid, $0.00 |
| stage1-invalid-input | rejected choices print the reason and ask again | Defeat: 1 paid, $18.00, avg 76.0 |
| stage2-victory | VIP pays $8.10 for a Salad, Critic reacts +5 to quick service | Victory: 4 paid, $53.10, avg 76.3 |
| stage2-defeat | same as stage 1 defeat with VIP and Critic | Defeat: 0 paid, $0.00 |
| stage3-victory | Host, combo meals, Happy Hour from turn 7 | Victory: 5 paid, $88.45, avg 83.2 |
| stage3-defeat | Host waits, Chef never cooks | Defeat: 0 paid, $0.00 |
| stage3-vip-happy-hour-seed1 | VIP Burger during Happy Hour: $10.80 (seed 1) | Victory: 5 paid, $73.80, avg 91.4 |
| stage3-happy-hour-after-payment | Happy Hour does not touch customers who already paid | Victory: 5 paid, $88.74, avg 87.6 |
| extra-host-and-waiter-seat | turn 4: Host seats Regular #1 and the Waiter seats Regular #2 in the same turn | Defeat: 0 paid (Chef never cooks) |
| extra-critic-slow-service | Critic ordered on turn 15, served on turn 18, reacts 40 -> 30 | Victory: 4 paid, $53.10, avg 76.3 |

## Report

The PDF report (class diagram, sequence diagram, design discussion, functional evidence and
reflections) is in [report/SC2002_Restaurant_Rush_Report.pdf](report/SC2002_Restaurant_Rush_Report.pdf).
The two UML diagrams are also saved on their own in [report/diagrams/](report/diagrams/) so they are easier
to zoom into, and the LaTeX source is in [report/latex/](report/latex/).

## Video timestamps

| # | Segment | Presenter | Time |
|---|---------|-----------|------|
| 1 | Game launching from the submitted project | Member E | TBD |
| 2 | Manager choices when staff are constrained | Member A | TBD |
| 3 | A dish going READY -> SERVED -> payment | Member B | TBD |
| 4 | VIP or Critic behaviour | Member C | TBD |
| 5 | A Stage 3 feature | Member D | TBD |
| 6 | One victory and one defeat | Member A | TBD |
| 7 | Stage tag comparisons and diagrams | Member E | TBD |

## Project structure

```
src/restaurantrush/   Java source (one package, 25 files)
test-runs/            input files and transcripts for reproducible games
TESTING.md            what each run demonstrates
report/               PDF report, standalone UML diagrams, LaTeX source
```
