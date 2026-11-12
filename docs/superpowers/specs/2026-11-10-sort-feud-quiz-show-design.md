# Sort Feud 1v1 Quiz Show Design

## Intent

Turn Sort Feud into a polished two-player quiz show where every sorting step is an open buzzer question. Preserve the three sorting algorithms, JSON question files, scoring, developer demos, and step-by-step play.

## Rules

- Player 1 buzzes with `A`; Player 2 buzzes with `L`. Clicking either on-screen buzzer uses the same engine path.
- The first accepted buzz owns the current attempt. Held/repeated keys do not create more buzzes, and buzzer keys are ignored while an answer field has focus.
- A correct intermediate step advances the displayed array and reopens the buzzer for both players.
- A wrong answer or timeout reopens the buzzer for both players on the same step.
- The player who completes the final step earns 10 points.
- The selected buzzer remains visibly locked while that player is answering.

## Sorting Questions

- Question JSON retains `sort`, `unOrderedList`, and ordered `sortSteps.stepN` fields.
- Identifiers are exactly `insertionSort`, `selectionSort`, and `mergeSort` across generators, persistence, the engine, and the legacy question index.
- Merge sort records the full array after every completed merge, including merges that do not visibly change an already ordered segment.
- Sorting and answer input support sorted, random, reverse-sorted, duplicate, and negative-value arrays.

## Presentation

- Use player score cards, a central round/timer area, a dedicated algorithm-and-step line, and a separate `What is the next step?` prompt.
- Use a restrained navy/slate broadcast palette, red and blue player accents, consistent spacing, and clear locked/active states.
- Retain developer demos in a compact toolbar.

## Input

- Answer boxes accept signed, multi-digit integers.
- A completed integer advances after a short pause; Enter advances immediately, and Enter on the last field submits.
- Backspace in an empty field returns to the previous field.

## Approaches

1. **Targeted refactor (selected):** keep the existing JavaFX/GameEngine structure, add small question-persistence and input helpers, and remove native global-key dependencies. This preserves the project while making the important boundaries testable.
2. **Full MVC rewrite:** split every view and controller. Cleaner in theory, but too broad for the requested fixes and likely to disrupt the student project.
3. **UI-only patch:** change labels and CSS without fixing engine and persistence boundaries. Rejected because it would leave the merge and 1v1 behavior fragile.

## Non-goals

- No network play, player-name setup, tournament bracket, audio system, or replacement of JavaFX.
- Legacy classes remain unless a small identifier correction is required; inactive code is not rewritten.
