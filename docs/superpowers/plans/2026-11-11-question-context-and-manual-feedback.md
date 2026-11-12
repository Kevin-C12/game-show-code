# Question Context and Manual Feedback Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a 15-second post-buzz clock, concise array context, and click-to-continue wrong-answer reveals.

**Architecture:** `GameEngine` remains the source of round and step state. A pure `ArrayStageLabel` formatter supplies concise context to `App`; `App` owns the continue-button pause because it already controls feedback transitions.

**Tech Stack:** Java 21, JavaFX 21, Maven, JUnit 5

**Spec:** `docs/superpowers/specs/2026-11-11-question-context-and-manual-feedback-design.md`

## Global Constraints

- Timer is 15 seconds and starts only after buzzing.
- Only wrong answers require a click to continue.
- Third-strike forfeits also require `NEXT ROUND`.
- Rounds 4–5 show only the original starting list.

## Review Focus

- Step 1 in rounds 1–3 labels the array `START` rather than step 0.
- Random target questions never imply a chronological previous step.
- A wrong final step waits even though the engine has already closed that question.
- A third strike waits before the round transition.
- Buzzer input remains blocked while feedback is waiting.

---

### Task 1: Timer and array-stage context

**Files:**
- Create: `src/main/java/com/example/ArrayStageLabel.java`
- Create: `src/test/java/com/example/ArrayStageLabelTest.java`
- Modify: `src/main/java/com/example/GameEngine.java`
- Modify: `src/test/java/com/example/GameEngineTest.java`

**Interfaces:**
- Produces: `ArrayStageLabel.forQuestion(int seriesRound, int targetStep): String`
- Produces: `GameEngine.getArrayStageLabel(): String`

- [ ] Write failing tests for the 15-second countdown and labels `START`, `1ST STEP`, `2ND STEP`, plus mixed-round `START`.
- [ ] Run the focused tests and verify the expected failures.
- [ ] Implement the 15-second constant and label formatter/getter.
- [ ] Run the focused tests and verify they pass.

### Task 2: Manual wrong-answer continuation and concise copy

**Files:**
- Modify: `src/main/java/com/example/App.java`
- Modify: `src/main/resources/styles.css`

**Interfaces:**
- Consumes: `GameEngine.getArrayStageLabel()`
- Produces: wrong-answer feedback that exposes `NEXT QUESTION` or `NEXT ROUND` and does not start the automatic feedback timer.

- [ ] Update the JavaFX view with a persistent current-array caption and continue button.
- [ ] Route wrong results to the manual button; keep correct results automatic.
- [ ] Shorten nonessential captions and status messages.
- [ ] Compile, run all non-headless tests, package, and launch the app for startup/CSS verification.
