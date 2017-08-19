package tm.tm_04;

import java.util.Date;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

/**
 * 守护进程的创建和运行
 *
 * 用户线程：WriterTask 每秒写入一个事件到 Deque
 * 守护线程：CleanerTask 清除 2s 前加入到 Deque 的事件
 *
 */
public class Main {

    public static void main(String[] args) {

        // 用来存储事件
        Deque<Event> deque = new ConcurrentLinkedDeque<>();

        // 创建可用 cpu 个 WriterTask 线程
        WriterTask writer = new WriterTask(deque);
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            Thread thread = new Thread(writer);
            thread.start();
        }

        // 创建一个 CleanerTask 守护进程
        CleanerTask cleaner = new CleanerTask(deque);
        cleaner.start();

    }

}

class Event {

    private Date date;
    private String event;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}

class WriterTask implements Runnable {

    // 用来存储事件的 Deque
    Deque<Event> deque;


    public WriterTask(Deque<Event> deque) {
        this.deque = deque;
    }

    @Override
    public void run() {

        // 写 10 个事件
        for (int i = 1; i < 10; i++) {

            Event event = new Event();
            event.setDate(new Date());
            event.setEvent(String.format("The thread %s has generated the event %d event",
                            Thread.currentThread().getId(),
                            i));

            // 加入到存储结构中
            deque.addFirst(event);

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class CleanerTask extends Thread {

    private Deque<Event> deque;

    public CleanerTask(Deque<Event> deque) {
        this.deque = deque;
        // 设置线程为守护线程
        setDaemon(true);
    }


    @Override
    public void run() {
        while (true) {
            Date date = new Date();
            clean(date);
        }
    }

    private void clean(Date date) {
        long difference;
        boolean delete;
        if (deque.size() == 0) {
            return;
        }

        delete = false;
        do {
            Event e = deque.getLast();
            difference = date.getTime() - e.getDate().getTime();

            // 清除 2s 前加入的事件
            if (difference > 2000) {
                System.out.printf("Cleaner: %s\n", e.getEvent());
                deque.removeLast();
                delete = true;
            }
        } while (difference > 2000);

        if (delete) {
            System.out.printf("Cleaner: Size of the queue: %d\n", deque.size());
        }
    }
}