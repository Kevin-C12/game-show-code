# Sort Feud Best-of-Five Series Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a first-to-three, best-of-five-round series with round-specific sorting policies, balanced mixed questions, direct nth-step calculation, round intros, and JavaFX transitions.

**Architecture:** A new `SeriesController` owns series score, round progression, and balanced mixed-algorithm scheduling. `GameEngine` continues to own round dots/strikes, question generation, buzzer state, the 10-second clock, and answer validation. `App` presents round/series state and animations without duplicating rules.

**Tech Stack:** Java 21, JavaFX 21, Maven, JUnit 5, Gson

**Spec:** `docs/superpowers/specs/2026-11-11-sort-feud-best-of-five-series-design.md`

## Global Constraints

- The series ends immediately when a team wins three rounds.
- Every round remains first to five dots with third-strike forfeiture.
- Round 1 is insertion, round 2 selection, round 3 merge.
- Rounds 4 and 5 rotate all three algorithms before repeating and ask for a random valid nth step from the original array.
- Teammate rotation continues across round boundaries.
- Buzzing starts a 10-second answer timer; transitions never accept buzzes.
- Preserve existing JSON structure and sorting implementations.

## Review Focus

- Third strike records exactly one round win and does not end the series before three round wins.
- A 3–0 series score ends the game without starting round 4.
- Mixed algorithm bags contain insertion, selection, and merge once before repeating.
- Mixed questions display the original array and validate the randomly selected saved step.
- Starting a new round resets dots, strikes, owner, and timer without resetting teammate rotation.

---

### Task 1: Series controller

**Files:**
- Create: `src/main/java/com/example/QuestionRequest.java`
- Create: `src/main/java/com/example/SeriesController.java`
- Create: `src/test/java/com/example/SeriesControllerTest.java`

**Interfaces:**
- Produces: current round, round wins, series winner, round completion, `nextQuestion()`, and `advanceRound()`.

- [ ] Write failing tests for fixed round policies, balanced mixed bags, first-to-three termination, and round advancement.
- [ ] Run focused tests and verify expected compilation failures.
- [ ] Implement the minimal controller with injectable `Random`.
- [ ] Run focused tests and verify success.

### Task 2: Engine integration and nth-step mode

**Files:**
- Modify: `src/main/java/com/example/GameEngine.java`
- Modify: `src/test/java/com/example/GameEngineTest.java`

**Interfaces:**
- Consumes: `SeriesController` question requests.
- Produces: series/round getters, mixed target-step behavior, `advanceRound()`, and round/series terminal results.

- [ ] Write failing tests for round-scoped resets, series wins, fixed algorithms, original-array mixed questions, and target-step validation.
- [ ] Run focused tests and verify failures.
- [ ] Integrate the controller while preserving buzzer, timer, roster, and JSON behavior.
- [ ] Run all non-UI tests and verify success.

### Task 3: Round presentation and animations

**Files:**
- Modify: `src/main/java/com/example/App.java`
- Modify: `src/main/resources/styles.css`

**Interfaces:**
- Consumes: engine series score, round number, policy label, and terminal state.
- Produces: animated round introductions, series scoreboard, dot/strike feedback motion, and final series screen.

- [ ] Add the round-introduction and final-series views.
- [ ] Wire transitions so buzzes remain closed until question reveal.
- [ ] Update header copy for series and round scores.
- [ ] Launch JavaFX and verify clean CSS/runtime startup.

### Task 4: Verification

**Files:**
- Modify only if verification exposes defects.

- [ ] Run `mvn test` with the JavaFX toolkit.
- [ ] Run `mvn package`.
- [ ] Independently review the completed diff against the spec and review focus.
