<img src="https://github.com/bendisposto/prob/raw/develop/logo.png" width="500" align="center">

# The ProB Model Checker and Animator

The ProB source code is distributed under the EPL license (http://www.eclipse.org/org/documents/epl-v10.html).
(C) 2000-2011 Michael Leuschel and many others.

For updates please visit the ProB website: http://www.stups.uni-duesseldorf.de/ProB/

ProB comes with ABSOLUTELY NO WARRANTY OF ANY KIND ! This software is distributed in the hope that it will be useful but WITHOUT ANY WARRANTY. The author(s) do not accept responsibility to anyone for the consequences of using it or for whether it serves any particular purpose or works at all. No warranty is made about the software or its performance.

The ProB binary and source distributions contain the nauty library (http://cs.anu.edu.au/~bdm/nauty/) which imply further restrictions: the ProB model checker with nauty symmetry reduction cannot be used for applications with nontrivial military significance. 

For availability of commercial support, please contact Formal Mind (http://www.formalmind.com).

# Contributing

## Setting up the development environment

- Clone the repository (http://github.com/bendisposto/prob) 
  We suggest to fork the project on github (see https://help.github.com/articles/fork-a-repo)

- We use gradle to manage the dependencies to the libraries, so you will need gradle installed on your computer. 
  (see http://www.gradle.org/)

- Open a shell and cd into the de.prob.releng directory

- Run the downloadCli task (gradle downloadCli). This will download the latest nightly build of the Prolog binary.

- Run the collectDependencies task (gradle collectDependencies). This will download the required libraries (such as apache commons, etc.)

- Install Eclipse (Indigo/Juno) for RCP Development

- Import the projects into Eclipse (note that de.prob.releng is not an Eclipse project, it can be left out). At this point Eclipse will show many errors, the reason is that the Target platform (i.e., Rodin hasn't been setup yet)

- Open the file prob_target.target from the de.prob.core project and click on "Set as Target Platform". Grab a coffee. 




