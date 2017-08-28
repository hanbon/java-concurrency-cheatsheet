package bts.bts_02;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 使用 synchronized 、 wait() 、 notify() 实现生产者消费者
 *
 */
public class Main {

    public static void main(String[] args) {

        // 存储事件
        EventStorage storage = new EventStorage();

        // 生产者线程
        Producer producer = new Producer(storage);
        Thread thread1 = new Thread(producer);

        // 消费者线程
        Consumer consumer = new Consumer(storage);
        Thread thread2 = new Thread(consumer);

        thread2.start();
        thread1.start();
    }

}

class EventStorage {

    private int maxSize;
    private Queue<Date> storage;

    public EventStorage() {
        maxSize = 10;
        storage = new LinkedList<>();
    }

    /**
     * 创建和存储事件
     */
    public synchronized void set() {
        while (storage.size() == maxSize) {
            System.out.println("producer  waiting...");
            try {
                // 等待消费
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        storage.add(new Date());
        System.out.printf("Set: %d\n", storage.size());
        // 唤醒消费者
        notify();
    }

    /**
     * 取出并删除一个事件
     */
    public synchronized void get() {
        while (storage.size() == 0) {
            System.out.println("consumer waiting...");
            try {
                // 等待生产
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String element = storage.poll().toString();
        System.out.printf("Get: %d: %s\n", storage.size(), element);
        // 唤醒生产者
        notify();
    }

}

class Producer implements Runnable {

    private EventStorage storage;

    public Producer(EventStorage storage) {
        this.storage = storage;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            storage.set();
        }
    }
}

class Consumer implements Runnable {

    private EventStorage storage;

    public Consumer(EventStorage storage) {
        this.storage = storage;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            storage.get();
        }
    }

}
