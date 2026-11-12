# Sort Feud Team Match Design

## Match rules

- Sort Feud is a two-team quiz show. Each matchup features one randomly selected member from each team.
- The first valid buzz (Team 1: `A`, Team 2: `L`) owns the answer attempt.
- A correct next sorting step fills the next of nine shared score dots with that team's color.
- The first team to fill five dots wins the match.
- A wrong answer or expired timer adds one strike to the answering team. The score dot remains unclaimed.
- After a wrong answer, show the correct next array before advancing to the next matchup.
- A team's third strike immediately forfeits the match to the other team, even if the striking team leads 4–0.
- Numerical points are removed; dots are the score and strikes are the penalty.
- Scores and strikes reset for each new best-of-nine match.

## Sorting progression

- Each matchup asks for the exact ordinal step, such as “What is the 1st step?” or “What is the 3rd step?”, and shows algorithm metadata separately.
- Whether answered correctly or incorrectly, the revealed next step becomes the current array and the game advances. A missed step is never retried.
- Saved multi-step questions continue to their next step. When their final step resolves, the next algorithm question is generated.
- Merge sort omits intermediate snapshots that are identical to the immediately preceding visible array.
- Existing insertion, selection, and merge sort generation, persistence, validation, and signed multi-digit input remain unchanged.

## Team setup and fair rotation

- Before a match, each team enters a team name, chooses a roster size from 1–8, and enters every member name.
- Empty or duplicate-within-team member names are rejected with inline feedback.
- Each team has its own shuffled rotation bag. A member cannot be selected again until every other member on that team has appeared.
- When team sizes differ, each team cycles independently; this preserves the no-repeat guarantee that is possible for that roster.
- A fresh matchup is selected after every resolved answer.

## Visual direction

- Use a warm editorial game-show palette: parchment and ink foundations, brick red for Team 1, and deep teal for Team 2.
- Avoid blue-purple gradients, neon glows, glass cards, excessive pills, and uniform floating-card layouts.
- Use strong typographic hierarchy, tabular-feeling scores, clear focus states, and physical scoreboard markers.
- Keep the application usable at its existing minimum window size and preserve vertical scrolling when needed.

## Feedback timing

- Correct feedback briefly marks the claimed dot, then starts the next matchup.
- Wrong feedback displays the expected array for about two seconds, records the strike, then starts the next matchup.
- Match-ending feedback remains visible until the host starts a rematch or returns to setup.
- Every accepted keyboard or mouse buzz logs its wall-clock timestamp with milliseconds and its elapsed milliseconds since the question was revealed.
