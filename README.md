# Creating and Running Threads

The purpose of this exercise is to practice creating and starting threads.  

Duration: _30 minutes_

## Description

In this exercise, you will start threads in two ways: by inheriting the 
`Thread` class and by implementing the `Runnable` interface.

Now, please proceed to `IncDecThreads` class and implement the following content:
* `public static void main(String[] args)` \
  Creates and executes exactly two threads. The first thread is for incrementing data, the second is for decrementing the same data.


* `static class Increment` \
  A thread class that is described by inheriting the `Thread` class. Its task in the loop at each iteration is to increase by __1__ and output the value of the `value` variable to the console. The names of the class and the thread are also printed to the console.

* `static class Decrement ` \
 A thread class that is described through an implementation of the `Runnable` interface. Its task in the loop at each iteration is to decrease by __1__ and display the value of the `value` variable in the console. The names of the class and the thread are also printed to the console.

### Details

* 	The `IncDecThreads` class contains  static fields:  `COUNT` defines the number of iterations for threads, and `value` is a common variable that is handled by threads.
* The threads must run concurrently. Run the threads only after both have been created.


> **Note**: \
> Launching the program several times may produce different outputs to the console. Moreover, the result of increment and decrement operations can be intermittent. This is fine because the virtual machine controls thread execution; you cannot influence this.

### Restrictions
You must not: 
*	Set names of threads. 
*	Use synchronization. 

Do not use __daemon__ threads.

## Example

One of the options for executing the program
```
Increment : Thread-0 : 1
Increment : Thread-0 : 2
…
Increment : Thread-0 : 51
Decrement : Thread-1 : 50
Decrement : Thread-1 : 49
Decrement : Thread-1 : 48
Increment : Thread-0 : 52
Decrement : Thread-1 : 47
Increment : Thread-0 : 48
Increment : Thread-0 : 49
```
