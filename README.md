SPAN Digital Coding Exercise
============================
A sample Scala project written for SPAN Digital that calculates league
results using match data.


Execution Requirements
----------------------
* JRE 8+


Execution
---------
1. Install JRE 8+
2. Navigate to https://github.com/OOPMan/span-digital-exercise/releases
3. Download a pre-compiled JAR release
4. Open a shell window and navigate to the location the pre-compiled JAR release was saved
5. Execute `java -jar span-digital-exercise.jar --help`


Compilation Requirements
------------------------
* Git
* JDK 8+
* SBT 1.x

The simplest way to get up and running with JDK 8+ and SBT is to
visit one of the following links:

* Linux: https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html
* Mac: https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Mac.html
* Windows: https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Windows.html

Compilation and execution of this project has only been tested
on Kubuntu 17.10 using SBT installed via the official Debian package


Compilation
-----------
1. Install git
2. Install JDK 8+
3. Install SBT 1.x
4. Open a shell and navigate to a suitable work location
5. Execute `git clone https://github.com/OOPMan/span-digital-exercise.git`
6. Execute `cd span-digital-exercise`
7. Execute `sbt assembly`. This will cause SBT to:

  1. Download dependencies
  2. Compile the source code
  3. Run the unit tests
  4. Package the binaries into a fat JAR located in `target/scala-2.12`


Unit Tests
----------
1. Follow steps 1 through 6 of the *Compilation* process
2. Execute `sbt test`
