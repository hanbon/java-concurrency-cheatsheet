package tm.tm_01;

import java.lang.Thread.State;

/**
 * 线程的创建、运行与信息的获取
 *
 */
public class Main {

    public static void main(String[] args) {

        // 线程优先级信息
        System.out.printf("Minimum Priority: %s\n", Thread.MIN_PRIORITY);
        System.out.printf("Normal Priority: %s\n", Thread.NORM_PRIORITY);
        System.out.printf("Maximum Priority: %s\n", Thread.MAX_PRIORITY);

        Thread threads[];
        Thread.State status[];

        // 开 10 个线程，5 个最高优先级，5 个最低优先级
        threads = new Thread[10];
        status = new Thread.State[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new Calculator());
            if ((i % 2) == 0) {
                threads[i].setPriority(Thread.MAX_PRIORITY);
            } else {
                threads[i].setPriority(Thread.MIN_PRIORITY);
            }
            threads[i].setName("My Thread " + i);
        }

        // 记录线程的状态
        for (int i = 0; i < 10; i++) {
            System.out.println("Main : Status of Thread " + i + " : " + threads[i].getState());
            status[i] = threads[i].getState();
        }

        // 开始线程
        for (int i = 0; i < 10; i++) {
            threads[i].start();
        }

        // 当线程状态改变，输出相应信息
        boolean finish = false;
        while (!finish) {
            for (int i = 0; i < 10; i++) {
                if (threads[i].getState() != status[i]) {
                    writeThreadInfo(threads[i], status[i]);
                    status[i] = threads[i].getState();
                }
            }

            finish = true;
            for (int i = 0; i < 10; i++) {
                // 当所有线程都终止
                finish = finish && (threads[i].getState() == State.TERMINATED);
            }
        }

    }

    /**
     * 输出线程的信息
     */
    private static void writeThreadInfo(Thread thread, State state) {
        System.out.printf("Main : Id %d - %s\n", thread.getId(), thread.getName());
        System.out.printf("Main : Priority: %d\n", thread.getPriority());
        System.out.printf("Main : Old State: %s\n", state);
        System.out.printf("Main : New State: %s\n", thread.getState());
        System.out.printf("Main : ************************************\n");
    }

}
