package bts.bts_06;

import java.util.concurrent.locks.StampedLock;

/**
 * JDK 中的 StampedLock 使用示例
 */
public class Point {
    private double x, y;
    private final StampedLock sl = new StampedLock();

    // 排它锁-写锁（writeLock）
    void move(double deltaX, double deltaY) {
        long stamp = sl.writeLock();
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            sl.unlockWrite(stamp);
        }
    }

    //下面看看乐观读锁案例
    double distanceFromOrigin() {
        long stamp = sl.tryOptimisticRead(); //获得一个乐观读锁
        double currentX = x, currentY = y; //将两个字段读入方法体栈
        if (!sl.validate(stamp)) { //检查发出乐观读锁后同时是否有其他写锁发生？
            stamp = sl.readLock(); //如果没有，再次获得一个读悲观锁
            try {
                currentX = x; // 将两个字段读入方法体栈
                currentY = y; // 将两个字段读入方法体栈
            } finally {
                sl.unlockRead(stamp);
            }
        }
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }

    //使用悲观锁获取读锁，并尝试转换为写锁
    void moveIfAtOrigin(double newX, double newY) {
        // 这里可以使用乐观读锁替换
        long stamp = sl.readLock();
        try {
            while (x == 0.0 && y == 0.0) { //循环，检查当前状态是否符合
                long ws = sl.tryConvertToWriteLock(stamp); //将读锁转为写锁
                if (ws != 0L) { //这是确认转为写锁是否成功
                    stamp = ws; //如果成功 替换票据
                    x = newX; //进行状态改变
                    y = newY; //进行状态改变
                    break;
                } else { //如果不能成功转换为写锁
                    sl.unlockRead(stamp); //显式释放读锁
                    stamp = sl.writeLock(); //显式直接进行写锁 然后再通过循环再试
                }
            }
        } finally {
            sl.unlock(stamp); //释放读锁或写锁
        }
    }
}