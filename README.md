# game-show-code
Code for running the game show

# Game Rules
Objective
- Sort Feud is a two-team competition based on the popular game show Family Feud. Players complete insertion, selection, and merge sort challenges. Each new round uses a different sort. The goal is to earn the most points.
Teams
- Players are divided into two competing teams.
- Each team selects players to participate in the challenges.
- A host asks the questions, while a co-host provides commentary, guidance, and explanations throughout the game.
Round One: Sort Challenge
1. The host displays a short, unordered list of numbers on the game board.
2. Players must determine the next step of the specified sort algorithm.
3. The first player to buzz in with an answer gets the opportunity to respond.
4. If the answer is correct, the game displays the completed step and asks for the next one.
5. The team continues identifying the correct steps until the list is completely sorted.
6. If the team answers incorrectly, they receives a strike.
7. A team is allowed a maximum of three strikes. After its third strike, the opposing team gets an opportunity to steal the round by correctly completing the sort.
8. The team that completes the sorting challenge earns 10 points and wins the round.
## Planned Final Round: Fast Money
1. The winning team advances to the Fast Money round.
2. Players answer questions about the Specified sort algorithm.
3. Questions may cover topics such as:
   - The steps involved in a specified sort.
   - The number of comparisons required in different scenarios.
   - Best-case and worst-case time complexity.
   - Why the worst-case time complexity is \(O(n^2)\).
   - How a specified sort behaves with an already sorted or reverse-sorted list.
4. Players must answer the questions correctly to earn points toward the final prize.
5. The team must reach the required score to win the prize.

## Buzzer controls

Team 1 presses left Shift; Team 2 presses right Shift. Either team can also
click its on-screen buzzer. The game window must be focused for keyboard
buzzers to work. On macOS, the game reads left and right Shift state locally;
on Windows and Linux, it uses JNativeHook. Some Linux Wayland sessions may
block native keyboard listeners, so the on-screen buttons remain available.

# Runnning the code
mvn clean javafx:run
