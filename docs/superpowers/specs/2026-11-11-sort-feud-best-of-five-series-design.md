# Sort Feud Best-of-Five Series Design

## Confirmed game structure

- A game is a best-of-five-round series and ends immediately when one team wins three rounds.
- Every round is best of nine: the first team to claim five dots wins that round.
- Correct answers claim one dot. Wrong answers claim no dot and add one strike to the answering team.
- A team's third strike forfeits only the current round. Dots and strikes reset at the start of the next round.
- Team rosters remain fixed for the series. Round boundaries do not reset member rotation: no player may repeat until every teammate has participated.

## Round question policies

- Round 1 uses insertion sort questions in chronological step order.
- Round 2 uses selection sort questions in chronological step order.
- Round 3 uses merge sort questions in chronological step order.
- Rounds 4 and 5 use mixed questions. Insertion, selection, and merge must each appear once before any algorithm repeats.
- A mixed question randomly selects a valid saved step number for its algorithm and shows the original unsorted array. Players calculate directly to that requested step.
- Mixed questions do not show the preceding step and do not require chronological progression.
- Every displayed step number must exist for the generated question. Questions with no meaningful step are regenerated.

## Buzzing and timing

- Players have unlimited thinking time before buzzing.
- The first accepted A/L or mouse buzz locks the question to that team and starts a 10-second answer timer.
- A timeout counts as a wrong answer and adds a strike.
- Existing reaction-time terminal logging remains unchanged.

## Series controller

- A dedicated series controller owns round number, round wins, per-round dots and strikes, round winner, series winner, and the next round policy.
- `GameEngine` continues to own question loading, sorting-step validation, buzzing, and the answer clock.
- The JavaFX `App` renders controller state and triggers transitions without duplicating match rules.

## Round and series presentation

- Before every round, show a full-screen round-introduction view with the round number, current series score, round format, and upcoming algorithm policy.
- Use short native JavaFX fade and slide transitions for round introductions, score-dot claims, strikes, round wins, and the final series winner.
- Animations must not consume thinking time or answer time and must not accept buzzes while a transition is active.
- The final screen identifies the series winner and final round score, with rematch and edit-team actions.

## Preserved behavior

- Team setup, teammate-count control, fair member rotation, A/L bindings, mouse buzzers, signed multi-digit input, JSON question format, merge-step de-duplication, and the simplified visual system remain in place.
