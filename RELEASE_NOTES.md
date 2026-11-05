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
- **Errors** are shown in the console, in red, instead of in a dialog, including the error messages ProB reports while evaluating.

### ProB Graph
A new view that shows the graphs ProB can compute. Open it from the State view's right-click menu ("Open ProB Graph") or via Window › Show View › ProB › ProB Graph.
- **Menus:** the graphs are offered in toolbar drop-down menus, one per group: State Space, State, Formula, Trace, Data Flow and Machine. Unavailable graphs appear greyed out, with the reason; this is updated when the current state changes (e.g. the invariant formula tree needs an initialised state).
- **Examples:**
  - the state space, also with a faster layout
  - the signature merge and DFA merge
  - the current state as a graph
  - the path to the current state
  - formula trees of the invariant and axioms
  - the deadlock formula tree
  - the event enable and dependence graphs
  - the variable read/write graph
- **Going to a state:** in graphs of states (e.g. State Space), double-click a state to make it the current state, reached by the shortest trace from the root.
- **From the State view:** the right-click menu of a variable or formula has two new entries. "Show Value as Graph" draws its value, e.g. a relation, as a graph. "Project State Space onto Value" merges the states that have the same value.
- **Graphs for a formula:** graphs whose names end in "…" use the expression or predicate in the field above the graph, e.g. State Space Expression Projection… (states merged by the value of an expression), Expression as Graph… and Formula Tree…. Enter recomputes the graph. The field translates ASCII to symbols and completes identifiers with Tab or Ctrl+Space.
- **Navigation:** the mouse wheel zooms and dragging moves the graph. The + / − / Fit buttons zoom in, zoom out, or fit the whole graph into the view.
- **Refresh** recomputes the graph, e.g. after animating or model checking. Computations run in the background. The red Stop button interrupts ProB or, once ProB has written the graph, GraphViz.
- **Errors** while computing a graph are shown in the view instead of in a dialog: ProB's error messages are listed in red, where they can be scrolled, selected and copied.
- **Requirements:**
  - GraphViz must be installed.
  - Its `dot` program is looked up first via ProB's DOT preference, then on the PATH and in the usual install locations (Homebrew, `/usr/local/bin`, `/usr/bin`, `C:/Program Files/Graphviz/bin`).
  - The view needs Eclipse's embedded browser.

### ProB Table
A new view that shows the tables ProB can compute, e.g. machine statistics, operation and variable coverage, min/max values, read/write matrices and well-definedness proof obligations. Open it from the State view's right-click menu ("Open ProB Table") or via Window › Show View › ProB › ProB Table.
- **Menus:** the tables are offered in toolbar drop-down menus, one per group: Machine, Coverage, Formulas, Well-Definedness, Trace, Debug. Unavailable tables appear greyed out, with the reason; this is updated when the current state changes.
- **Tables for an expression:** tables whose names end in "…", e.g. Expression Table…, Expression Coverage… and Find Value…, use the expression in the field above the table. Enter recomputes the table. The field translates ASCII to symbols and completes identifiers with Tab or Ctrl+Space.
- **From the State view:** "Show Value as Table" in the right-click menu of a variable or formula shows its value as an Expression Table, e.g. a relation with one row per pair.
- **Working with tables:** click a column header to sort (numbers sort numerically). Ctrl/Cmd+C copies the selected rows, or all, tab-separated with headers, ready for a spreadsheet.
- **Refresh** recomputes the table in the current state, e.g. after animating or model checking. Computations run in the background, and the red Stop button interrupts them.
- **Errors** while computing a table are shown in the view instead of in a dialog: a red summary in the status line, and ProB's error messages as the rows of the table, where they can be copied.
- **Not yet offered:** the VisB and MC/DC tables.

### Start animation from the editors
- **Camille:** a ProB toolbar button, and "Start Animation / Model Checking" in the right-click menu of the Camille text editor.
- **Rodin Editor (Systerel):** a ProB toolbar button and the same right-click menu entry.
- **Rodin's form editors:** a ProB toolbar button in the machine and context editors.

### Disprover: "Use ProB WD Prover"
A new proof tactic that calls ProB's well-definedness prover without constraint solving. Its time-out is set in the disprover preferences. Proofs found by it are labelled "ProB WD prover" in the proof tree.

### State view: Show Full Value
A new "Show Full Value ..." entry in the right-click menu shows the complete value of a variable or formula. Formula values are not cut off at 600 characters. The dialog can switch between the current and the previous state, and between plain and pretty-printed output.

### State view: open a machine or context
Double-clicking a machine or context name in the State view opens its file in the default editor, e.g. Rodin's form editor, the Rodin Editor or Camille.

### History view: save and replay traces
- **Save Trace:** a new button in the History view's toolbar saves the trace from the start to the current state as a ProB2 JSON trace file (`.prob2trace`), e.g. for replay in ProB2-UI. The file is put in the folder `traces` of the model's project, which is created if necessary, and is named after the model with the first unused number, e.g. `M1_1.prob2trace`, `M1_2.prob2trace`. Its metadata records the machine or context file (`.bum`/`.buc`) as the model file. The button is disabled while there is no step to save, i.e. at the start of the history. After saving, the Event-B Explorer is shown with the new trace selected below the model's "Traces" node.
- **Replay Trace:** a new button next to it replays a `.prob2trace` file, e.g. one saved with Save Trace or by ProB2-UI. The file dialog opens in the project's `traces` folder. The replayed trace becomes the history, so you can step through it. A perfect replay is confirmed in the status bar; otherwise a dialog says how many steps were replayed and shows ProB's messages, e.g. which step could not be replayed.
- **Event-B Explorer:** each machine and context with saved traces gets a "Traces" node listing them (the trace files in the project's `traces` folder whose model name is that machine or context, numbered traces in order). Right-click a trace and choose "Replay Trace with ProB" to replay it; the animation of its machine or context is started first if it is not already animated. The right-click menu can also rename a trace ("Rename Trace...", the `.prob2trace` extension is kept) or delete it ("Delete Trace", after a confirmation). The node is updated when traces are saved or removed. Saving a trace no longer marks the animated model as modified.
- **Requirements:** a probcli with the new `prob2_save_trace_file/4` and `prob2_replay_json_trace_file_with_messages/5` (in `prob2_interface.pl`), and with two fixes for models sent by Rodin (which ProB knows as `package(event_b_project)`, not as a file): writing the JSON metadata no longer fails on the model file, and formatting error messages during a replay no longer raises a type error.

### Operations view: non-deterministic choices
When you right-click an enabled event, each non-deterministic choice is now labelled with the variables it changes and their new values.

### Operations view: "Unexplored Successors First"
A new option in the Double Click Behaviour menu. A double-click on an event picks one of its choices at random, preferring choices that lead to a state ProB has not explored yet. If all successor states are explored, it picks any choice at random. This needs a probcli whose `state_property/3` knows the new `explored` property (added to `prob2_interface.pl`). With an older probcli it behaves like "Random solution for Parameters".

## Improvements
- **Model checking:** the progress dialog and its cancel button now stay responsive during model checking.
- **probcli crashes:** ProB restarts probcli automatically after a crash, instead of requiring a Rodin restart.
- **Error reporting:** errors inside ProB commands are no longer swallowed.
- **ProB Tcl/Tk and ProB2-UI:** starting them gives clearer messages, including the program's last output when it exits with an error.
- **Preferences:** ProB's read-only version preferences are no longer stored or sent to probcli, which removes six error log entries on every model load.
- **State view:** in the right-click menu of a section title (Variables, Constants, Formulas, ...), the entries for values, such as "Show item in history", are disabled. Opening the ProB Console, Graph and Table stays possible.

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
- **Large state spaces:** GraphViz can get slow with thousands of states, so it is stopped after 120 seconds. State Space (Fast), the Signature Merge or an Expression Projection give smaller or faster graphs.
- **ProB version for the WD prover:** "Use ProB WD Prover" needs a probcli that supports the `no_constraint_solving` disprover option.
