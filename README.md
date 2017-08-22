# Java Concurrency Cheatsheet

Java 中关于并发相关知识的整理，持续更新中... 满满的干货，欢迎 Star⭐

Tips: 项目配合 Chrome 插件 [Octotree](https://chrome.google.com/webstore/detail/octotree/bkhaagjahfmjljalopjnoealnfndnagc "点击跳转") 食用更佳😎

## 目录导航

- [基础并发知识 (basic concurrency knowledge)](src/bck/README.md "进入子页面")

- [线程管理 (thread management)](src/tm/README.md "进入子页面")

- [线程同步基础 (basic thread synchronization)](src/bts/README.md "进入子页面")

- [线程同步工具 (thread synchronization utilities)](src/tsu/README.md "进入子页面")

- [线程执行器 (thread executors)](src/te/README.md "进入子页面")

- [Fork / Join 框架 (Fork / Join Framework)](src/fjf/README.md "进入子页面")

- [并行和异步流 (parallel and reactive streams)](src/pars/README.md "进入子页面")

- [并发集合 (concurrent collections)](src/cc/README.md "进入子页面")

- [定制并发类 (customizing concurrency classes)](src/ccc/README.md "进入子页面")

- [测试并发应用 (testing concurrency applications)](src/tca/README.md "进入子页面")

- [并发编程习惯用法 (concurrent programming idiom)](src/cpi/README.md "进入子页面")

- [并发编程设计模式 (concurrent programming design patterns)](src/cpdp/README.md "进入子页面")


## 知识点概览

- [基础并发知识 (basic concurrency knowledge)](src/bck/README.md "进入子页面")

- [线程管理 (thread management)](src/tm/README.md "进入子页面")

    - 线程的创建、运行与信息的获取
    
        - 创建线程的方式有哪些？
        
        - 程序能依赖线程的优先级吗？
        
        - 线程的状态有哪些？
        
        - 哪些操作可以引起线程状态的改变？
    
    - 线程的中断与处理
    
        - 如何中断线程？
        
        - 线程中断后一定会退出吗？
        
        - `interrupted()` 和 `isInterrupted()` 的区别是什么？
        
        - InterruptedException 异常与中断标记有什么联系？
         
        - 为什么会在捕获 InterruptedException 后再次调用 `interrupt()` 方法？

    - 等待线程结束和谦让
    
        -  `join()` 方法的本质？
        
        - `Thread.yeild()`作用？
        
    - 守护进程的创建与运行
    
        - 守护进程与普通进程的区别？
        
        - 守护进程编程中的注意事项？

    - 线程中未受检异常的处理
    
        - 线程内的未受检异常有哪些处理方式？

    - 线程中局部变量的使用
    
        - 什么是线程局部变量？
        
        - 使用线程局部变量一定能提升性能吗？
        
        - 为什么线程局部变量可能导致内存泄露？
        
        - 子线程想要访问父线程的局部变量怎么办？


- [线程同步基础 (basic thread synchronization)](src/bts/README.md "进入子页面")

- [线程同步工具 (thread synchronization utilities)](src/tsu/README.md "进入子页面")

- [线程执行器 (thread executors)](src/te/README.md "进入子页面")

- [Fork / Join 框架 (Fork / Join Framework)](src/fjf/README.md "进入子页面")

- [并行和异步流 (parallel and reactive streams)](src/pars/README.md "进入子页面")

- [并发集合 (concurrent collections)](src/cc/README.md "进入子页面")

- [定制并发类 (customizing concurrency classes)](src/ccc/README.md "进入子页面")

- [测试并发应用 (testing concurrency applications)](src/tca/README.md "进入子页面")

- [并发编程习惯用法 (concurrent programming idiom)](src/cpi/README.md "进入子页面")

- [并发编程设计模式 (concurrent programming design patterns)](src/cpdp/README.md "进入子页面")
