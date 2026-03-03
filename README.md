## JOE

### An object-oriented LLVM-backed programming language

## What is this?

This is a project that I am working on to gain familiarity with and learn more about compiler development. The intent is to provide an object-oriented language similar to Java in functionality (not syntax), which compiles to an LLVM representation, which is then compiled for a target triple.

## What does it do?

### Source code -> AST -> IR -> LLVM IR -> compiled code for LLVM target triple

#### The intended capabilities of this project are as follows:
* C API compatibility
* Object-oriented (class-method-based) code and inheritance
* Classes/objects
* Interfaces (abstract classes with no concrete methods)
* Rust-style tagged unions (referred to as `enum`s)
* Static/global variables
* Garbage collection
* Default-mutable variables
* `if`/`if-else` statements
* `while` and `for` loops
* Functions
* Recursion
* Anonymous functions/lambdas
* C-style comments
* C-style arithmetic and assignment
* Multiple code source file support
* Casting? (maybe)

## What works

* Language syntax frontend (parsing code -> abstract syntax tree -> intermediate representation)
* Language backend (intermediate representation -> LLVM IR -> compiled code) for all features described in this section below
* Arithmetic and variable assignment
* Functions
* Recursion
* Early/conditional returns
* `while` loops
* `if`/`if-else` statements
* Arrays of primitives and arrays of arrays
* Basic memory allocation
* C API compatibility with primitives, pointers, and var-args

## What does not work?

* Classes and objects
* C API compatibility with structs

## What is yet to be implemented

* Universal base class
* Garbage collection
* Static/global variables
* Static analysis and code checking
* Anonymous functions/lambdas
* Multiple code source file support
* Casting

