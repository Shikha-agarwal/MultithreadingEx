package otherThreadIssues;

public class Main {
}
/*
Atomic Action:
When a thread is running, it can be suspended when it's in the middle of doing something. Ex if a thread calls sout method
it can be suspended in the middle of executing the method. It may have evaluated the argument that is being passed, but it
is suspended before it can print result. Or it may be pathway through evaluating the argument when it's suspended.
So, sout is not an atomic action.

An atomic action can not be suspended in the middle of execution. It either completes or it doesn't happens at all. Once a
thread starts to run an atomic action, we can be confident that it won't be suspended until it has completed the action.

Following are atomic operations
1. Reading and Writing reference variables. Ex: myObject1 = myObject2.

2. Reading and Writing primitive variables, except those of type long and doubles. Ex: myInt = 10 (Atomic Action). But
   myDouble = 1.234 is not atomic action it require 2 operations to read and write long values and thread may be suspended
   in between.

3. Reading and writing all variables declared volatile.

VOLATILE VARIABLES
We may think that since we don't have to worry about thread interference with atomic actions, that we don't need to
synchronize them, but that isn't true.
Because of the way java manages memory, it's possible to get memory consistency errors when multiple threads can read and
write same variables. Each thread has CPU cache, which can contain copies of values that are in main memory. So it's
faster to read from cache, this can improve the performance of an application. There wouldn't be problem if there is
one CPU, but these days most computers have more than 1 CPU.

When running an application, each thread may be running on different CPU, and each CPu has its own cache memory.

Suppose we have 2 threads that use same int counter. Thread1 reads and write counter. Thread2 only reads counter. As we
know reading and writing to an int is atomic action. A thread won't be suspended in the middle of reading writing value
to memory. But lets suppose Thread1 is running on CPU1 and Thread2 is running on CPU2. Because of CPU caching, following can happen
1. The value of counter is 0 in main memory.
2. Thread1 reads the value of 0 from main memory.
3. Thread1 adds 1 to value.
4. Thread1 write the value of 1 to its CPU cache
5. Thread2 reads the value of counter from main memory and gets 0, rather than latest value which is 1.

This is where volatile variable comes in. When we use a non-volatile variable the JVM doesn't guarantee when it writes
an updated value back to main memory. But when we use volatile variable, the JVM writes the value back to main
memory immediately after a thread updates the value in its CPU cache. It also guarantees that every time a variable
reads from volatile variable, it will get latest value.

To make variable volatile, we use volatile keyword.
public volatile int counter;

Now we might be thinking that we don't have to synchronize code that uses volatile variables. But that's not entirely true.
In our example only 1 thread is updating the variable. In that case we don't need synchronization. But if more than 1
thread update the value of volatile variable then we still get race condition.

Lets assume that we have 2 threads and both share volatile int counter and each thread run the following code
counter++;
This isn't atomic operation. A thread has to do the following:
1. Reads the value of counter from memory
2. Add 1 to counter
3. Write the new value of counter back to memory

A thread can be suspended in any of these steps. Because of that, the following can happen:
1. The value of counter is 1 in main memory and in Thread1 and Thread2's CPU caches.
2. Thread1 reads the value of counter and gets 1.
3. Thread2 reads the value of counter and gets 1.
4. Thread1 increments the value and gets 2. It writes 2 to its cache. JVM immediately writes the value to main memory.
5. Thread2 increments the value and gets 2. It writes 2 to its cache. JVM immediately writes the value to main memory.
6. Oops.... The counter has been incremented twice, so its value should now be 3 but it's 2.

A memory consistency error like this can occur when a thread can update the value of variable in a way that depends
on existing value of variable. In counter++ case, the result increment depends on existing value of variable.

A common use of volatile is with variables of type long and double. Reading and writing long and doubles isn't atomic.
Using volatile makes them atomic but we still have to use synchronization as in case stated above.

We had seen java.util.concurrency package. Another sub package is java.util.concurrent.atomic package. This package
provides us with classes that we can use to ensure that reading and writing variables is atomic.

Lets suppose we have multiple threads using following Counter class:
class Counter{
    private int counter = 0;

    public void inc(){
        counter++;
    }

    public void dec(){
        counter--;
    }

    public int value(){
        return counter;
   }
}

Now there is potential for thread inference. because increment and decrement operations aren't atomic. Once again increment
involves following steps:
1. Reads the value of counter from memory
2. Add 1 to counter
3. Write the new value of counter back to memory

A thread can be suspended between any of these steps. As a result following can happen
1. Thread1 reads the value of counter and gets 5.
2. Thread1 suspends.
3. Thread2 reads the value of counter and gets 5. It add 1 to value amd write 6 to counter and gets suspends.
4. Thread3 reads the value of counter and gets 6. It add 1 to value and write 7 to counter and gets suspends.
5. thread1 runs again. It adds 1 to value of 5 and write 6 to counter
6. oops.

Now instead of using int counter we will update the code to use an AtomicInteger object. When using one of atomic
types, we don't have to worry about thread interference. Classes in java.util.concurrent.atomic package support lock
free thread safe programming on single variable.
There are Atomic classes for following types: boolean, integer, integer array, long, long array, object reference and
double.

















































 */