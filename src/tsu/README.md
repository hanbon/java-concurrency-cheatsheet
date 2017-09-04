## 线程同步工具 (thread synchronization utilities)

使用更高级的同步机制来实现多线程的同步。

- Semaphore：一种计数器，用来保护一个或多个共享资源的访问。并发编程的基础工具，大多数编程语言都提供了这个机制。

- LockSupport：线程阻塞工具，可以在线程内任意位置让线程阻塞。

- CountDownLatch：允许一个或多个线程一直等待，直到其他线程的操作执行完后再执行。 

- CyclicBarrier：允许多个线程在集合点（common point）处进行相互等待。

- Parser：把并发任务分成了多个阶段运行，在开始下一阶段前，当前阶段的所有线程都必须执行完成。

- Exchanger：提供了两个线程之间的数据交换点。

- CompletableFuture：提供了一种优雅的处理异步任务的机制。


### 目录导航

- [并发的访问一个或多个资源](#并发的访问一个或多个资源)

- [线程阻塞工具](#线程阻塞工具)

- [等待多个并发事件的完成](#等待多个并发事件的完成)



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


### [线程阻塞工具](tsu_02/Main.java "查看示例")

LockSupport 是一个用来创建锁和其他同步类的基本线程阻塞原语，可以在线程内任意位置让线程阻塞。

和过时方法 `Thread.suspend()` 相比，它弥补了由于 `resume()` 前发生，导致线程无法继续执行的问题。

和 `Object.wait()` 相比，它不需要先获取某个对象的锁，也不会抛出 InterruptedException 异常。

LockSupport 使用了类似信号量的机制。它为每一个线程准备了一个许可，如果许可可用，那么 `park()` 函数立即返回，并且消费这个许可（也就是将许可变为不可用），
如果许可不可用，就会阻塞。而 `unpark()` 则使得一个许可变为可用，和信号量不同的是，许可不能累加，永远只有一个许可。

这个特点使得：即使 `unpark()` 操作发生在 `park()` 之前，也可以使得下一次的 `park()` 操作立即返回。

由于许可的存在，调用 `park()` 的线程和另一个试图将其 `unpark()` 的线程之间的竞争将保持活性。
此外，如果调用者线程被中断，并且支持超时，则 `park()` 将返回。
`park()` 方法还可以在其他任何时间**毫无理由**地返回，因此通常必须在重新检查返回条件的循环里调用此方法。
从这个意义上说，`park()` 是**忙等**的一种优化，它不会浪费这么多的时间进行自旋，但是必须将它与 `unpark()` 配对使用才更高效。

三种形式的 `park()` 还各自支持一个 blocker 对象参数。此对象在线程受阻塞时被记录，以允许监视工具和诊断工具确定线程受阻塞的原因。

处于 `park()` 挂起状态的线程不会像 `Thread.suspend()` 那样还给出一个令人费解的 Runnable 状态。
它会明确的给出一个 WAITING / TIME_WAITING 状态，还会标注一个 parking。

提供的方法如下：
```
static Object   getBlocker(Thread t) 返回提供给最近一次尚未解除阻塞的 park 方法调用的 blocker 对象，如果该调用不受阻塞，则返回 null。
static void	park() 
static void	park(Object blocker) 
static void	parkNanos(long nanos) 
static void	parkNanos(Object blocker, long nanos) 
static void	parkUntil(long deadline)
static void	parkUntil(Object blocker, long deadline)
static void	unpark(Thread thread)
```

### [等待多个并发事件的完成](tsu_03/Main.java "查看示例")

CountDownLatch 类是一个同步辅助类。在完成一组正在其他线程中执行的操作前，它允许线程一直等待。

这个类使用一个整数进行初始化，这个整数就是线程要等待完成操作的数目。

这个整数被初始化后就不能再次初始化或者修改，唯一能改变这个值的方法是 `countDown()` 方法。可以使用 `getCount()` 方法获取内部计数器的值。

当一个线程要等待某些操作先执行完时，需要调用 `await()` 方法，这个方法让线程进入休眠直到等待的所有操作都完成。

另一个 `boolean await(long timeout, TimeUnit unit)` 方法则是线程被休眠直到CountDownLatch 类的内部计数器为 0 （返回 true） 或者指定的时间过期（返回 false）。

当一个操作完成后，它将调用 `countDown()` 方法将 CountDownLatch 类的内部计数器减 1。

当计数器变成 0 的时候，CountDownLatch 类将唤醒所有调用 `await()` 方法而进入休眠的线程。

特点：

- CountDownLatch 机制不是用来保护共享资源或者临界区的，它是用来同步执行多个任务的一个或者多个线程。

- CountDownLatch 只准许进入一次。一旦内部计数器减到 0，再调用 `countDown()` 方法将不起作用。如果还要做类似的同步，就必须创建一个新的 CountDownLatch 对象。








