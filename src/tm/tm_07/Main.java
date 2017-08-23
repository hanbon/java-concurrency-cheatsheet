package tm.tm_07;

import java.util.Random;

/**
 * 线程的分组和未受检异常的处理
 *
 */
public class Main {

    public static void main(String[] args) {

        int numberOfThreads = 2 * Runtime.getRuntime().availableProcessors();
        MyThreadGroup threadGroup = new MyThreadGroup("MyThreadGroup");
        Task task = new Task();

        // 创建线程并关联到线程组
        for (int i = 0; i < numberOfThreads; i++) {
            Thread t = new Thread(threadGroup, task);
            t.start();
        }

        // 输出线程组的信息
        System.out.printf("Number of Threads: %d\n", threadGroup.activeCount());
        System.out.printf("Information about the Thread Group\n");
        threadGroup.list();

        // 输出线程组中线程对象的信息
        Thread[] threads = new Thread[threadGroup.activeCount()];
        threadGroup.enumerate(threads);
        for (int i = 0; i < threadGroup.activeCount(); i++) {
            System.out.printf("Thread %s: %s\n", threads[i].getName(), threads[i].getState());
        }
    }

}

class MyThreadGroup extends ThreadGroup {

    public MyThreadGroup(String name) {
        super(name);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // 输出当前线程的ID
        System.out.printf("The thread %s has thrown an Exception\n", t.getId());
        // 堆栈信息
        e.printStackTrace(System.out);
        // 中断线程组中所有的线程
        System.out.printf("Terminating the rest of the Threads\n");
        interrupt();
    }
}

class Task implements Runnable {

    @Override
    public void run() {

        int result;
        Random random = new Random(Thread.currentThread().getId());

        while (true) {
            // 当产生 0 时会产生 ArithmeticException
            result = 1000 / random.nextInt(1000000000);

            // 检查当前线程是否被中断
            if (Thread.currentThread().isInterrupted()) {
                System.out.printf("%d : Interrupted\n", Thread.currentThread().getId());
                return;
            }
        }
    }
}