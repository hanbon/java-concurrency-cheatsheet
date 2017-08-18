package tm.tm_03;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 等待线程结束与谦让
 * 试一试取消注释 Thread.yield();
 */
public class Main {

    public static void main(String[] args) {

        Thread worker1 = new Thread(() -> {
            System.out.println("Worker1 started!");

            for (int i = 0; i < 5; i++) {
                System.out.println("Worker1 says:" + i);
//                Thread.yield();
            }

            try {
                TimeUnit.SECONDS.sleep(new Random().nextInt(5));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Worker1 finished!");

        });

        Thread worker2 = new Thread(() -> {
            System.out.println("Worker2 started!");

            for (int i = 0; i < 5; i++) {
                System.out.println("Worker2 says:" + i);
//                Thread.yield();
            }

            try {
                TimeUnit.SECONDS.sleep(new Random().nextInt(5));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Worker2 finished!");

        });

        worker1.setPriority(Thread.MAX_PRIORITY);
        worker2.setPriority(Thread.MIN_PRIORITY);

        worker1.start();
        worker2.start();

        try {
            // 等待 1 s
            worker1.join(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            // 一直等待
            worker2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Main: finished!");
    }

}
