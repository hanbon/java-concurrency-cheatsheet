## 线程同步工具 (thread synchronization utilities)

使用更高级的同步机制来实现多线程的同步。

- Semaphore：一种计数器，用来保护一个或多个共享资源的访问。并发编程的基础工具，大多数编程语言都提供了这个机制。

- CountDownLatch：允许一个或多个线程一直等待，直到其他线程的操作执行完后再执行。 

- CyclicBarrier：允许多个线程在集合点（common point）处进行相互等待。

- Parser：把并发任务分成了多个阶段运行，在开始下一阶段前，当前阶段的所有线程都必须执行完成。

- Exchanger：提供了两个线程之间的数据交换点。

- CompletableFuture：提供了一种优雅的处理异步任务的机制。



