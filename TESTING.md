# Reproducible runs

Every file in `test-runs/` is a list of the exact inputs typed in one game
(first line is the stage, second is the seed). Replay one with:

```
javac -d bin src/restaurantrush/*.java
java -cp bin restaurantrush.RestaurantRush --echo < test-runs/stage1-victory.txt
```

`--echo` prints each input next to its prompt so the transcript is readable.
The matching `.out` file is the transcript we got.

| File | What it shows | Result |
|------|---------------|--------|
| stage1-victory.txt | Serve READY dishes first, then take orders, then seat. Turns 1-6 reproduce the worked example in the brief (Pasta: 92, 84, 76, served turn 4, READY_TO_PAY turn 5, pays $18.00 on turn 6) | Victory: 5 paid, $63.00, avg 74.4 |
| stage1-defeat.txt | Same Waiter play but the Chef never cooks, so nobody is served and two customers walk out | Defeat: 0 paid, $0.00, N/A |
| stage2-victory.txt | Stage 2 with the same priorities. VIP #3 orders Salad and pays $8.10 (10% off); Critic #5 orders Pasta, is served 2 turns after ordering and reacts +5 (52 -> 57) | Victory: 4 paid, $53.10, avg 76.3 |
| stage2-defeat.txt | Stage 2, Chef never cooks | Defeat: 0 paid, $0.00, N/A |
| stage1-invalid-input.txt | Bad input (`abc`, `9`), serving with an empty kitchen, taking an order with nobody seated, serving a dish that is only 1/2 done, seating at an occupied table. Each one prints the reason and asks again without using up the Waiter's task | (stops after turn 4; remaining turns default to Wait) |

The Stage 1 files were replayed on the Stage 2 build (choosing stage 1) and give the
same transcript, which is how we checked that Stage 2 did not change the base rules.
