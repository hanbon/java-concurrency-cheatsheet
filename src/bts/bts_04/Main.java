package bts.bts_04;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用 Condition 实现一个生产者，多个消费者的问题
 *
 */
public class Main {

    // 可以测试不同的消费者数量对整体等待次数的影响
    private static final int CONSUMER_NUM = 5;

    public static void main(String[] args) throws InterruptedException {

        FileMock mock = new FileMock(100, 10);

        // 最多放入 20 行
        Buffer buffer = new Buffer(20);


        Producer producer = new Producer(mock, buffer);
        Thread producerThread = new Thread(producer, "Producer");


        Consumer consumers[] = new Consumer[CONSUMER_NUM];
        Thread consumersThreads[] = new Thread[CONSUMER_NUM];

        for (int i = 0; i < CONSUMER_NUM; i++) {
            consumers[i] = new Consumer(buffer);
            consumersThreads[i] = new Thread(consumers[i], "Consumer_" + i);
        }


        producerThread.start();
        for (int i = 0; i < CONSUMER_NUM; i++) {
            consumersThreads[i].start();
        }

        for (int i = 0; i < CONSUMER_NUM; i++) {
            consumersThreads[i].join();
        }

        System.out.println("Producer total waiting times: " + buffer.getProducerWaitingTimes());
        System.out.println("Consumer total waiting times: " + buffer.getConsumerTotalWaitingTimes());
    }

}

/**
 * 模拟一个文件
 */
class FileMock {

    // 文件内容
    private String[] content;
    // 记录处理过的行数
    private int index;

    /**
     * 模拟一个文件，填充随机内容
     * @param size 文件的行数
     * @param length 一行的长度
     */
    public FileMock(int size, int length) {
        content = new String[size];
        for (int i = 0; i < size; i++) {
            StringBuilder buffer = new StringBuilder(length);
            for (int j = 0; j < length; j++) {
                int randomCharacter = (int) (Math.random() * 27) + 65;
                buffer.append((char) randomCharacter);
            }
            content[i] = buffer.toString();
        }
        index = 0;
    }

    public boolean hasMoreLines() {
        return index < content.length;
    }

    public String getLine() {
        if (this.hasMoreLines()) {
            System.out.println("left line: " + (content.length - index));
            return content[index++];
        }
        return null;
    }

}

/**
 * 用来储存从文件读出的文件内容
 */
class Buffer {

    private final LinkedList<String> buffer;
    private final int maxSize;

    private final ReentrantLock lock;

    private final Condition lines;
    private final Condition space;

    // 标记是否还有新行
    private boolean pendingLines;

    private AtomicInteger producerWaitingTimes;
    private AtomicInteger consumerTotalWaitingTimes;


    public Buffer(int maxSize) {
        this.maxSize = maxSize;
        buffer = new LinkedList<>();
        lock = new ReentrantLock();
        lines = lock.newCondition();
        space = lock.newCondition();
        pendingLines = true;
        producerWaitingTimes = new AtomicInteger(0);
        consumerTotalWaitingTimes = new AtomicInteger(0);
    }


    public void insert(String line) {
        lock.lock();
        try {
            while (buffer.size() == maxSize) {
                System.out.println("No more place for producer to insert a line");
                producerWaitingTimes.getAndIncrement();
                space.await();
            }
            buffer.offer(line);
            System.out.printf("%s: insert a line: %s, buffer size is : %d\n", Thread.currentThread().getName(), line, buffer.size());
            lines.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public String get() {
        String line = null;
        lock.lock();
        try {
            while ((buffer.size() == 0) && (hasPendingLines())) {
                System.out.printf("%s waiting for a new line.\n", Thread.currentThread().getName());
                consumerTotalWaitingTimes.getAndIncrement();
                lines.await();
            }

            if (hasPendingLines()) {
                line = buffer.poll();
                System.out.printf("%s: read a line: %s, buffer size is: %d\n", Thread.currentThread().getName(), line, buffer.size());
                space.signalAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return line;
    }

    public synchronized void setPendingLines(boolean pendingLines) {
        this.pendingLines = pendingLines;
    }

    public synchronized boolean hasPendingLines() {
        return pendingLines || buffer.size() > 0;
    }

    public int getProducerWaitingTimes() {
        return producerWaitingTimes.get();
    }

    public int getConsumerTotalWaitingTimes() {
        return consumerTotalWaitingTimes.get();
    }
}

class Producer implements Runnable {

    private FileMock mock;
    private Buffer buffer;

    public Producer(FileMock mock, Buffer buffer) {
        this.mock = mock;
        this.buffer = buffer;
    }

    /**
     * 当模拟文件还有剩余的行时，读取一行并放到 Buffer 中
     */
    @Override
    public void run() {
        buffer.setPendingLines(true);
        while (mock.hasMoreLines()) {
            String line = mock.getLine();
            buffer.insert(line);
        }
        buffer.setPendingLines(false);
    }

}

class Consumer implements Runnable {

    private Buffer buffer;

    public Consumer(Buffer buffer) {
        this.buffer = buffer;
    }


    @Override
    public void run() {
        while (buffer.hasPendingLines()) {
            String line = buffer.get();
            processLine(line);
        }
    }

    private void processLine(String line) {
        try {
            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(5));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}