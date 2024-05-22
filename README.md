# P4-RaceGuard-ParserGenerator

This project involves designing and developing a compiler for a new programming language, RaceGuard, 
which supports multi-threaded programming and aims to guarantee the avoidance of race conditions. 
The implementation leverages the Java Compiler Compiler (JavaCC) for parsing,
and uses ANTLR for creating the Abstract Syntax Tree (AST) and performing type and scope checking. 
The compiler ensures safe multi-threading by employing the actor model to prevent shared data access between threads, 
thus eliminating race conditions.

## Prerequisites

Before you begin, ensure you have met the following requirements:

* You have installed the latest version of Java 21 and Maven 3.5 or later.

## Installing P4-ParserGenerator

To install P4-ParserGenerator, follow these steps:

1. Clone the repository:

```
git clone https://github.com/cs-24-sw-4-03/P4-ParserGenerator.git
```