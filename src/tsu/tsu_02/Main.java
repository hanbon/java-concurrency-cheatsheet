package tsu.tsu_02;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport 测试
 *
 */
public class Main {

    private static Object u = new Object();
    private static MyThread t1 = new MyThread("t1");
    private static MyThread t2 = new MyThread("t2");

    public static class MyThread extends Thread {

        public MyThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            synchronized (u) {
                System.out.printf("%s in %s\n", new Date(), getName());
                LockSupport.park(u);
                if (isInterrupted()) {
                    System.out.println(getName() + " is interrupted.");
                }
            }

            // 最多阻塞 5 s
            // 如果中断位为 true，则下面的语句无效，可以对比上面如果是使用 Thread.interrupted() 方法判断有什么不同
            LockSupport.parkNanos(this, TimeUnit.SECONDS.toNanos(5));

            System.out.printf("%s %s ends\n", new Date(), getName());
        }
    }

    public static void main(String[] args) throws InterruptedException {

        t1.start();

        TimeUnit.SECONDS.sleep(1);

        System.out.println("t1 blocker is: " + LockSupport.getBlocker(t1));

        t2.start();

        // 中断可以使 park 返回
        t1.interrupt();

        System.out.println("t1 blocker is: " + LockSupport.getBlocker(t1));

        // unpark 可以使 park 返回
        LockSupport.unpark(t2);

        TimeUnit.SECONDS.sleep(1);
        System.out.println("t2 blocker is: " + LockSupport.getBlocker(t2));

        t1.join();
        t2.join();
    }

}
