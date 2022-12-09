# Concurrent threads

The purpose of this exercise is to familiarize you with basic concepts 
of starting threads.

Duration **30 minutes**.

## Description 

In this exercise, you need to start threads in two ways and take a look
at the "_data race_" problem that can occur in parallel programming.
In this exercise you will not solve the "data race" problem. 

Proceed to `ConcurrentThreads` class, which in the `test()` method 
creates and starts two threads.
* `test()` \
  creates and executes exactly two threads. The first is for the 
  `Increment` class, and the second is for the `Decrement` class. 
  Then executes the `wait()` method and then returns the value.

`Increment` class is the child of `Thread` class. 
* `void run()` \
  in a loop _increments_ **N** times the static variable `value`.

`Decrement` class is the implementor of `Runnable`. 
* `void run()` \
  in a loop _decrements_ **N** times the static variable `value`.

### Details

The threads must run concurrently. Firstly create both objects and 
only then run threads.

It's OK if some results _in this task_ are non-zero after the threads 
have finished. 

> **Note**: \
> The tests check whether both non-zero and zero results exist. 
> You can use `runtests.cmd` to check if your code is passing 
> the tests steadily before pushing your solution. 
> It runs the tests 20 times.

### Restrictions

You must not set names for threads.

You are not allowed to use any synchronization due the definition.

> Do not use _daemon_ threads. 
