package concurrentPackageAndThreadInterface.producerConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        List<String> buffer = new ArrayList<>();
        MyProducer producer = new MyProducer(buffer, ThreadColor.ANSI_PURPLE);
        MyConsumer consumer1 = new MyConsumer(buffer, ThreadColor.ANSI_BLUE);
        MyConsumer consumer2 = new MyConsumer(buffer, ThreadColor.ANSI_RED);

        new Thread(producer).start();
        new Thread(consumer1).start();
        new Thread(consumer2).start();
    }
}

class MyProducer implements Runnable {
    private final List<String> buffer;
    private final String color;

    public MyProducer(List<String> buffer, String color) {
        this.buffer = buffer;
        this.color = color;
    }

    @Override
    public void run() {
        Random random = new Random();
        String[] nums = {"1", "2", "3", "4", "5"};
        for (String num : nums) {
            try {
                System.out.println(color + "Adding...." + num);
                synchronized (buffer){
                    buffer.add(num);
                }
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                System.out.println("Producer was interrupted.");
            }
        }

        System.out.println("Adding EOF and exiting....");
        synchronized (buffer){
            buffer.add("EOF");
        }

    }
}

class MyConsumer implements Runnable{
    private List<String> buffer;
    private String color;

    public MyConsumer(List<String> buffer, String color) {
        this.buffer = buffer;
        this.color = color;
    }

    public void run() {
        while (true){
            synchronized (buffer){
                if(buffer.isEmpty()){
                    continue;
                }
                if(buffer.get(0).equals("EOF")){
                    System.out.println(color + "Exiting...");
                    break;
                }else{
                    System.out.println(color + "Removed " + buffer.remove(0));
                }
            }

        }
    }
}








/*
We had seen that how easy it is for threads to experience race conditions when multiple threads are running or thread interference.
So to help developers write codes that involves multiple threads, developers had introduced java.util.concurrent package along with several other
sub packages. Now the classes and interfaces in these packages tree are there tp help developers to properly synchronize code
and also make it easier to work with multiple threads as well.

Thread Safe: There are synchronised methods or blocks.

Now since ArrayList isn't thread safe that's why we are using synchronised block with Instance variable of ArrayList as parameter
We are using synchronized block where we are updating the values of ArrayList because if multiple threads are trying to
update the contents of ArrayList and since the ArrayList is not thread safe, the integrity of ArrayList will be compromised
and there will be data inconsistency.

DRAWBACKS OF SYNCHRONIZATION
1. Threads that are blocked waiting to execute synchronized block can't be interrupted. Once they are blocked, they stuck there
until they get lock for the object the code is synchronizing on.

2. Synchronized block must be within same methods. In other words we can't start a synchronized block in one method and end
in other methods.

3. We can't test to see if an object's intrinsic lock is available or find out any other information about that lock.
Also the lock is not available we can't time out after we waited for lock for a while. When we reach the beginning of
synchronized block, we can either get the lock and start continuing execution or block at that line of code until we get
lock.

4. If multiple threads are waiting to get the lock, it's not FCFS there isn't a set order in which the jvm will choose the
next thread which will get the lock. So the first that is blocked could be the last thread to get the lock and vice versa.


So, to overcome these drawbacks and to remove thread interference, we can use Classes in java.util.concurrent package.
see code in concurrentPackageAndThreadInterface.reentrantLockAndUnlock package's Main class.
 */