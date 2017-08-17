## 线程管理 (thread management)

### 目录导航

- [线程的创建、运行与信息的获取](#线程的创建、运行与信息的获取)


### 线程的创建、运行与信息的获取

每个 Java 程序至少有一个执行线程。当运行程序的时候，JVM 会启动这个执行线程来调用程序的 main() 方法。

当调用 Thread 对象的 start() 方法时，另一个执行线程将被创建。

当所有的非守护线程都运行完成的时候，Java 程序结束。

如果初始线程结束了，其余的线程仍将继续执行直到结束。

如果一个线程调用了 System.exit()，所有线程都将结束。


#### 创建线程的两种方式：

- 继承 Thread 类，并重写该类的 run() 方法。

- 创建一个实现 Runnable 接口的类。使用带参数的 Thread 构造器来创建 Thread 对象。参数是实现 Runnable 接口的类的一个对象。

- 注：还可以使用 Callable + FutureTask 的方式创建：使用 FutureTask 类来包装 Callable 对象，使用 FutureTask 对象作为 Thread 对象参数
  创建并启动线程，最后调用 FutureTask 对象的 get() 方法来获得线程执行结束后的返回值。本质上是 FutureTask 实现了 Runnable 接口。


#### 线程的信息：

- ID：线程唯一标识符。

- Name: 线程名称。

- Priority: 线程优先级，从低到高为 1 到 10。注意：线程优先级是 Java 平台上最不可移植的特征。当设计多线程应用程序的时候，一定不要依赖于线程的优先级。
  因为线程调度优先级操作是没有保障的，只能把线程优先级作用作为一种提高程序效率的方法，但是要保证程序不依赖这种操作。

- Status: 线程的状态，有 6 种。

    - **NEW**: 线程被创建，还没有运行 start 方法。

    - **RUNNABLE**: 调用 start() 后线程在执行 run 方法且没有阻塞时状态为 RUNNABLE。 
      不过，RUNNABLE 不代表 CPU 一定在执行该线程的代码，可能正在执行也可能在等待操作系统分配时间片，只是它没有在等待其他条件，比如 IO

    - **BLOCKED**: 阻塞状态，等待锁的释放，比如线程A进入了一个 synchronized 方法，线程B也想进入这个方法， 
      但是这个方法的锁已经被线程A获取了，这个时候线程B就处于 BLOCKED 状态。

    - **WAITING**: 等待状态，处于等待状态的线程是由于执行了3个方法中的任意方法。

        - Object的 wait() 方法，并且没有使用 timeout 参数

        - Thread的 join() 方法，没有使用 timeout 参数 

        - LockSupport 的 park 方法

        处于 waiting 状态的线程会等待另外一个线程处理特殊的行为。 
        再举个例子，如果一个线程调用了一个对象的 wait 方法，那么这个线程就会处于 WAITING 状态直到另外一个线程调用这个对象的 notify() 或者 notifyAll() 方法后才会解除这个状态。

    - **TIME_WAITING**: 有等待时间的等待状态，比如调用了以下几个方法中的任意方法，并且指定了等待时间，线程就会处于这个状态。 

        - Thread.sleep() 方法

        - Object 的 wait() 方法，带有时间

        - Thread.join() 方法，带有时间

        - LockSupport 的 parkNanos() 方法，带有时间

        - LockSupport 的 parkUntil() 方法，带有时间

    - **TERMINATED**: 线程运行结束后状态为 TERMINATED



