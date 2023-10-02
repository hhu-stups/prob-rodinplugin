![ProB logo](https://github.com/hhu-stups/prob-rodinplugin/raw/develop/logo.png)

# The ProB Model Checker and Animator - plugin for Rodin

## License

The ProB source code is distributed under the [EPL 1.0 license](https://www.eclipse.org/org/documents/epl-v10.html).
(C) 2000-2021 Michael Leuschel and many others.

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

- Clone the repository (https://github.com/hhu-stups/prob-rodinplugin)

- We use gradle to manage the dependencies to the libraries, thus you will need gradle installed on your computer.
  (see https://gradle.org/)

- In the workspace directory run the completeInstall task (`gradle completeInstall`), alternatively you can also run the downloadCli and collectDependencies tasks (`gradle downloadCli collectDependencies`). This will download the latest nightly build of the Prolog binary and the required Java libraries (such as the parser libraries).

- Install Eclipse for RCP Development

- Import the projects into Eclipse. At this point Eclipse will complain about errors, the reason is that the target platform (i.e., Rodin) hasn't been setup yet).

- Open the file prob_target.target from the de.prob.core project and click on "Set as Target Platform". Grab a coffee.

- After the target platform was installed and the workspace has been compiled you can run the project as an Eclipse application (use org.rodinp.platform.product as the product in the run configuration)
