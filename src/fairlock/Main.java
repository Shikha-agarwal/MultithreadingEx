package fairlock;

import starvation.ThreadColor;

import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static ReentrantLock lock = new ReentrantLock(true);
    //This will create fair lock. By fair lock we means that ordering of the execution of threads will be on FCFS basis.
    //By FCFS we means the the order in which the processor is assigned to the threads will be repeated every time
    //and not the order of creation of thread. All threads will get fair chance of execution and there will be no deadlock
    //starvation.
    //Remember that by fair lock we means that Thread Scheduling that is done for all threads at first time will be repeated
    //every time. It will appear as like some for loop is executing.

    public static void main(String[] args) {
        Thread t1 = new Thread(new Worker(ThreadColor.ANSI_RED), "Priority 10");
        Thread t2 = new Thread(new Worker(ThreadColor.ANSI_BLUE), "Priority 8");
        Thread t3 = new Thread(new Worker(ThreadColor.ANSI_GREEN), "Priority 6");
        Thread t4 = new Thread(new Worker(ThreadColor.ANSI_CYAN), "Priority 4");
        Thread t5 = new Thread(new Worker(ThreadColor.ANSI_PURPLE), "Priority 2");

//        t1.setPriority(10);
//        t2.setPriority(8);
//        t3.setPriority(6);
//        t4.setPriority(4);
//        t5.setPriority(2);

        t3.start();
        t2.start();
        t5.start();
        t4.start();
        t1.start();

    }

    private static class Worker implements Runnable {
        private int runCount = 1;
        private String threadColor;

        public Worker(String threadColor) {
            this.threadColor = threadColor;
        }

        @Override
        public void run() {
            for(int i=0; i<100; i++) {
                lock.lock();
                try {
                    System.out.format(threadColor + "%s: runCount = %d\n", Thread.currentThread().getName(), runCount++);
                    // execute critial section of code
                }finally {
                    lock.unlock();
                }

            }
        }
    }
}
