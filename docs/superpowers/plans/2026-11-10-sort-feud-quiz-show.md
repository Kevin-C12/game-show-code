# Sort Feud 1v1 Quiz Show Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver reliable merge-sort questions and a professional two-player, first-buzz quiz-show experience.

**Architecture:** Preserve `App` as the JavaFX view/controller and `GameEngine` as the rules layer. Add a shared JSON writer so generators return the exact file the engine loads, plus small pure input helpers for keyboard behavior that can be tested without launching JavaFX.

**Tech Stack:** Java 21, JavaFX 21, Maven, Gson 2.11, JUnit Jupiter 5

**Spec:** `docs/superpowers/specs/2026-11-10-sort-feud-quiz-show-design.md`

## Global Constraints

- Keep JSON fields `sort`, `unOrderedList`, and `sortSteps`.
- JSON sort IDs are `insertionSort`, `selectionSort`, and `mergeSort`.
- Player 1 uses `A`; Player 2 uses `L`.
- Preserve generated JSON files already present in the user's checkout.
- Do not rewrite inactive legacy GUI classes.

## Review Focus

- A sorted merge input still produces one recorded state for every merge.
- Duplicate and negative values survive step generation and engine validation.
- A wrong answer reopens the same step to both players without changing scores.
- Held A/L keys register only once and do nothing while typing.
- Signed multi-digit values remain editable before focus advances.

---

### Task 1: Merge question generation and persistence

**Files:**
- Create: `src/main/java/com/example/QuestionWriter.java`
- Modify: `src/main/java/com/example/InsertionSort.java`
- Modify: `src/main/java/com/example/SelectionSort.java`
- Modify: `src/main/java/com/example/MergeSort.java`
- Modify: `src/main/java/com/example/QuestionReturn.java`
- Modify: `pom.xml`
- Test: `src/test/java/com/example/MergeSortTest.java`
- Test: `src/test/java/com/example/QuestionWriterTest.java`

**Interfaces:**
- Produces: `QuestionWriter.writeNext(Path, String, int[], Map<String,int[]>) -> Path`
- Produces: each generator `run() -> Path` and public `SORT_ID`

- [ ] Add JUnit and write failing tests for exact merge steps across sorted, reverse, duplicate, and negative arrays.
- [ ] Run targeted tests and confirm the missing persistence API/visibility failures.
- [ ] Implement the shared writer and generator IDs, preserving the JSON schema.
- [ ] Run targeted tests and the full suite.

### Task 2: 1v1 engine rules and merge loading

**Files:**
- Modify: `src/main/java/com/example/GameEngine.java`
- Modify: `src/main/java/com/example/GameState.java`
- Test: `src/test/java/com/example/GameEngineTest.java`

**Interfaces:**
- Consumes: generator `run() -> Path` and `SORT_ID`
- Produces: `loadQuestion(Path, String)` for production generation/loading and package-level tests
- Produces: player-oriented getters while retaining deprecated team getter aliases only where needed

- [ ] Write failing engine tests for merge JSON progression, first-buzz ownership, wrong-answer reopening, timeout reopening, and final scoring.
- [ ] Run tests and confirm failures describe the old strike/steal behavior.
- [ ] Implement the 1v1 attempt lifecycle and validated JSON loading.
- [ ] Run targeted tests and the full suite.

### Task 3: Keyboard and answer-input behavior

**Files:**
- Create: `src/main/java/com/example/BuzzerInput.java`
- Create: `src/main/java/com/example/AnswerInput.java`
- Modify: `src/main/java/com/example/App.java`
- Modify: `pom.xml`
- Modify: `src/main/java/module-info.java`
- Delete: `src/main/java/com/example/MacShiftKeys.java`
- Test: `src/test/java/com/example/BuzzerInputTest.java`
- Test: `src/test/java/com/example/AnswerInputTest.java`

**Interfaces:**
- Produces: `BuzzerInput.press(KeyCode, boolean) -> int` and `release(KeyCode)`
- Produces: signed-integer edit and completion predicates used by `TextFormatter`

- [ ] Write failing pure tests for A/L mapping, repeat suppression, typing suppression, and signed multi-digit validation.
- [ ] Run targeted tests and verify RED.
- [ ] Implement helpers and replace native Shift hooks with scene-level A/L filters.
- [ ] Implement delayed auto-advance, Enter navigation, and backspace navigation.
- [ ] Run targeted tests and the full suite.

### Task 4: Professional JavaFX presentation

**Files:**
- Modify: `src/main/java/com/example/App.java`
- Modify: `src/main/resources/styles.css`
- Modify: `README.md`

**Interfaces:**
- Consumes: player-oriented engine state and shared buzz path
- Produces: separate algorithm/step metadata and question prompt in the game view

- [ ] Restructure the header, landing panel, game card, player buzzers, and developer toolbar without changing engine behavior.
- [ ] Replace the stylesheet with the broadcast design system and responsive minimum sizing.
- [ ] Update controls and 1v1 rules in README.
- [ ] Run `mvn test` and `mvn package`.
- [ ] Launch the JavaFX application and verify the main interaction flow where the environment supports GUI display.
