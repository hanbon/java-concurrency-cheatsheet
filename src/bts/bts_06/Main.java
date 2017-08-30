package bts.bts_06;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

/**
 *
 *
 */
public class Main {

    public static void main(String[] args) {

        Position position = new Position();
        StampedLock lock = new StampedLock();

        Thread threadWriter = new Thread(new Writer(position, lock));
        Thread threadReader = new Thread(new Reader(position, lock));
        Thread threadOptReader = new Thread(new OptimisticReader(position, lock));

        threadWriter.start();
        threadReader.start();
        threadOptReader.start();

        try {
            threadWriter.join();
            threadReader.join();
            threadOptReader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}

class Position {

    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}

class Writer implements Runnable {

    private final Position position;
    private final StampedLock lock;

    public Writer(Position position, StampedLock lock) {
        this.position = position;
        this.lock = lock;
    }

    @Override
    public void run() {

        for (int i = 0; i < 10; i++) {
            long stamp = lock.writeLock();

            try {
                System.out.printf("Writer: Lock acquired stamp %d\n", stamp);
                position.setX(position.getX() + 1);
                position.setY(position.getY() + 1);
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlockWrite(stamp);
                System.out.printf("Writer: Lock released stamp %d\n", stamp);
            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}

class Reader implements Runnable {

    private final Position position;
    private final StampedLock lock;

    public Reader(Position position, StampedLock lock) {
        this.position = position;
        this.lock = lock;
    }

    @Override
    public void run() {
        for (int i = 0; i < 50; i++) {
            long stamp = lock.readLock();
            try {
                System.out.printf("Reader get stamp: %d - (%d,%d)\n", stamp, position.getX(), position.getY());
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlockRead(stamp);
                System.out.printf("Reader get stamp: %d - Lock released\n", stamp);
            }
        }
    }

}

class OptimisticReader implements Runnable {

    private final Position position;
    private final StampedLock lock;

    public OptimisticReader(Position position, StampedLock lock) {
        this.position = position;
        this.lock = lock;
    }

    @Override
    public void run() {
        long stamp;
        for (int i = 0; i < 100; i++) {
            try {
                stamp = lock.tryOptimisticRead();
                int x = position.getX();
                int y = position.getY();
                if (lock.validate(stamp)) {
                    System.out.printf("OptmisticReader get stamp: %d - (%d,%d)\n", stamp, x, y);
                } else {
                    System.out.printf("OptmisticReader get stamp: %d - Not Free\n", stamp);
                }
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
