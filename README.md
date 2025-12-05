![ProB logo](https://github.com/hhu-stups/prob-rodinplugin/raw/develop/logo.png)

# The ProB Model Checker and Animator - plugin for Rodin

## License

The ProB source code is distributed under the [EPL 1.0 license](https://www.eclipse.org/org/documents/epl-v10.html).
(C) 2000-2024 Michael Leuschel and many others.

For updates please visit the ProB website: https://prob.hhu.de/w/

ProB comes with ABSOLUTELY NO WARRANTY OF ANY KIND! This software is distributed in the hope that it will be useful but WITHOUT ANY WARRANTY. The author(s) do not accept responsibility to anyone for the consequences of using it or for whether it serves any particular purpose or works at all. No warranty is made about the software or its performance.

The ProB binary and source distributions contain the [nauty](https://users.cecs.anu.edu.au/~bdm/nauty/) library, which imply further restrictions: the ProB model checker with nauty symmetry reduction cannot be used for applications with nontrivial military significance.

For availability of commercial support, please contact [Michael Leuschel](https://www.cs.hhu.de/en/research-groups/software-engineering-and-programming-languages/our-team/team/michael-leuschel).

## Bugs

Please report bugs and feature requests on the [ProB issue tracker](https://github.com/hhu-stups/prob-issues).

## Prolog Source Code

The latest source code of the Prolog binary can be downloaded from https://stups.hhu-hosting.de/downloads/prob/source/.
To build the Prolog binaries you require a [SICStus 4](https://sicstus.sics.se/) licence.

## Setting up the development environment

### Requirements

* Java 21 or later (at runtime, the plugin is still compatible with Java 11)
* Maven 3.9 or later
* Gradle (will be downloaded automatically by the Gradle wrapper)

### Building without Eclipse

After cloning the repository, run these commands in the repository root:

```sh
$ ./gradlew prepareMaven
$ mvn -f de.prob.parent/pom.xml verify
```

Adding the -U flag to force re-loading dependencies does not (seem to) work.
Run `./gradlew prepareMaven` before running the mvn command to update.

This will build the plugin into a local Eclipse plugin repository.
To test your build of the plugin, you need to configure this repository in Rodin:

* Open the Rodin preferences and go to Install/Update > Available Software Sites
* Click on "Add..."
* Enter "ProB local" as the name
* Click on "Local..." and select the directory .../prob-rodinplugin/de.prob.repository/target/repository
* Click on "Add"
* Make sure that the "ProB local" repository is checked

Once the repository is configured, use Help > Check for Updates to update the ProB plugin to your locally built version.

Note that if you have both "ProB nightly" and "ProB local" enabled, Rodin will prefer whichever one was built more recently.
To ensure that only your local build is used, you can temporarily uncheck "ProB nightly".

To revert to the official build of the plugin, uncheck the "ProB local" plugin repository, re-check either "ProB" or "ProB nightly", then use Help > Check for Updates again.
(If this doesn't work, you might need to completely uninstall and reinstall the ProB plugin.)

### Building with Eclipse

This requires Eclipse for RCP Development.

After cloning the repository, run this command in the repository root

```sh
$ ./gradlew prepareMaven
```

- Import the projects into Eclipse. At this point Eclipse will complain about errors, the reason is that the target platform (i.e., Rodin) hasn't been setup yet).

- Open the file prob_target.target from the de.prob.core project and click on "Set as Target Platform". Grab a coffee.

- After the target platform was installed and the workspace has been compiled you can run the project as an Eclipse application (use org.rodinp.platform.product as the product in the run configuration)

### Importing the project into IntelliJ IDEA

Be aware that IntelliJ doesn't support Tycho-based builds properly - see [IDEA-186628](https://youtrack.jetbrains.com/issue/IDEA-186628/Tycho-support-is-broken) in the IntelliJ issue tracker.
Thus, getting this project set up in IntelliJ is a bit painful, and not all IntelliJ features will work fully in the end.
It works well enough for many purposes, but if you want a fully supported IDE integration, use Eclipse.

These instructions were written for IntelliJ IDEA 2025.2.
The steps may differ slightly in other IntelliJ versions.

#### First-time setup

1. Open the top-level repository folder ("prob-rodinplugin" or "prob_rodin_plugin") as an IntelliJ project.
	Do *not* import any Maven or Gradle build scripts yet and don't search for sources automatically.
	If you're using an existing IntelliJ project, unlink any Maven or Gradle builds and delete any manually created IntelliJ modules except the top-level one.
	If in doubt, just delete the ".idea" folder and recreate the project from scratch.
2. Optional, but recommended: In the IntelliJ settings, under "Build, Execution, Deployment" > "Build Tools", uncheck the checkbox "Sync project after changes in the build scripts".
	The automatic reload can be annoying here, because certain settings are reset every time the Maven build is reloaded.
	(Even with this checkbox disabled, IntelliJ will notify you when it notices that the Maven build needs to be reloaded.)
3. In the IntelliJ settings, under "Build, Execution, Deployment" > "Build Tools" > "Maven" > "Importing", in the text box "Dependency types", add the text `eclipse-plugin` at the end of the comma-separated list.
4. Edit the file [tycho_build.gradle](./tycho_build.gradle).
	In the function `endRepos`, inside the `configuration` XML tag for the `target-platform-configuration` plugin, add the tag `<requireEagerResolve>true</requireEagerResolve>`.
	(The order relative to other tags inside the `configuration` tag doesn't matter.)
5. Run `./gradlew prepareMaven` to (re)generate the Maven build files.
6. Run `mvn -f de.prob.parent/pom.xml install` (not `verify` as in the regular instructions).
	This installs the ProB plugin files into your Maven local p2 repository (~/.m2/repository/p2), which is necessary for IntelliJ to resolve dependencies across the different ProB plugin projects.
7. In the "Maven" tool window (on the right side by default), click on the "+" ("Add Maven Projects") button in the toolbar and select the file "de.prob.parent/pom.xml".
8. The Maven build should import now.
	You can watch the progress in the "Build" tool window (on the left or bottom side by default).
	Once the import has finished, the "Maven" tool window should show a top-level entry "de.prob.parent" with child entries for every other plugin project in this repository.
	If the import process gives any errors, make sure that you followed all steps above.
9. Optional, but recommended: In the "Maven" tool window, select every subproject whose name ends with ".feature" or ".repository", right-click, and select "Ignore Projects".
10. Open the "Project Structure" window and go to the page "Project Settings" > "Modules".
	For every module (in the left tree), select the "Sources" tab, and set the "Language level" to 11 and mark the folder "src" as "Sources".
	For the project "de.prob.core.tests", mark the folder "src" as "Tests" instead.
	For the project "de.prob.core", additionally mark the folder "prob" as "Excluded".
11. Optional: Create a run configuration of type "Maven" for the project "de.prob.parent" with the goal `install`, and a second one with `--offline clean`, as a convenient way to clean and build the plugin.
12. Optional: Also import the Gradle build and add run configurations for the Gradle tasks `prepareMaven` and `clean`.

#### Whenever the plugin version numbers or dependencies change

1. Make sure that `requireEagerResolve` is still enabled in [tycho_build.gradle](./tycho_build.gradle), as described above.
2. Rerun `./gradlew prepareMaven` to regenerate the Maven build files.
	(If you've already run this command recently, you can run `./gradlew createPoms` instead, which is a bit faster.)
3. Rerun `mvn -f de.prob.parent/pom.xml install`.
	This is especially important if the plugin version numbers have changed - otherwise the import will not find some dependencies!
4. In the "Maven" tool window (on the right side by default), click on the "🔄" icon and select "Reload All Maven Projects" in the dropdown menu.
	Make sure that you use "Reload" and not "Sync" - the "Sync" option will not do what is necessary here!
5. Open the "Project Structure" window and go to the page "Project Settings" > "Modules".
	For every module (in the left tree), select the "Sources" tab, and set the "Language level" to 11.

#### Whenever you reload the Maven build

1. Open the "Project Structure" window and go to the page "Project Settings" > "Modules".
	For every module (in the left tree), select the "Sources" tab, and set the "Language level" to 11.

#### Known issues

* The "Build Project" button/menu item probably won't do the right thing.
	Use a run configuration for the `install` goal instead, as described above.
* IntelliJ cannot find the sources for the Eclipse plugin dependencies.
	If you "Go to Declaration" for a symbol from an external dependency, you will only see a decompilation for the bytecode, not the actual source code.
* IntelliJ doesn't recognize dependencies between ProB plugin projects and treats them as external dependencies instead.
	If you "Go to Declaration" for a symbol from a different plugin, IntelliJ will only find the built jar file and will again only show a decompilation, even though the corresponding source file exists in the same project.
