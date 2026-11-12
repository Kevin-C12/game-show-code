# Sort Feud Team Match Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace point-based anonymous players with a best-of-nine team match, fair roster rotation, three-strike forfeits, answer reveals, and a distinctive editorial JavaFX interface.

**Architecture:** `GameState` owns dot wins, strikes, and match termination. New immutable `TeamRoster` and stateful `MatchupScheduler` classes own validated team data and independent shuffle bags. `GameEngine` advances every resolved step and exposes result/matchup state; `App` provides setup, feedback, and scoreboard presentation without duplicating rules.

**Tech Stack:** Java 21, JavaFX 21, Maven, JUnit 5, Gson, JavaFX CSS

**Spec:** `docs/superpowers/specs/2026-11-11-sort-feud-team-match-design.md`

## Global Constraints

- Preserve insertion, selection, and merge sort JSON behavior.
- Preserve A/L first-buzz behavior and signed multi-digit answer entry.
- Nine shared dots; first to five wins; wrong answers leave dots unclaimed.
- Third team strike immediately forfeits the match.
- No new runtime dependencies.
- UI must remain reachable at the existing minimum window size.
- Prompts name the exact ordinal sorting step and accepted buzzes log reveal-to-buzz latency in milliseconds.
- Merge sort never saves two consecutive identical visible arrays.

## Review Focus

- A third strike while leading 4–0 must still forfeit the match.
- A wrong final sorting step must reveal and apply the expected array before the next generated question.
- Unequal roster sizes must not cause a member to repeat before their own team bag is exhausted.
- A one-member roster must continue producing valid matchups.
- Match reset must clear dots, strikes, winner, current buzzer owner, and roster rotation.

---

### Task 1: Match rules and scoring state

**Files:**
- Modify: `src/main/java/com/example/GameState.java`
- Create: `src/test/java/com/example/GameStateTest.java`

**Interfaces:**
- Produces: `recordCorrect()`, `recordWrong()`, `resetMatch()`, team dot/strike getters, `getWinner()`, `isMatchOver()`.

- [ ] Write failing tests for first-to-five, wrong-answer strike without a dot, third-strike forfeit while leading, and reset.
- [ ] Run `mvn -Dtest=GameStateTest test`; expect failures because match APIs do not exist.
- [ ] Implement the minimal match state and remove numerical points.
- [ ] Run `mvn -Dtest=GameStateTest test`; expect all tests to pass.
- [ ] Commit match-state changes.

### Task 2: Team validation and fair matchup rotation

**Files:**
- Create: `src/main/java/com/example/TeamRoster.java`
- Create: `src/main/java/com/example/Matchup.java`
- Create: `src/main/java/com/example/MatchupScheduler.java`
- Create: `src/test/java/com/example/TeamRosterTest.java`
- Create: `src/test/java/com/example/MatchupSchedulerTest.java`

**Interfaces:**
- Consumes: validated team names and member lists from setup.
- Produces: `MatchupScheduler.next()` returning one member per team with independent no-repeat rotation.

- [ ] Write failing tests for blank/duplicate names, full-cycle uniqueness, unequal rosters, and single-member teams.
- [ ] Run the two new test classes; expect missing-class compilation failures.
- [ ] Implement immutable roster validation and shuffled queue rotation with injectable `Random`.
- [ ] Run the two new test classes; expect all tests to pass.
- [ ] Commit roster and scheduler changes.

### Task 3: Engine round progression

**Files:**
- Modify: `src/main/java/com/example/GameEngine.java`
- Modify: `src/test/java/com/example/GameEngineTest.java`

**Interfaces:**
- Consumes: Task 1 match state and Task 2 matchup scheduler.
- Produces: match configuration, active matchup, expected-answer reveal, dot/strike state, and terminal result values for five-dot wins and strike forfeits.

- [ ] Write failing tests proving correct answers claim one dot, wrong answers reveal/advance without a dot, third-strike forfeiture, matchup rotation, and match reset.
- [ ] Run `mvn -Dtest=GameEngineTest test`; expect assertion or compilation failures for missing behavior.
- [ ] Implement the new result flow while preserving saved-step loading and algorithm rotation.
- [ ] Run non-UI tests; expect all to pass.
- [ ] Commit engine integration.

### Task 4: Merge-step cleanup and prompt timing utilities

**Files:**
- Modify: `src/main/java/com/example/MergeSort.java`
- Create: `src/main/java/com/example/QuestionPrompt.java`
- Create: `src/main/java/com/example/BuzzTiming.java`
- Modify: `src/test/java/com/example/MergeSortTest.java`
- Create: `src/test/java/com/example/QuestionPromptTest.java`
- Create: `src/test/java/com/example/BuzzTimingTest.java`

**Interfaces:**
- Consumes: engine step numbers and reveal timestamps.
- Produces: de-duplicated merge snapshots, ordinal question wording, and reveal-to-buzz elapsed milliseconds.

- [ ] Write failing tests for unchanged merge snapshots, ordinal edge cases, and elapsed millisecond calculation.
- [ ] Run focused tests; expect failures before implementation.
- [ ] Implement merge snapshot de-duplication and prompt/timing utilities.
- [ ] Run focused tests; expect all tests to pass.
- [ ] Commit utilities and merge cleanup.

### Task 5: Setup flow and editorial scoreboard UI

**Files:**
- Modify: `src/main/java/com/example/App.java`
- Modify: `src/main/resources/styles.css`
- Modify: `src/test/java/com/example/GameViewportTest.java`

**Interfaces:**
- Consumes: engine team/match APIs from Task 3 and prompt/timing utilities from Task 4.
- Produces: dynamic 1–8 member setup, current matchup labels, nine shared dots, team strikes, answer reveal, rematch/setup actions, and parchment/ink/brick/teal styling.

- [ ] Add or adjust JavaFX structure tests for scroll reachability and scoreboard/setup factories where behavior can be tested without duplicating framework internals.
- [ ] Run the relevant UI tests in a graphical environment; expect failures before implementation.
- [ ] Implement setup, scoreboard, matchup, ordinal prompt, timed buzz logging, and feedback wiring; replace the current dark blue visual system with the editorial palette.
- [ ] Run the full test suite and `mvn package`; expect success in a graphical environment.
- [ ] Launch the JavaFX app and manually verify setup, A/L buzzing, answer reveal, dot fill, strike forfeit, and responsive scrolling.
- [ ] Commit UI integration.
