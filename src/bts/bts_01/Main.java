package bts.bts_01;

import java.util.concurrent.TimeUnit;

/**
 * 使用 synchronized 实现同步方法
 * Sensor: 用来模拟停车场的状况
 * ParkingCash: 用来停车计费的线程安全的类
 * ParkingStats: 提供停汽车、摩托车的方法的抽象类
 * SafeParkingStats: ParkingStats 的线程安全版本的实现
 * UnsafeParkingStats: ParkingStats 的非线程安全版本
 */
public class Main {

    public static void main(String[] args) {

        ParkingCash cash = new ParkingCash();

        // 线程安全的版本
//        ParkingStats stats = new SafeParkingStats(cash);
        // 非线程安全的版本
        ParkingStats stats = new UnsafeParkingStats(cash);

        System.out.printf("Parking Simulator\n");
        int numberSensors = 10;
        Thread threads[] = new Thread[numberSensors];
        for (int i = 0; i < numberSensors; i++) {
            Thread thread = new Thread(new Sensor(stats));
            thread.start();
            threads[i] = thread;
        }

        for (int i = 0; i < numberSensors; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("Number of cars: %d\n", stats.getNumberCars());
        System.out.printf("Number of motorcycles: %d\n", stats.getNumberMotorcycles());
        cash.close();

        assert stats.getNumberCars() == 0;
        assert stats.getNumberMotorcycles() == 0;

    }

}

class Sensor implements Runnable {

    private ParkingStats stats;

    public Sensor(ParkingStats stats) {
        this.stats = stats;
    }

    @Override
    public void run() {
        // 模拟车的来往状态，最终的结果应该是没有车留下
        for (int i = 0; i < 10; i++) {
            stats.carComeIn();
            stats.carComeIn();
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stats.motoComeIn();
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stats.motoGoOut();
            stats.carGoOut();
            stats.carGoOut();
        }
    }
}

/**
 * 线程安全
 */
class ParkingCash {

    private static final int cost = 2;

    private long cash;

    public ParkingCash() {
        cash = 0;
    }

    // 可以把这里的 synchronized 去掉试试结果
    public synchronized void vehiclePay() {
        cash += cost;
    }

    public void close() {
        System.out.printf("Closing accounting");
        System.out.printf("The total amount is : %d", cash);
    }
}

abstract class ParkingStats {
    /**
     * 分别用来保存汽车和摩托的数量
     */
    protected long numberCars;
    protected long numberMotorcycles;

    public abstract void carComeIn();

    public abstract void carGoOut();

    public abstract void motoComeIn();

    public abstract void motoGoOut();

    public abstract long getNumberCars();

    public abstract long getNumberMotorcycles();

}

/**
 * 线程安全的类
 */
class SafeParkingStats extends ParkingStats {


    /**
     * 分别用来同步 numberCars 和 numberMotorcycles 的对象
     */
    private final Object controlCars, controlMotorcycles;

    private ParkingCash cash;

    public SafeParkingStats(ParkingCash cash) {
        numberCars = 0;
        numberMotorcycles = 0;
        controlCars = new Object();
        controlMotorcycles = new Object();
        this.cash = cash;
    }


    public void carComeIn() {
        synchronized (controlCars) {
            numberCars++;
        }
    }

    public void carGoOut() {
        synchronized (controlCars) {
            numberCars--;
        }
        cash.vehiclePay();
    }

    public void motoComeIn() {
        synchronized (controlMotorcycles) {
            numberMotorcycles++;
        }
    }

    public void motoGoOut() {
        synchronized (controlMotorcycles) {
            numberMotorcycles--;
        }
        cash.vehiclePay();
    }

    public long getNumberCars() {
        synchronized (controlCars) {
            return numberCars;
        }
    }

    public long getNumberMotorcycles() {
        synchronized (controlMotorcycles) {
            return numberMotorcycles;
        }
    }

}

/**
 * 非线程安全的类
 */
class UnsafeParkingStats extends ParkingStats {

    private ParkingCash cash;

    public UnsafeParkingStats(ParkingCash cash) {
        numberCars = 0;
        numberMotorcycles = 0;
        this.cash = cash;
    }


    public void carComeIn() {
        numberCars++;
    }

    public void carGoOut() {
        numberCars--;
        cash.vehiclePay();
    }

    public void motoComeIn() {
        numberMotorcycles++;
    }

    public void motoGoOut() {
        numberMotorcycles--;
        cash.vehiclePay();
    }

    public long getNumberCars() {
        return numberCars;
    }

    public long getNumberMotorcycles() {
        return numberMotorcycles;
    }

}
