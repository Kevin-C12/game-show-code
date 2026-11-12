# Question Context and Manual Feedback Design

## Intent

Make the game screen faster to read and give players time to study a missed answer.

## Approved behavior

- The answer clock starts at 15 seconds after a valid buzz.
- Rounds 1–3 label the displayed array as the previous chronological state: `START:` for step 1 and the ordinal prior step for later questions.
- Rounds 4–5 always label the displayed original array `START:` because their target steps are random.
- A wrong answer reveals the correct array and pauses indefinitely.
- The host must click `NEXT QUESTION`; after a third strike the button says `NEXT ROUND` and is still required.
- Correct-answer progression remains automatic.
- Screen copy is shortened without removing team, score, algorithm, step, timer, or control information.

## Approach

Keep gameplay state in `GameEngine`. Add a small pure formatter for the displayed-array label so round/step wording is testable without JavaFX. Keep the pause in `App`, where feedback timing and controls already live; wrong results skip the automatic feedback timer and expose a continue button.

## Alternatives considered

- Store a second reference array in the engine: rejected because the engine already exposes the correct displayed state for both chronological and mixed questions.
- Pause every answer: rejected because only incorrect answers were requested.

## Constraints

- Preserve roster rotation, dots, strikes, best-of-five series rules, and A/L buzzer behavior.
- Do not add dependencies or redesign the established minimal dark style.
