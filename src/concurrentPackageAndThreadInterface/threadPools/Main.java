package concurrentPackageAndThreadInterface.threadPools;

import concurrentPackageAndThreadInterface.producerConsumer.ThreadColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        List<String> buffer = new ArrayList<>();
        ReentrantLock bufferLock = new ReentrantLock();

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        MyProducer producer = new MyProducer(buffer, ThreadColor.ANSI_PURPLE, bufferLock);
        MyConsumer consumer1 = new MyConsumer(buffer, ThreadColor.ANSI_BLUE, bufferLock);
        MyConsumer consumer2 = new MyConsumer(buffer, ThreadColor.ANSI_RED, bufferLock);

//        new Thread(producer).start();
//        new Thread(consumer1).start();
//        new Thread(consumer2).start();
        executorService.execute(producer);
        executorService.execute(consumer1);
        executorService.execute(consumer2);

        Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println(ThreadColor.ANSI_GREEN + "I'm being printed from Callable class.");
                return "This is Callable result";
            }
        });

        //Output will be at the end if Line 16 code sets to 3 threads because all the threads will be in active state at
        //that time. But if we make 3 to 5 then output will be somewhere in between.
        try {
            System.out.println(future.get());
        }catch (ExecutionException e){
            System.out.println("Something went wrong");
        }catch (InterruptedException e){
            System.out.println("Interrupted");
        }

        executorService.shutdown();
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
                try {
                    buffer.add(num);
                } finally {
                    bufferLock.unlock();
                }
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                System.out.println("Producer was interrupted.");
            }
        }

        System.out.println("Adding EOF and exiting....");
        bufferLock.lock();
        try {
            buffer.add("EOF");
        } finally {
            bufferLock.unlock();
        }
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
            try {
                if (buffer.isEmpty()) {
                    continue;
                }
                if (buffer.get(0).equals("EOF")) {
                    System.out.println(color + "Exiting...");
                    break;
                } else {
                    System.out.println(color + "Removed " + buffer.remove(0));
                }
            } finally {
                bufferLock.unlock();
            }
        }
    }
}
/*
* Executive Service Interface
* This interface is present in java.util.concurrent package and this manages threads for us so that we don't have to explicitly
* create and start threads. Now we have to still provide runnable object to the service because we have to code the task
* we want to execute on background threads, but we dont manage threads directly as such. The ExecutiveService implementation
* allow us to focus on code we want to run without the fuss of managing threads and their life cycles.
* We create an implementation of ExecutiveService and give it a task we want to run without worrying about details how that
* task will run.
* So ExecutiveService implementations make use of Thread Polls. Thread Polls is managed sets of threads. It reduces the
* overhead of Thread creation espically in applications that uses large number of threads. Thread poll may also limit the
* number of threads that are active running a block at any particular time.
* We can also make our own thread poll by making a class which implements one of thread poll interfaces.
* Now since thread poll can limit the number of active threads, it is possible when we ask service to run a task, it won't
* be able to run straight away. Ex. if we limit the threads to 20, there may already be 20 active threads when we submit
* a task. In that case, the task will have to wait in service queue until one of threads terminates.
*
* ExecutiveService interface has Executive interface which has only 1 method : execute() which is replacement of
* new Thread(Runnable r).start();
*
* We have to explicitly shut down the executive service to stop program which is not so in new Thread(Runnable r).start();
*
* Now if we want to return something from thread running in background we can use submit() method. This method accepts
* Callable object which is very similar to Runnable object except that it can return a value. That value returned should
* be an object of type Future.
* */








