package tm.tm_05;

/**
 * 线程中未受检异常的处理
 *
 */
public class Main {

    public static void main(String[] args) {

        Thread thread = new Thread(() -> {
            // 会抛出异常
            int number = Integer.parseInt("TTT");
            // 永远不会执行到这
            System.out.printf("Number: %d ", number);
        });

        // 设置未受检异常的 Handler
        thread.setUncaughtExceptionHandler(new ExceptionHandler());

        // 全局设置
//        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Thread has finished\n");

    }

}

class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.printf("An exception has been captured\n");
        System.out.printf("Thread: %s\n", t.getId());
        System.out.printf("Exception: %s: %s\n", e.getClass().getName(), e.getMessage());
        System.out.printf("Stack Trace: \n");
        e.printStackTrace(System.out);
        System.out.printf("Thread status: %s\n", t.getState());
    }

}