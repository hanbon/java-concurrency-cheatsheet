package tm.tm_06;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 线程中局部变量的使用
 * 观察 UnsafeTask 的输出是否前后一致
 *
 */
public class Main {

    public static void main(String[] args) {

        UnsafeTask unsafeTask = new UnsafeTask();
        SafeTask safeTask = new SafeTask();

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            Thread unsafeTaskThread = new Thread(unsafeTask);
            Thread safeTaskThread = new Thread(safeTask);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            unsafeTaskThread.start();
            safeTaskThread.start();
        }

    }

}

class UnsafeTask implements Runnable {

    // 所有线程共享
    private Date startDate;

    @Override
    public void run() {
        startDate = new Date();
        System.out.printf("UnsafeTask: Starting Thread: %s : %s\n", Thread.currentThread().getId(), startDate);
        try {
            TimeUnit.SECONDS.sleep((int) Math.rint(Math.random() * 10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("UnsafeTask: Thread Finished: %s : %s\n", Thread.currentThread().getId(), startDate);
    }
}

class SafeTask implements Runnable {

    // 每个线程私有
    private static ThreadLocal<Date> startDate = ThreadLocal.withInitial(Date::new);

    //
//    private static InheritableThreadLocal<Date> startDate2 = new InheritableThreadLocal<>() {
//        protected Date initialValue() {
//            return new Date();
//        }
//    };

    @Override
    public void run() {
        System.out.printf("  SafeTask: Starting Thread: %s : %s\n", Thread.currentThread().getId(), startDate.get());
        try {
            TimeUnit.SECONDS.sleep((int) Math.rint(Math.random() * 10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("  SafeTask: Thread Finished: %s : %s\n", Thread.currentThread().getId(), startDate.get());
    }
}