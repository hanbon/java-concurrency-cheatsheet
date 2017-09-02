## 线程同步工具 (thread synchronization utilities)

使用更高级的同步机制来实现多线程的同步。

- Semaphore：一种计数器，用来保护一个或多个共享资源的访问。并发编程的基础工具，大多数编程语言都提供了这个机制。

- CountDownLatch：允许一个或多个线程一直等待，直到其他线程的操作执行完后再执行。 

- CyclicBarrier：允许多个线程在集合点（common point）处进行相互等待。

- Parser：把并发任务分成了多个阶段运行，在开始下一阶段前，当前阶段的所有线程都必须执行完成。

- Exchanger：提供了两个线程之间的数据交换点。

- CompletableFuture：提供了一种优雅的处理异步任务的机制。


### 目录导航

- [并发的访问一个或多个资源](#并发的访问一个或多个资源)



### [并发的访问一个或多个资源](tsu_01/Main.java "查看示例")

Semaphore（信号量）是一种计数器，用来保护一个或多个共享资源的访问。

同样提供了非公平模式（默认）和公平模式。

使用信号量实现临界区的三个步骤：

- 通过 `acquire()` 方法获取信号量。

- 使用共享资源执行必要的操作。

- 最后，必须通过 `release()` 方法释放信号量。


提供的方法如下：
```
void	acquire() 获取一个许可，在提供一个许可前线程将一直阻塞，否则线程被中断。
void	acquire(int permits) 同上，获取多个许可。
void	acquireUninterruptibly() 不响应中断。
void	acquireUninterruptibly(int permits) 
boolean	tryAcquire() 仅在调用时存在一个可用许可，才从信号量获取许可。
boolean	tryAcquire(long timeout, TimeUnit unit) 
boolean	tryAcquire(int permits) 
boolean	tryAcquire(int permits, long timeout, TimeUnit unit) 
void	release()  释放一个许可。
void	release(int permits) 
int	availablePermits() 返回当前可用的许可数。
int	drainPermits()  获取并返回立即可用的所有许可。
int	getQueueLength() 返回正在等待获取的线程的估计数目。
boolean	hasQueuedThreads() 查询是否有线程正在等待获取。
boolean	isFair() 是否为公平模式
```



















