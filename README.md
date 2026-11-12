# game-show-code
Code for running the game show

# Game Rules

Sort Feud is a two-team quiz show built around insertion sort, selection sort,
and merge sort. Players calculate the requested sorting step, then buzz when
they are ready to answer.

1. The game is a best-of-five series. The first team to win three rounds wins.
2. Each round is best of nine: the first team to claim five dots wins the round.
3. Three wrong answers or timeouts by one team forfeit that round to its opponent.
4. The first accepted buzz locks in that team and starts a 20-second answer clock.
5. A correct answer claims one dot. A wrong answer claims no dot, reveals the
   correct step, and waits for the host to continue.
6. Round 1 uses insertion sort, round 2 selection sort, and round 3 merge sort.
   Rounds 4 and 5 mix all three algorithms and ask for a random nth step.
7. One randomly selected member represents each team. Nobody repeats until
   every teammate on that roster has played.

## Controls

- Team 1 buzzes with **A**.
- Team 2 buzzes with **L**.
- Either player can click their on-screen buzzer.
- Buzzer keys are disabled while an answer field has focus, so signed and
  multi-digit answers can be typed normally.
- Press Enter to move to the next answer box; Enter on the final box submits.

# Running the code
mvn clean javafx:run
