package livelock;

public class Main {

    public static void main(String[] args) {
        Worker worker1 = new Worker("Worker 1", true);
        Worker worker2 = new Worker("Worker 2", true);
        SharedResource sharedResource = new SharedResource(worker1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                worker1.work(sharedResource, worker2);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                worker2.work(sharedResource, worker1);
            }
        });
    }

}
/*
A live lock is similar to deadlock with a difference that threads that are live lock are in active state. In other words,
suppose a thread A is executing and continuing its for loop and waiting for thread B to complete its execution so that
it can stop executing its for loop. At the same time thread B is executing and continuing its for loop and waiting for
thread A to complete its execution so that it can stop executing its for loop. In this scenario, both threads are doing
work that means they are in active state but they are dependant on each other and are blocked. This is live lock.
 */