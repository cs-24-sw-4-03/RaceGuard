# P4-RaceGuard-ParserGenerator

This project involves designing and developing a compiler for a new programming language, RaceGuard, 
that aims to guarantee the avoidance of race conditions. 
The implementation uses ANTLR for parsing and creating the Concrete Syntax Tree (CST).
The visitor pattern is leveraged to construct the Abstract Syntax Tree (AST). Subsequently, the AST is traversed using the visitor pattern to perform scoping, type checking and code generation.
The code generation process makes use of Untyped Akka to implement the actor model.
Thus the compiler aims to avoid race conditions by employing the actor model to prevent shared data acess between threads.
## Prerequisites

Before you begin, ensure you have met the following requirements:

* You have installed Java 21 and Maven 3.5 or later versions.

## Installing P4-ParserGenerator

To install P4-ParserGenerator, follow these steps:

1. Clone the repository:

```
git clone https://github.com/cs-24-sw-4-03/P4-ParserGenerator.git
```
