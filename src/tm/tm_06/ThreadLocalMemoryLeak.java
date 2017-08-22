package tm.tm_06;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 模拟 ThreadLocal 可能导致的业务异常
 *
 */
public class ThreadLocalMemoryLeak {

    private static ExecutorService es = Executors.newFixedThreadPool(5);

    private static ThreadLocal<Date> date = ThreadLocal.withInitial(Date::new);

    public static void main(String[] args) throws InterruptedException {

        // 模拟 10 个用户进行登录，1 秒 1 个，输出登录时间
        for (int i = 0; i < 10; i++) {
            login();
            TimeUnit.SECONDS.sleep(1);
        }

        es.shutdownNow();
    }

    /**
     * 输出用户登录时间
     */
    private static void login() {
        es.submit(() -> {
            System.out.println(date.get());

            // 这里必须进行清除
//                date.remove();
        });

    }

}
