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
  - `:states`: statistics about the state space explored so far, e.g. after model checking: the number of states and transitions, the states by kind (e.g. open, live, deadlocked, invariant violated), the transitions per event, and the events not executed yet. It needs a running animation, but no current state.
  - `:table EXPR` / `:graph EXPR`: show the value of EXPR in the ProB Table or ProB Graph view, like "Show Value as Table/Graph" in the State view. The view is opened if necessary, and the console keeps the focus.
  - `:prefs [TEXT]`: ProB's preferences, with their defaults if changed
  - `:version`: the versions of ProB (probcli, with its Git revision, last change date and Prolog version), of the ProB plug-ins, of Rodin's Event-B core and of Java. probcli's version needs a running animation.
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
- **Going to a state:** in graphs of states (e.g. State Space), double-click a state to make it the current state, reached by the shortest trace from the root. The Current State in State Space graph is then computed again automatically, so that it shows the surroundings of the new current state.
- **Going to a state of a merged node:** in the Signature Merge and State Space Expression Projection graphs, double-click a node to go to one of its states: directly if it stands for a single state, otherwise a menu lists its states (the first 30, the current state marked) to choose from. Requires a probcli with `prob2_get_equivalence_class_states/4` (in `prob2_interface.pl`); with an older probcli the example state named in the node's tooltip is used.
- **Opening a model:** in the Machine Hierarchy graph, double-click a machine or context to open it in its default editor (e.g. Rodin's Event-B editor). In the Event Hierarchy graph, double-click the box of a machine to open that machine, or an event to open its machine with the event selected. This works in Rodin's Event-B editor and in text editors such as Camille (where the event's declaration `event NAME` is selected); other editors just open the machine. Likewise, in the Variable Refinement Hierarchy graph, double-click the box of a machine or context to open it, or a variable (or a constant or set of a context) to open its machine or context with it selected: in Camille its first occurrence after `variables` (`constants`, `sets`) is selected. A variable kept from an abstraction is selected in the machine whose box it is drawn in.
- **From the State view:** the right-click menu of a variable or formula has two new entries. "Show Value as Graph" draws its value, e.g. a relation, as a graph. "Project State Space onto Item" merges the states that have the same value. Both are only enabled for entries with a formula: variables and expressions or predicates such as invariants, axioms or the guard of an event (the event's guard is used, not its name), not for sections such as "invariants", nor for parts of a formula that refer to quantified or local identifiers, e.g. a conjunct of the guard of an event with parameters (they cannot be evaluated on their own). The same holds for "Show Value as Table", which is moreover only enabled for expressions. This needs a probcli with `evaluation_formula_text/3` (in `eclipse_interface.pl`); with an older one, the entry's label is used as before.
- **Graphs for a formula:** graphs whose names end in "…" use the expression or predicate in the field above the graph, e.g. State Space Expression Projection… (states merged by the value of an expression, or by the truth value of a predicate; the latter needs a probcli with `parse_expression_or_predicate_raw_or_atom_with_prob_ids/2` in `bmachine.pl`), Expression as Graph… and Formula Tree…. Enter recomputes the graph. If a predicate has an error (e.g. a type error), that error is shown rather than "expected: an expression but was: predicate". The field translates ASCII to symbols and completes identifiers with Tab or Ctrl+Space.
- **Navigation:** the mouse wheel zooms and dragging moves the graph. The + / − / Fit buttons zoom in, zoom out, or fit the whole graph into the view.
- **Refresh** recomputes the graph, e.g. after animating or model checking. Computations run in the background. The red Stop button interrupts ProB or, once ProB has written the graph, GraphViz.
- **Preferences:** a toolbar button with a gear icon, next to Refresh, edits the ProB preferences relevant for the shown graph, e.g. colours, shapes, whether self-loops are shown or the graph title: check boxes, drop-down lists and fields (with a colour chooser for colours); hovering shows the preference's name in ProB and its default, and "Restore Defaults" resets them. OK sets the changed preferences in ProB and computes the graph again. They are stored by ProB for Rodin and set again whenever a model is loaded (also those not on the ProB preference page). They are set with `set_eclipse_preference/2`, so only preferences with an eclipse name are shown (most of them are advanced preferences, not on the ProB preference page). The formula tree time-outs got the eclipse names `FORMULA_TREE_MIN_TIMEOUT` and `FORMULA_TREE_MAX_TIMEOUT` (in `preferences.pl`).
- **Save Graph As...:** a toolbar button (with Eclipse's Save icon) saves the shown graph as an SVG image (`.svg`) or as ProB's GraphViz file (`.dot`), e.g. to lay it out differently or to include it in a document. The format follows the file's extension, or the file type chosen in the dialog. The proposed file name is the model's name followed by the graph's title, e.g. `M1_State_Space.svg`.
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
- **Save Table As...:** a toolbar button (with Eclipse's Save icon) saves the shown table, in its current sort order, as comma-separated values (`.csv`, e.g. for a spreadsheet) or as text with aligned columns (`.txt`). The format follows the file's extension, or the file type chosen in the dialog. The proposed file name is the model's name followed by the table's title, e.g. `M1_Machine_Statistics.csv`. Files are written in UTF-8, so mathematical symbols are kept.
- **Refresh** recomputes the table in the current state, e.g. after animating or model checking. Computations run in the background, and the red Stop button interrupts them.
- **Preferences:** a toolbar button with a gear icon, next to Refresh, edits the ProB preferences relevant for the shown table, e.g. the maximal number of rows or columns: check boxes, drop-down lists and fields (with a colour chooser for colours); hovering shows the preference's name in ProB and its default, and "Restore Defaults" resets them. OK sets the changed preferences in ProB and computes the table again. They are stored by ProB for Rodin and set again whenever a model is loaded (also those not on the ProB preference page). They are set with `set_eclipse_preference/2`, so only preferences with an eclipse name are shown (most of them are advanced preferences, not on the ProB preference page). The formula tree time-outs got the eclipse names `FORMULA_TREE_MIN_TIMEOUT` and `FORMULA_TREE_MAX_TIMEOUT` (in `preferences.pl`).
- **Errors** while computing a table are shown in the view instead of in a dialog: a red summary in the status line, and ProB's error messages as the rows of the table, where they can be copied.
- **Not offered:** the VisB and MC/DC tables, and ProB's internal profiling tables (external function profile, cache info).

### Start animation from the editors
- **Camille:** a ProB toolbar button, and "Start Animation / Model Checking" in the right-click menu of the Camille text editor.
- **Rodin Editor (Systerel):** a ProB toolbar button and the same right-click menu entry.
- **Rodin's form editors:** a ProB toolbar button in the machine and context editors.

### Disprover: "Use ProB WD Prover"
A new proof tactic that calls ProB's well-definedness prover without constraint solving. Its time-out is set in the disprover preferences. Proofs found by it are labelled "ProB WD prover" in the proof tree. It is also available as the auto tactic "ProB WD Prover", e.g. for the auto-prover's tactic profiles (Window › Preferences › Event-B › Sequent Prover › Auto-Tactic), next to "ProB (Dis)Prover"; it uses the same time-out.

### Disprover: notice for counter-examples
When the disprover is started from the Proof Control view and finds a counter-example, a notice appears at the bottom right of the window, without blocking work. It names the proof obligation and its machine or context, and says whether the counter-example only holds for the selected hypotheses (the goal may then still be provable with all hypotheses). "Show details" lists the values of the counter-example and the goal. The auto-prover and proof replay show no notice.
- **Visualise:** a "Visualise" link below "Show details" opens the ProB Graph view with ProB's formula tree of the selected hypotheses implying the goal, evaluated in the counter-example: each sub-formula with its value, so you can see which hypotheses hold and why the goal is false. The disprover only reports the values of the goal's identifiers (plus a few others), so ProB solves the sequent again with these values fixed to get the remaining ones, within the disprover's time-out; if that fails, the tree shows the reported values only. The graph can be saved like the others; Refresh does not compute it again. Requires a probcli with `cbc_disprove_counter_example_dot/5` (in `prob2_interface.pl`, implemented by `disprover_counter_example_dot/5` in `disprover.pl`).

### State view: Show Full Value
A new "Show Full Value ..." entry in the right-click menu shows the complete value of a variable or formula. Formula values are not cut off at 600 characters. The dialog can switch between the current and the previous state, and between plain and pretty-printed output.

### State view: open a machine or context
Double-clicking a machine or context name in the State view opens its file in the default editor, e.g. Rodin's form editor, the Rodin Editor or Camille.

### Traces: save, replay and manage them
- **Save Trace:** a new button in the History view's toolbar saves the trace from the start to the current state as a ProB2 JSON trace file (`.prob2trace`), e.g. for replay in ProB2-UI. It is disabled while there is no step to save, i.e. at the start of the history. Its drop-down menu has "Save Trace for Replay", which does the same, leaving room for other ways of saving later.
  - **Description:** before saving, it asks for an optional description of the trace, shown when hovering over the trace in the Event-B Explorer and in its details. It can be changed later with "Edit Description..." in the Explorer. Without one, the trace is described as "File created by ProB for Rodin". Cancelling does not save the trace.
  - **Where:** the file is put in the model's own folder of its project, named like the model (e.g. `M1/` for `M1.bum`), which is created if necessary. It is named after the model with the first unused number, e.g. `M1/M1_1.prob2trace`, `M1/M1_2.prob2trace`. Its metadata records the machine or context file (`.bum`/`.buc`) as the model file.
  - **Afterwards:** the Event-B Explorer is shown with the new trace selected below the model's "Traces" node.
- **Replay Trace:** a new button next to it replays a `.prob2trace` file, e.g. one saved with Save Trace or by ProB2-UI. Its drop-down menu lists the saved traces of the animated machine or context, with their replay status icons, so one can be replayed directly; its first entry, "Other Trace...", like the button itself, chooses any trace file, starting in the model's folder. The replayed trace becomes the history, so you can step through it. A perfect replay is confirmed in the status bar; otherwise a resizable dialog says how many steps were replayed and shows a table explaining how each step of the trace file was replayed: the step as stored in the file, the operation replayed for it, how it matched (precise; ignoring the target state; operation name only; or failed and skipped), the parameters, results and variables whose values differ from the file, and the step's replay errors. Imperfect steps are shown in orange, failed ones in red. ProB's messages, e.g. which step could not be replayed, are shown below the table.
- **Event-B Explorer:** each machine and context with saved traces gets a "Traces" node, after Rodin's nodes such as Proof Obligations, listing them: the trace files in the model's folder, numbered traces in order, shown without their `.prob2trace` extension (as in the Replay Trace drop-down menu). The folder decides which model a trace belongs to, so traces can be renamed freely, and a trace of another model (e.g. of an abstract machine) can be copied into a model's folder to be replayed for it; its details then say which model it was recorded for. Hovering over a trace shows its number of steps and its description, if the trace file has one. The node is updated when traces are saved, renamed or deleted.
  - **Replay:** double-click a trace, or right-click it and choose "Replay Trace with ProB". If its machine or context is not being animated (or was changed), the animation is started first.
  - **Replay All Traces:** right-click a "Traces" node to replay all traces of the machine or context one after the other, in the background with a progress bar that can be cancelled. Each trace's replay status is updated, and a summary lists the perfect, imperfect and failed traces. The animation is started first if necessary, and the history shows the last trace afterwards.
  - **Show Trace Details...:** shows the trace's metadata (when and by whom it was created, ProB version, model and model file, description), its file size and date, its replay status, and its numbered steps with their parameters and results. The text can be selected and copied.
  - **Edit Trace:** opens the trace's JSON file in the text editor (a double-click replays it instead).
  - **Edit Description...:** changes the trace's description (see Save Trace), which is shown in its hover and details. Only the description in the trace file changes, so the trace keeps its replay status.
  - **Copy Trace To:** a sub-menu listing the machines that refine the trace's machine (or the contexts that extend its context), direct refinements first, then indirect ones. Choosing one copies the trace into that model's folder as the next `ModelName_N`, e.g. to check that a refinement can still replay the abstract trace. "Other Model..." chooses any other machine or context of the project in a dialog. The copy is selected in the Explorer, keeps the original's description, shows which model it was recorded for, and has no replay status yet.
  - **Rename Trace...:** gives the trace a new name; the `.prob2trace` extension is kept, and the trace stays with its model.
  - **Delete Trace:** deletes the trace after a confirmation.
  - **Replay status:** the icon of a trace shows the outcome of its last replay: a green check for a perfect replay, a warning for an imperfect one (all steps replayed, but e.g. with other parameters), a red cross if it failed (not all steps could be replayed), and the plain file icon if it was not replayed yet for the current model. Replays from the Explorer and from the History view's Replay Trace button both count. Selecting a trace shows details in the status bar, e.g. the number of steps replayed and when. A trace just saved counts as replayed perfectly, and renaming a trace keeps its status. The status is reset to unknown when the trace is edited or the model changes: the machine or context, or one it depends on (abstract machines, seen or extended contexts). It is stored in the workspace, not in the trace file.
- **Model changes:** saving a trace does not mark the animated model as modified, as only the model's folder changes.
- **Requirements:** a probcli with the new `prob2_save_trace_file/4` and `prob2_replay_json_trace_file_with_messages/5` (in `prob2_interface.pl`); for the table of the replay dialog also `prob2_replay_json_trace_file_with_table/6` (without it, the dialog shows only the messages), and with two fixes for models sent by Rodin (which ProB knows as `package(event_b_project)`, not as a file): writing the JSON metadata no longer fails on the model file, and formatting error messages during a replay no longer raises a type error.

### Operations view: non-deterministic choices
When you right-click an enabled event, each non-deterministic choice is now labelled with the variables it changes and their new values.

### Operations view: "Unexplored Successors First"
A new option in the Double Click Behaviour menu. A double-click on an event picks one of its choices at random, preferring choices that lead to a state ProB has not explored yet. If all successor states are explored, it picks any choice at random. This needs a probcli whose `state_property/3` knows the new `explored` property (added to `prob2_interface.pl`). With an older probcli it behaves like "Random solution for Parameters".

## Improvements
- **Model checking:** the progress dialog and its cancel button now stay responsive during model checking.
- **probcli crashes:** ProB restarts probcli automatically after a crash, instead of requiring a Rodin restart.
- **Error reporting:** errors inside ProB commands are no longer swallowed.
- **Proof information notices:** problems with the proof information of a model no longer open one modal dialog per machine and context when the model is loaded. Instead, one notice for the whole model appears at the bottom right of the window without blocking work. "Show details" lists the affected machines and contexts, and the notice closes itself after 15 seconds unless the mouse is over it. If both problems below occur in one load, they share one notice.
  - **Missing proof status files:** a machine or context without a `.bps` file, e.g. while Rodin has not built the project yet or with Build Automatically switched off, is no longer reported as a Rodin database issue. The notice says that no proof information is used for it, with a hint to build the project.
  - **Database issues:** when proof obligations refer to model elements that no longer exist ("Translation incomplete ... try Project -> Clean..."), the notice says how many were skipped. Such a proof obligation is now skipped completely, instead of being passed to ProB with only some of its sources.
  - **Effect:** in both cases, animation and model checking remain correct, but can be slower.
- **Constants that violate the axioms:** when ProB cannot find valid constants, e.g. because of contradictory axioms, it no longer opens modal dialogs: "ProB could not find valid constants ..." together with "AXIOMS are unsatisfiable (but all CONSTANTS valued)", or the error dialog "ProB reported errors: AXIOMS are unsatisfiable (but some CONSTANTS valued, e.g., z)". Instead, one notice appears at the bottom right of the window, like the proof information notice. It names the marked axioms, and "Show details" lists the axioms that prevented a solution. If all constants got values, the animation can still continue with PARTIAL_SETUP_CONTEXT.
  - **Compute Unsat Core:** below "Show details", the notice has a "Compute Unsat Core" link. It closes the notice and opens the ProB Table view at the "Unsat Core of Properties/Axioms" table, which computes a minimal set of axioms that cannot be satisfied together.
  - **Other setup problems:** "ProB could not find valid constants" (when ProB cannot say which axioms are concerned) and "ProB could not find valid initial values for the variables" are shown in the same notice instead of a modal dialog.
  - **Problem markers:** the axioms concerned get a problem marker, shown in the Problems view, in Rodin's form editor (the predicate is highlighted) and in Camille (the axiom is underlined). Double-clicking the marker opens the context at the axiom. The marker is an error if the axiom is false, or if there is no solution for it together with other axioms, which are then all marked (e.g. `x>y` and `y>x`). It is a warning if ProB could not determine whether the axioms are satisfiable. The markers are replaced when a model is animated again, removed when Rodin rebuilds the context (e.g. after editing it), and not kept when Rodin is closed. The error icons in the Event-B Explorer follow them.
  - **Requirements:** a probcli with the new `unsat_properties_positions/1` and `getErrorMessagesWithSources/2` (in `eclipse_interface.pl`). The latter tells the source of each of ProB's messages, so that its messages about the axioms are recognised without relying on their wording. With an older probcli, the notice is shown without markers, and the messages are recognised by their wording ("CONSTANTS valued").
- **ProB's warnings:** when a command succeeds but ProB reports warnings (e.g. while loading a model), they are no longer shown in a modal "ProB reported warnings" dialog, but in a notice at the bottom right of the window: it shows the first warning and the number of warnings, "Show details" lists them all. ProB's errors are still shown in a dialog.
- **Time-out when finding constants:** "A timeout occured when finding constants ..." (when a context is animated and ProB cannot find all solutions in time) is a notice instead of a modal dialog; "Show details" has the advice about animation refinements.
- **State view evaluation failures:** when ProB cannot evaluate an entry of the State view, it is shown as "unknown value" and a notice names the entry, with ProB's messages in its details. Before, a modal "Something went wrong while evaluating" dialog appeared for each such entry, after a further dialog with ProB's errors.
- **Several notices:** notices no longer replace each other: up to four are stacked at the bottom right, the newest at the bottom (the oldest is closed when a fifth appears). A notice with the same message as a shown one replaces it.
- **Machines with Rodin problems:** the message "The machine contains Rodin Problems. ProB will continue, but you may observe unexpected behaviour" is no longer a modal error dialog. It is shown as a notice at the bottom right of the window, naming the machine; "Show details" explains that Rodin's static checker marked it as not accurate and points to the Problems view.
- **ProB Tcl/Tk and ProB2-UI:** starting them gives clearer messages, including the program's last output when it exits with an error.
- **Preferences:** ProB's read-only version preferences are no longer stored or sent to probcli, which removes six error log entries on every model load.
- **BMotion Studio perspectives:** the BMS Edit and BMS Run icons no longer stay in the perspective bar (top right) after use. Closing the last visualisation, including with "Close Others" or "Close All" on editor tabs, also closes these two perspectives. If one of them was shown, Eclipse switches back to the previously used perspective. They reappear when a visualisation is started, and are kept when Rodin is closed with a visualisation open.
- **Where the ProB Console, Graph and Table views open:** they now open together, as tabs of one stack, in the middle below the editor area: in the ProB perspective (whose editor area is hidden, so they get the middle column between the Operations view and the State view) and in Rodin's Event-B and Proving perspectives. They can still be moved or detached as usual. An existing perspective layout is stored in the workspace: use Window › Perspective › Reset Perspective... once to get the new placement.
  - **Separate window:** a new preference page, Window › Preferences › ProB › Views, has the option "Open the ProB Console, Graph and Table views in a separate window" (off by default; stored by ProB for Rodin, not by ProB). When it is on, these views open detached, in one window of their own (a view opened while another of them is detached joins its window as a tab). Views that are already detached stay where they are; a view docked again into the main window is detached again the next time it is opened.
- **Icons for starting ProB:** the ProB perspective has its own icon in the perspective bar (top right): the ProB logo, a little smaller, like Rodin's perspective icons, with a green "run" triangle. The ProB buttons in the editors' toolbars and the "Start Animation / Model Checking" menu entries use the ProB logo at full size with a green "run" button (a white triangle on a green disc). Both icons have a high-resolution variant for HiDPI screens. The other ProB icons are unchanged.
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
  - replaying a trace (e.g. by double-clicking it in the Event-B Explorer) no longer sometimes shows "An error has occurred": the Operations view could redraw while the replay had reset the history, when there was no current state yet
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
- `de.prob.ui` requires `org.eclipse.ui.navigator` (for the traces in the Event-B Explorer)
- `de.prob.ui` requires `org.eclipse.e4.ui.model.workbench` and `org.eclipse.e4.ui.workbench` (for opening the ProB Console, Graph and Table views in a separate window)
- build fixes: closing download streams, no stale parser jars
- new `Makefile` for local builds

## Notes
- **Free identifiers:** in the console, they need inferable types. Add a typing predicate such as `x : INT` if needed.
- **Large state spaces:** GraphViz can get slow with thousands of states, so it is stopped after 120 seconds. State Space (Fast), the Signature Merge or an Expression Projection give smaller or faster graphs.
- **ProB version for the WD prover:** "Use ProB WD Prover" and the "ProB WD Prover" auto tactic need a probcli that supports the `no_constraint_solving` disprover option. The "Visualise" link of the counter-example notice needs a probcli with `cbc_disprove_counter_example_dot/5`.
