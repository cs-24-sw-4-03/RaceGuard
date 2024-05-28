# RaceGuard

This project involves designing and developing a compiler for a new programming language, RaceGuard, 
that aims to guarantee the avoidance of race conditions. 
The implementation uses ANTLR for parsing and creating the Concrete Syntax Tree (CST).
The visitor pattern is leveraged to construct the Abstract Syntax Tree (AST). Subsequently, the AST is traversed using the visitor pattern to perform scoping, type checking and code generation.
The code generation process makes use of Untyped Akka to implement the actor model.
Thus the compiler aims to avoid race conditions by employing the actor model to prevent shared data acess between threads.

## Prerequisites

Before you begin, ensure you have met the following requirements:

* You have installed the latest version of Java 21 and Maven 3.5 or later.

## Installing P4-ParserGenerator

To install P4-ParserGenerator, follow these steps:

**1.** Clone the repository:
```
git clone https://github.com/cs-24-sw-4-03/RaceGuard.git
```
**2.** Navigate to ```./generator``` and package the Maven project with the command below.
```
mvn clean package
```
**3.** Run RaceGuard Compiler and specify FILE PATH. Default path is ```./generator/input``` and does not have to be specified. Example of path could be ```test/non-sequential/MatrixMult.par```
```
java -cp target/RaceGuard-1.0-jar-with-dependencies.jar org.RaceGuard.RaceGuard <FILE PATH>
```
You should now have target files in ```./output```. <br>
**4.** Navigate to ```./output``` and run ```mvn clean package```.  <br>
**5.** Use Maven to execute the target code. This will run ```output.Main```
```
mvn exec:exec
```
---
The use of RaceGuard is at your own risk, and we assume no responsibility for any consequences of its use.
