# ProB for Rodin 3.2.3 – Release Notes

## New features

### ProB Console
A new view for evaluating formulas in the current state of the animation. Open it from the State view's right-click menu ("Open ProB Console") or via Window › Show View › ProB › ProB Console.
- **Evaluate predicates and expressions** in the current state. Predicates give TRUE/FALSE, expressions their value. Evaluation uses ProB's constraint solver and expands symbolic values.
- **Free identifiers are existentially quantified.** Identifiers that aren't part of the model are solved for, and the solution is shown, e.g. `x : 1..10 & x*x = 49` gives `TRUE  x = 7`. UNKNOWN means the solver found no solution but couldn't prove there is none.
- **Other solvers:** `:kodkod P`, `:cdclt P`, `:sat P`, `:satz3 P`, `:z3 P`, `:z3cns P`, `:z3axm P`, `:cvc4 P`, `:smt P`, `:prob P`. Availability depends on the ProB build.
- **Commands:**
  - `:type F`: the type of a formula and of its free identifiers
  - `:state`: the current state's values, status and enabled events
  - `:prefs [TEXT]`: ProB's preferences, with their defaults if changed
  - `:clear` clears the output, and `:x` closes the console
  - `:help [COMMAND]` or the ? button shows the help
- **Editing:**
  - Enter evaluates and Shift+Enter starts a new line. Up/Down browse the input history.
  - Tab or Ctrl+Space completes model identifiers, Event-B keywords and console commands.
- **Mathematical symbols:** the ∀ toggle translates ASCII to symbols while typing, as in the Rodin editors (except in `:` commands). The console uses Rodin's math font, so `<<->`, `<->>`, `<<->>` and `<+` display correctly.
- **Display:**
  - The ms toggle shows solving times.
  - The + / − buttons, or Ctrl+= / Ctrl+-, change the font size.
  - Type errors name the identifiers whose type can't be inferred.

### Start animation from the editors
- **Camille:** "Start Animation / Model Checking" in the right-click menu of the Camille text editor.
- **Rodin's editors:** a ProB toolbar button in the machine and context editors.

### Disprover: "Use ProB WD Prover"
A new proof tactic that calls ProB's well-definedness prover without constraint solving. Its time-out is set in the disprover preferences. Proofs found by it are labelled "ProB WD prover" in the proof tree.

### State view: Show Full Value
A new "Show Full Value ..." entry in the right-click menu shows the complete value of a variable or formula. Formula values are not cut off at 600 characters. The dialog can switch between the current and the previous state, and between plain and pretty-printed output.

### Operations view: non-deterministic choices
When you right-click an enabled event, each non-deterministic choice is now labelled with the variables it changes and their new values.

## Improvements
- **Model checking:** the progress dialog and its cancel button now stay responsive during model checking.
- **probcli crashes:** ProB restarts probcli automatically after a crash, instead of requiring a Rodin restart.
- **Error reporting:** errors inside ProB commands are no longer swallowed.
- **ProB Tcl/Tk and ProB2-UI:** starting them gives clearer messages, including the program's last output when it exits with an error.
- **Preferences:** ProB's read-only version preferences are no longer stored or sent to probcli, which removes six error log entries on every model load.

## Bug fixes
- **Deadlocks and concurrency:**
  - fixed a deadlock between History and Animator
  - fixed thread-safety problems in the listener registry
  - views no longer access disposed widgets after being closed
- **Model loading:**
  - a failed load now clears the old model, so views no longer show it as animated
  - invariants added by other plugins are no longer dropped
  - deleting or closing the animated project no longer causes problems
- **Disprover:**
  - fixed a possible crash
  - fixed a problem when translation fails
  - fixed inconsistent preferences
  - fixed the export of free identifiers
  - fixed the id of the extended-timeout tactic
- **Trace replay and caching:**
  - fixed a null-pointer exception in trace replay
  - fixed user notifications when loading traces
  - fixed a stale evaluation cache
- **BMotion Studio:**
  - The first visualisation after starting Rodin no longer leaves the State and History views empty. Rodin's saved Run perspective had to be reset each time before.
  - Fixed the splash screen getting stuck.
  - Fixed paste/undo, replace and grid colour problems.
  - Fixed the converter.
  - Loading and conversion failures now give clear error messages.
- **UI:**
  - fixed operating-system handle leaks
  - fixed text decoding (UTF-8) in the connection to probcli
  - fixed missing separators when dragging from the State view
  - fixed a shared layout object in the Operations view
  - several missing null checks added

## Security
- **BMotion Studio `.bmso` files:**
  - Loading is hardened against XML external entity (XXE) attacks.
  - XStream's allow-list is now enforced for child controls.
- **XStream:** updated to 1.4.21 (CVE-2024-47072).

## Build and dependencies
- probcli 1.16.2 (nightly)
- Tycho 5.0.4
- tests migrated to JUnit 5
- feature requires `org.eclipse.core.runtime` 3.20.0 or later
- build fixes: closing download streams, no stale parser jars
- new `Makefile` for local builds

## Notes
- **Free identifiers:** in the console, they need inferable types. Add a typing predicate such as `x : INT` if needed.
- **ProB version for the WD prover:** "Use ProB WD Prover" needs a probcli that supports the `no_constraint_solving` disprover option.
