package tm.tm_02;

import java.util.concurrent.TimeUnit;

/**
 * 线程的中断
 * 运行 1 s 后通过中断机制强制使其终止
 *
 */
public class Main {

    public static void main(String[] args) {

        Thread task = new Thread(new PrimeGenerator());
        task.start();

        // 等待 1 s
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 中断素数生成器
        task.interrupt();

        // 试试看把这段代码注释会怎样
        // task 可能会有哪些状态？为什么？
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 输出线程的一些信息
        System.out.printf("Main: Status of the Thread: %s\n", task.getState());
        System.out.printf("Main: isInterrupted: %s\n", task.isInterrupted());
        System.out.printf("Main: isAlive: %s\n", task.isAlive());
    }

}
