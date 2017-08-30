## 线程同步基础 (basic thread synchronization)

多个执行线程共享一个资源的情景，是最常见的并发编程情景之一。

在并发应用中常常遇到这样的场景：多个线程读/写相同的数据，或者访问相同的文件或数据库连接。
为了防止这些共享资源有可能出现的错误或数据不一致，我们必须实现一些机制来防止这些错误的发生。

为了解决这些问题，人们引入了临界区（Critical Section）概念。

临界区是一个用以访问共享资源的代码块，这个代码块在同一时间内只允许一个线程执行。

为了帮助编程人员实现这个临界区，Java（以及大多数编程语言）提供了同步机制。
当一个线程试图访问一个临界区时，它将使用一种同步机制来查看是不是已经有其他线程进入临界区。
如果没有其他线程进入临界区，它就可以进入临界区；如果已经有线程进入了临界区，他就被同步机制挂起，直到进入的线程离开这个临界区。
如果在等待进入临界区的线程不止一个，JVM 会选择其中一个，其余的将继续等待。

下面将讲解如何使用 Java 语言提供的两种基本同步机制：

- synchronized 关键字机制

- Lock 接口及其实现机制


### 目录导航

- [使用 synchronized 实现同步](#使用synchronized实现同步)

- [在同步代码中使用条件](#在同步代码中使用条件)

- [使用锁实现同步](#使用锁实现同步)

- [在锁中使用多条件](#在锁中使用多条件)

- [使用读写锁实现同步数据访问](#使用读写锁实现同步数据访问)

- [使用改进的读写锁 StampedLock 实现同步](#使用改进的读写锁StampedLock实现同步)


### [使用synchronized实现同步](bts_01/Parking.java "查看示例")

分类：

- 使用 synchronized 同步实例方法

- 使用 synchronized 同步静态方法

- 使用 synchronized 同步代码块

    - 同步单一对象锁，使用 `this` 指代正在执行方法所属的对象
    
    - 同步类锁，使用 `*.class`
    
    - 使用非属性依赖，即使用其他对象

每一个用 synchronized 关键字声明的方法都是临界区。在同一时间内只允许一个执行线程访问。

注意：如果一个线程访问同步实例方法，另一个线程访问同步静态方法，而这两个方法都改变了相同的数据，可能会出现数据不一致的错误。（[示例](bts_01/SyncMethods.java)）

synchronized 是可重入的。即当一个线程在访问一个对象的同步方法块时，它还可以调用这个对象的其他同步方法，也包含正在执行的方法，而不必再次获取这个方法的访问权。

通过使用 synchronized 保护代码块（而不是整个方法），以获得更好的性能。


### [在同步代码中使用条件](bts_02/Main.java "查看示例")

在并发编程中一个典型的问题是生产者-消费者（Producer-Consumer）问题。
有一个数据缓冲区，一个或多个数据生产者将把数据放入这个缓冲区，一个或多个消费者将数据从缓冲区取走。

这个缓冲区是一个共享数据结构，必须使用同步机制控制对它的访问。例如使用 synchronized 关键字，但是会受到更多的限制。
如果缓冲区满，则生产者就不能再放入数据；如果缓冲区空，消费者就不能读取数据。

对于这些场景，Java 在 Object 类中提供了 `wait()`、`notify()`、`notifyAll()` 方法。
线程可以在同步代码块中调用 `wait()` 方法。如果在同步代码块之外调用 `wait()` 方法， JVM 会抛出 IllegalMonitorStateException 异常。
当一个线程调用 `wait()` 方法，JVM 将这个线程休眠，并且释放控制这个同步代码块的对象，同时允许其他线程执行这个对象控制的其他同步代码块。
为了唤醒这个线程，必须在这对象控制的某个同步代码块中调用 `notify()` 或 `notifyAll()` 方法。

使用应该保持用 wait 循环模式来调用 wait 方法，永远不要在循环之外调用 `wait()` 方法。

```
synchronized(obj) {
    while(<condition does not hold>) {
        obj.wait()
    }
}
```

循环可以在等待之前和之后测试条件。

 - 在等待之前测试条件，当条件已经成立时就跳过等待，这对于确保活性（liveness）是必要的。
 如果条件已经成立，并且在线程等待之前，`notify()` 或 `notifyAll()` 方法已经被调用，则无法保证该线程将会从等待中苏醒过来。
 
 - 在等待之后测试条件，如果条件不成立的话继续等待，这对于确保安全性（safety）是必要的。
 当条件不成立的时候，如果线程继续执行，则可能会破坏被锁保护的约束关系。
 
一个相关的话题是，为了唤醒正在等待的线程，应该使用 `notify()` 还是 `notifyAll()`。
一个常见的说法是，总是应该使用 `notifyAll()` 。这是合理而保守的建议。它总会产生正确的结果，因为它可以保证唤醒所有需要被唤醒的线程。
可能会唤醒其他一些线程，但是不会影响程序的正确性。这些线程醒来后，会检查它们正在等待的条件，如果发现条件并不满足，就会继续等待。

最后一点，并发工具优于 wait 和 notify。


### [使用锁实现同步](bts_03/Main.java "查看示例")

Java 提供了同步代码块的另一种机制，一种比 synchronized 关键字更强大和灵活的机制。

这种机制基于 Lock 接口（位于 `java.util.concurrent.locks` 包下）及其实现类（例如：ReentrantLock）。这种机制提供了以下的好处：

- Lock 接口支持更灵活的同步代码块结构。使用 synchronized 时，只能在同一个结构化的方式下来获取和释放控制同步代码块。Lock 接口运行实现更复杂的临界区结构。

- Lock 接口接口在 synchronized 关键字之上提供了额外的特性。其中一个特性是 `tryLock()` 方法的实现，这个方法尝试获取锁的控制权，如果因为其它线程的使用而未果，返回 false。
使用 synchronized 关键字，当一个线程(A)尝试去执行 synchronized 代码块，如果有另一个线程(B)执行它，线程(A)会被挂起直到线程(B)执行完同步代码块。
而使用锁，则可以使用 `tryLock()` 方法。这个方法返回一个boolean值表明是否有另一个线程正在执行被这个锁保护的代码块。

- ReadWriteLock 接口允许分离读写操作，支持多个读线程，一个写线程的模式。

- Lock 接口提供了比 synchronized 关键字更好的性能。

这节重点关注 ReentrantLock：

- ReentrantLock 允许递归调用，如其名，重入锁，这样的最大次数限制是 Integer.MAX_VALUE。

- ReentrantLock 的构造器有一个布尔参数 fair。表征了是否是公平锁，默认为非公平锁。主要是针对 `lock()` 方法。

    - 公平锁：线程获取锁的顺序和调用 lock 的顺序一样，FIFO。需要维持有序队列，需要增加阻塞和唤醒的时间开销。不会产生饥饿，但性能相对低下。
    
    - 非公平锁：线程获取锁的顺序和调用 lock 的顺序无关，性能更高。

注意事项:

- 如果在临界区使用了 try-catch 块，不要忘了将 `unlock()` 方法放入 finally 部分。

- 如果 `tryLock()` 方法返回了 false, 程序还错误的执行了临界区代码，很可能会出现错误的结果。

- 即便是公平锁，如果通过不带超时时间限制的 `tryLock()` 的方式获取锁的话，它也是不公平的。但是带有超时时间限制的 `tryLock(long timeout, TimeUnit unit)` 方法则不一样，还是会遵循公平或非公平的原则

重要方法：

- `lock()`：获得锁，如果锁被占用，进入等待。
- `lockInterruptibly()`：获得锁，但优先响应中断。
- `tryLock()`：尝试获得锁，如果成功，立即放回 true，反之失败返回 false。该方法不会进行等待，立即返回。
- `tryLock(long time, TimeUnit unit)`：在给定的时间内尝试获得锁。
- `unlock()`：释放锁。


### [在锁中使用多条件](bts_04/Main.java "查看示例")

一个锁可能关联一个或多个条件，这些条件通过 Condition 接口声明。

目的是允许线程获取锁并且查看等待的某一个条件是否满足，如果不满足就挂起直到某个线程唤醒它们。

Condition 接口提供了挂起线程和唤醒线程的机制。

如果理解了 `Object.wait()` 和 `Object.notify()` 就很容易理解 Condition 了。前者是配合 synchronized 关键字使用，后者配合 Lock 接口使用，一般的说，就是配合 ReentrantLock 使用。

提供的基本方法：

```
void await() throws InterruptedException;
void awaitUninterruptibly();
long awaitNanos(long nanosTimeout) throws InterruptedException;
boolean await(long time, TimeUnit unit) throws InterruptedException;
boolean awaitUntil(Date deadline) throws InterruptedException;
void signal();
void signalAll();
```
- `await()` 方法会使当前线程等待，同时释放当前锁，当其他线程中使用 `signal()` 或 `signalAll()` 时，线程会重新获得锁并继续执行。
或者当线程被中断是，也能跳出等待。和 `Object.wait()` 方法很相似。

- `awaitUninterruptibly()` 方法与 `await()` 方法基本相同，但是并不会在等待过程中响应中断。

- `signal()` 方法用户唤醒一个在等待中的线程。和 `Object.notify()` 方法相似。

- `signalAll()` 方法会唤醒所有在等待中的线程。和 `Object.notifyAll()` 方法相似。


### [使用读写锁实现同步数据访问](bts_05/Main.java "查看示例")

锁机制最大的改进之一就是 ReadWriteLock 接口和它唯一的实现类 ReentrantReadWriteLock。

这个类有两个锁，一个是读操作锁，另一个是写操作锁。注意：只有读读操作是非阻塞的。

写锁中是可以获取读锁，但是读锁中是无法获取写锁的。

访问资源的两个条件：

- 读取：没有线程正在做写操作，且没有线程在请求写操作。

- 写入：没有线程正在做读写操作。


### [使用改进的读写锁StampedLock实现同步](bts_06/Main.java "查看示例")

StampedLock 类（JDK8中新增）提供了一种特殊的锁，它没有实现 Lock 接口或者 ReentrantLock 接口，但是提供了类似的功能。

首先，值得注意的是这个类的主要目的是作为实现线程安全组件的辅助类，所以它在普通应用中不是很常见。

主要特性：

- 三种锁模式

    - 写：排他锁，当一个线程获取该锁后，其它请求的线程必须等待，当目前没有线程持有读锁或者写锁的时候才可以获取到该锁。
    
    - 读：共享锁，在没有线程获取独占写锁的情况下，同时多个线程可以获取该锁，如果已经有线程持有写锁，其他线程请求获取该读锁会被阻塞。
    
    - 乐观读：线程没有锁的控制权，而其他的线程可能获取了排他锁。当通过乐观读模式拿到一个 stamp，需要使用 `validate()` 方法验证是否可以访问被保护的数据。

- 提供的方法

    - 当通过方法（`readLock()`、`readLockInterruptibly()`、`writeLock()`）去尝试获取锁，如果不能获取锁的控制权，那么当前线程会被挂起直到获取锁。
    
        - `readLock()` 和 `readLockInterruptibly()` 会返回一个 read stamp，可以用来解锁或转换模式
        
        - `writeLock()` 返回一个 write stamp，可以用来解锁或转换模式
    
    - 当通过方法（`tryOptimisticRead()`、`tryReadLock()`、`tryWriteLock()`）去尝试获取锁，如果不能获取锁的控制权，会返回一个 long 类型的值表征状态。
    返回 0 都表示未能获取锁。
    
        - `tryOptimisticRead()` 返回非 0 值表示一个有效的乐观读 stamp，获取该 stamp 后在具体操作数据前还需要调用 `validate()` 验证下该 stamp 是否已经不可用，
        也就是看当调用 `tryOptimisticRead()` 返回 stamp 后到到当前时间是否有其他线程持有了写锁，如果是那么 `validate()` 会返回 0，否则就可以使用该 stamp 版本的锁对数据进行操作。

    - 使用方法 `asReadLock()`、`asWriteLock()`、`asReadWriteLock()` 返回对应锁的视图。都不支持 `newCondition()` 方法。
     
    - 使用方法 `tryConvertToReadLock()`、`tryConvertToWriteLock()`、`tryConvertToOptimisticRead()` 尝试转化为其他类型的锁。返回 0 表示失败，否则为相应的 stamp。
    
    - 使用方法 `isReadLocked()`、`isWriteLocked()` 来判断当前持有的锁的类型，即是否是共享锁，排他锁。
    
    - 使用方法 `unlock()`、`unlockRead()`、`unlockWrite()` 来释放对应的锁。
    
    - 使用方法 `getReadLockCount()` 来统计有多少读锁。 

- StampedLock 不是可重入的，如果获取锁之后再调用方法尝试去获取锁，则会导致死锁。

- StampedLock 没有所有权的概念，可以一个线程获取锁后被其他线程释放。

- StampedLock 的调度策略不是一惯地倾向于选择读者而不是写者，或相反。所有 try 方法都是尽最大努力的，不必向任何调度或公平策略确认。














