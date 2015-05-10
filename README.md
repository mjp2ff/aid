# AID - Assisted Improvement of Documentation

This tool provides an implementation of a set of algorithms for identifying and improving poor summary documentation for Java methods. The goals of this project are fourfold:

* Identify important concepts in the method source that should be documented in the method's Javadoc summary
* Identify incomplete documentation by searching for key components of the method's functionality in its summary documentation
* Focus documentation efforts by presenting the most poorly documented methods to the end user
* Assist with the improvement of documentation by providing a list of suggestions for improvement of each method's documentation

## Getting Started

Make sure you have the following dependencies installed before trying to run AID:

* [Java 8](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html)
* [Maven 3](http://maven.apache.org/download.cgi)

To ensure that you are running the right versions of these projects, you can run the following command:

```
> mvn -v
Apache Maven 3.2.3 (33f8c3e1027c3ddde99d3cdebad2656a31e8fdf4; 2014-08-11T16:58:10-04:00)
Maven home: /usr/local/apache-maven-3.2.3
Java version: 1.8.0_25, vendor: Oracle Corporation
...
```

Once you have the dependencies installed, clone the repository and configure it.

```
git clone git@github.com:mjp2ff/aid.git
./config.sh
```

Finally, the following command will allow you to run AID on a file:

```
./run.sh -m files path/to/file.java
```

## Options

AID conforms to the following command line interface:

```
./run.sh -m files|directories|projects|methods|train [-i] [-d] file/path/one file/path/two ...
```

* Mode (`-m`/`--mode`) - *REQUIRED* - Specifies the mode that AID should run in. Several values can be provided:
* * files - Processes all methods found in each file provided. Paths must be to files, *not* directories.
* * directories - Processes all methods located within each directory provided. Paths must be to directories, *not* files.
* * projects - Process all methods in the provided projects, using the ANT buildfiles to find the locaitons of relevant methods
* * methods - Process only the specific methods identified by the csv file provided. Each line of the csv file provides the following pieces of information, each separated by a comma: absolute file path, method name, parameter 1 name, parameter 2 name, ...
* * train - Trains the classifier needed for identifying a method's primary action. Two paths must be provided for this mode to function properly. The first is the path to the directory containing the training data, and the second is the path to the directory where the classifier model should be stored. Currently, the model must be stored in the `training` directory for AID to function properly. A set of a few hundred training instances can be found in the `training/data` directory.
* Individual (`-i`/`--individual`) - *OPTIONAL* - If this flag is included, the output of AID only includes one method at a time, providing both the difference score and suggestions as well as the associated method's source code. If the flag is not included, 10 methods are displayed at a time, but the method source for each is not shown.
* Documented Only (`-d`/`--documented-only`) - *OPTIONAL* - If this flag is included, the tool only analyzes methods that have a Javadoc comment. This is useful if you have many methods that are intentionally left undocumented, since these will often show up among the worst-documented methods in the rankings.

## Authors

* Matt Pearson-Beck
* Jeff Principe
