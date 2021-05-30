package concurrentPackageAndThreadInterface.reentrantLockAndUnlock;

import concurrentPackageAndThreadInterface.producerConsumer.ThreadColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        List<String> buffer = new ArrayList<>();
        ReentrantLock bufferLock = new ReentrantLock();
        MyProducer producer = new MyProducer(buffer, ThreadColor.ANSI_PURPLE, bufferLock);
        MyConsumer consumer1 = new MyConsumer(buffer, ThreadColor.ANSI_BLUE, bufferLock);
        MyConsumer consumer2 = new MyConsumer(buffer, ThreadColor.ANSI_RED, bufferLock);

        new Thread(producer).start();
        new Thread(consumer1).start();
        new Thread(consumer2).start();
    }
}

class MyProducer implements Runnable {
    private final List<String> buffer;
    private final String color;
    private final ReentrantLock bufferLock;

    public MyProducer(List<String> buffer, String color, ReentrantLock bufferLock) {
        this.buffer = buffer;
        this.color = color;
        this.bufferLock = bufferLock;
    }

    @Override
    public void run() {
        Random random = new Random();
        String[] nums = {"1", "2", "3", "4", "5"};
        for (String num : nums) {
            try {
                System.out.println(color + "Adding...." + num);
                bufferLock.lock();
                buffer.add(num);
                bufferLock.unlock();
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                System.out.println("Producer was interrupted.");
            }
        }

        System.out.println("Adding EOF and exiting....");
        bufferLock.lock();
        buffer.add("EOF");
        bufferLock.unlock();
    }
}

class MyConsumer implements Runnable {
    private final List<String> buffer;
    private final String color;
    private final ReentrantLock bufferLock;

    public MyConsumer(List<String> buffer, String color, ReentrantLock bufferLock) {
        this.buffer = buffer;
        this.color = color;
        this.bufferLock = bufferLock;
    }

    public void run() {
        while (true) {
            bufferLock.lock();
            if (buffer.isEmpty()) {
                bufferLock.unlock();  //Line 1
                continue;             //Line 2
            }
            if (buffer.get(0).equals("EOF")) {
                System.out.println(color + "Exiting...");
                bufferLock.unlock(); //Line 3
                break;              //Line 4
            } else {
                System.out.println(color + "Removed " + buffer.remove(0));
            }
            bufferLock.unlock();     //Line 5

        }
    }
}
/*
In MyConsumer class, if we dont write Line 1 and Line 3 then program will stuck because when thread execute run method then
either Line 2 or Line 4 will execute and we start from beginning of loop again and Line 5 will not be able to get execute
As a result, lock will not be released and other threads(Producer and Other Consumer) will not be able to acquire lock.
 */


/*
Reentrant Lock: If a thread is already holding the Reentrant lock when it reaches the code that requires the same lock, it can
continue executing. It doesn't have to obtain lock again.

        bufferLock.lock();      // to acquire lock
        buffer.add("EOF");      can only be executed of the thread acquire lock. Works same as synchronized block.
        bufferLock.unlock();    // to release lock

One drawback of using this lock instance is that we had to manage the acquiring and releasing of lock manually. It is not
done automatically as in case of synchronized block.

Note that the same thread can acquire more than one lock. In other words, if a thread acquire a lock using bufferLock.lock()
method and it doesn't execute bufferLock.unlock() method, and then again that thread execute bufferLock.lock() method then
again it will have lock and in total it will have 2 locks. And then it will have to call bufferLock.unlock() method 2 times
to make bufferLock free.

 */





