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

* Java 11 or later
* Maven 3 or later (Maven 3.9 recommended)
* Gradle (will be downloaded automatically by the Gradle wrapper)

### Building without Eclipse

After cloning the repository, run these commands in the repository root:

```sh
$ ./gradlew prepareMaven
$ mvn -f de.prob.parent/pom.xml install
```

Adding the -U flag to force re-loading dependencies does not (seem to) work.
Run ```./gradlew prepareMaven``` before running the mvn command to update.

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
