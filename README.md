# Concurrent threads

The purpose of this exercise is to familiarize you with basic concepts 
of starting threads.

Duration **30 minutes**.

## Description 

In this exercise you need to start threads in two ways.
Proceed to `ConcurrentThreads` class, which in the `test()` method 
creates and starts two threads.
* `test()` \
  creates and runs exactly two threads. The first is for the 
  `Incrementor` class and the second for the `Decrementor`. 
  Then executes `wait()` method and then returns `value`.

`Increment` class is the child of `Thread` class. 
* `void run()` \
  in a loop _increments_ **N** times the static variable `value`.

`Decrement` class is the implementor of `Runnable`. 
* `void run()` \
  in a loop _decrements_ **N** times the static variable `value`.

### Details

The threads must run concurrently. Firstly create both objects and 
only then run threads.

It's ok if the result, _in this task_, is not zero after threads 
are finished;

### Restrictions

You must not set names for threads.

You are not allowed to use any classes from any packages except 
`java.lang` package.

> Do not use _daemon_ threads. 
