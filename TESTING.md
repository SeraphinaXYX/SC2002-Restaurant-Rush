# Reproducible runs

Every `.txt` file in `test-runs/` is the exact list of inputs for one game: the
first line is the stage, the second the seed, then one line per choice. Replay with

```
javac -d bin src/restaurantrush/*.java
java -cp bin restaurantrush.RestaurantRush --echo < test-runs/stage1-victory.txt
```

`--echo` prints each input after its prompt so the transcript reads like a live game.
The matching `.out` file is the transcript we recorded. If the input runs out early,
every remaining prompt takes its last option (Wait / Back / no), so the game still
finishes all 18 turns.

The "victory" runs use one simple priority each turn: serve a READY dish if there is
one, otherwise take an order, otherwise seat someone; the Chef works on the oldest
unfinished order; the Host seats the first person in the queue. The "defeat" runs use
the same Waiter but the Chef never cooks (and the Host waits), so no food reaches anyone.

| File | What it shows | Result |
|------|---------------|--------|
| stage1-victory | Turns 1-6 are exactly the worked example in the brief: Regular #1 orders Pasta, satisfaction 92 / 84 / 76 at the end of turns 1-3, served on turn 4, READY_TO_PAY on turn 5, pays $18.00 on turn 6 | Victory: 5 paid, $63.00, avg 74.4 |
| stage1-defeat | Chef never cooks; two customers run out of patience and leave | Defeat: 0 paid, $0.00, N/A |
| stage1-invalid-input | `abc` and `9` at a menu, serving with an empty kitchen, taking an order with nobody seated, serving a Pasta that is only 1/2 done, seating at an occupied table. Each prints the reason and asks again without using the Waiter's task | stops after turn 4, rest defaults to Wait |
| stage2-victory | VIP #3 orders Salad and pays $8.10 (10% off); Critic #5 orders Pasta, is served 2 turns after ordering and reacts +5 (52 -> 57) | Victory: 4 paid, $53.10, avg 76.3 |
| stage2-defeat | as stage1-defeat with the VIP and Critic | Defeat: 0 paid |
| stage3-victory | Host seats every arrival; Happy Hour switched on at turn 7; VIP #3 orders a Salad + Burger Combo: $21.60 -> $19.44 (VIP) -> $15.55 (Happy Hour); VIP recovers 95 -> 100 on the second Happy Hour turn; combo prices $21.60 and $24.30 | Victory: 5 paid, $88.45, avg 83.2 |
| stage3-defeat | Host waits, Chef never cooks | Defeat: 0 paid |
| stage3-vip-happy-hour-seed1 | seed 1, Happy Hour at turn 7: VIP #3 orders a Burger, charged $10.80 ($15 x 0.9 x 0.8), the example in the brief | Victory: 5 paid, $73.80, avg 91.4 |
| stage3-happy-hour-after-payment | Three customers have already paid (recorded 92, 84, 90, average 88.7) when Happy Hour is switched on at turn 13. During both Happy Hour turns the average stays 88.7: only Regular #4 (still waiting for food, 76 -> 91) and Critic #5 (88 -> 100) recover, paid customers are never touched. Final average 87.6 = (92 + 84 + 90 + 91 + 81) / 5, i.e. exactly the values recorded at payment | Victory: 5 paid, $88.74, avg 87.6 |
| extra-host-and-waiter-seat | Stage 3, seed 42. The Host waits for three turns so two people queue up; on turn 4 Host Hana seats Regular #1 at Table 1 and Waiter Wendy seats Regular #2 at Table 2 in the same turn. Later turns show the Host refusing with "nobody to seat" and "no free table". The Chef never cooks | Defeat: 0 paid, 2 departures |
| extra-critic-slow-service | Stage 2, seed 42. The Critic's Pasta is ordered on turn 15 and only served on turn 18 (3 turns), so the Critic reacts 40 -> 30, the -10 penalty for slow service | Victory: 4 paid, $53.10, avg 76.3 |

## Checking that later stages did not break earlier rules

After Stage 2 and again after Stage 3 we replayed all earlier `.txt` files on the new
build and compared transcripts (ignoring the title line). They were identical, e.g.

```
for f in test-runs/stage1-*.txt test-runs/stage2-*.txt; do
  java -cp bin restaurantrush.RestaurantRush --echo < $f | diff - ${f%.txt}.out
done
```


The only lines that differ are the stage-selection menu at the very top (Stage 3 adds a
third option); every turn of the game is identical. The `.out` files committed at the
`stage-3` tag were regenerated with the Stage 3 build.
