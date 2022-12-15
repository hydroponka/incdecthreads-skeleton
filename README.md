# Concurrent threads

The purpose of this exercise is to familiarize you with basic concepts
of starting threads.

Duration **30 minutes**.

## Description

In this exercise, you need to start threads in two ways, by inheriting
`Thread` class and by implementing `Runnable`.

Proceed to `IncDecThreads` class, which in the `main()` method
creates and starts two threads.
* `public static void main(String[] args)` \
  creates and executes exactly two threads. The first is for the
  `Increment` class, and the second is for the `Decrement` class.

`Increment` class is the child of `Thread` class.
* `void run()` \
  **N** times in a loop _increments_ the static variable `value`,
  and then prints to the console the name of the class, the name of
  the thread and the `value`.

`Decrement` class is the implementor of `Runnable`.
* `void run()` \
  **N** times in a loop _decrements_ the static variable `value`,
  and then prints to the console the name of the class, the name of
  the thread and the `value`.

### Details

The threads must run concurrently. Firstly create both objects and
only then run threads.

It's OK if the output is intermittent.

> **Note**: \
> The tests check that the output is intermittent.

### Restrictions

You must not set names for threads.

You are not allowed to use any synchronization due the definition.

> Do not use ___daemon___ threads.

### Example of output

```
Decrement : Thread-1 : 50
Decrement : Thread-1 : 49
Decrement : Thread-1 : 48
Increment : Thread-0 : 52
Decrement : Thread-1 : 47
Increment : Thread-0 : 48
Increment : Thread-0 : 49
```
