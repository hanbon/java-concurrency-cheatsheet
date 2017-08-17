package tm.tm_02;

import java.util.concurrent.TimeUnit;

/**
 * 这个类用来生成素数直到其被中断
 *
 */
public class PrimeGenerator implements Runnable {

    @Override
    public void run() {
        long number = 1L;

        while (true) {

            if (isPrime(number)) {
                System.out.printf("Number %d is Prime\n", number);
            }
            number++;

            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                // 当抛出 InterruptedException 异常的时候，中断状态已被清除

                // 为了显示，打印出信息
                System.out.println("Exception in generator is: " + e.getMessage());

                // 重新设置中断状态，方便下面的代码段响应中断信息
                Thread.currentThread().interrupt();
            }

            // 把这段代码注释了线程就不能响应中断信息了，试下吧
            if (Thread.currentThread().isInterrupted()) {
                System.out.printf("The Prime Generator has been Interrupted\n");
                return;
            }

        }
    }

    private boolean isPrime(long number) {
        if (number <= 2) {
            return true;
        }
        for (long i = 2; i <= Math.sqrt(number); i++) {
            if ((number % i) == 0) {
                return false;
            }
        }
        return true;
    }

}
