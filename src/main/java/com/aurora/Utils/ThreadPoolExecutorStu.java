package com.aurora.Utils;

import java.util.concurrent.*;

public class ThreadPoolExecutorStu {

    // 线程池中初始线程个数
    private final static Integer CORE_POOL_SIZE = 10;
    // 线程池中允许的最大线程数
    private final static Integer MAXIMUM_POOL_SIZE = 20;
    // 当线程数大于初始线程时。终止多余的空闲线程等待新任务的最长时间
    private final static Long KEEP_ALIVE_TIME = 10L;
    // 任务缓存队列 ，即线程数大于初始线程数时先进入队列中等待，此数字可以稍微设置大点，避免线程数超过最大线程数时报错。或者直接用无界队列
    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<Runnable>(2000);

    public static void main(String[] args) {
        Long start = System.currentTimeMillis();
        /**
         * ITDragonThreadPoolExecutor 耗时 1503
         * ITDragonFixedThreadPool 耗时 505
         * ITDragonSingleThreadExecutor 语法问题报错，
         * ITDragonCachedThreadPool 耗时506
         * 推荐使用自定义线程池，或newFixedThreadPool(n)
         */
        ThreadPoolExecutor threadPoolExecutor = ITDragonThreadPoolExecutor();
        for (int i = 0; i < 8; i++) {    // 执行8个任务，若超过MAXIMUM_POOL_SIZE则会报错 RejectedExecutionException
            MyRunnableTest myRunnable = new MyRunnableTest(i);
            threadPoolExecutor.execute(myRunnable);
            System.out.println("线程池中现在的线程数目是："+threadPoolExecutor.getPoolSize()+",  队列中正在等待执行的任务数量为："+
                    threadPoolExecutor.getQueue().size());
        }
        // 关掉线程池 ，并不会立即停止(停止接收外部的submit任务，等待内部任务完成后才停止)，推荐使用。 与之对应的是shutdownNow，不推荐使用
        threadPoolExecutor.shutdown();
        try {
            // 阻塞等待30秒关掉线程池，返回true表示已经关闭。和shutdown不同，它可以接收外部任务，并且还阻塞。这里为了方便统计时间，所以选择阻塞等待关闭。
            threadPoolExecutor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("耗时 : " + (System.currentTimeMillis() - start));
    }

    // 自定义线程池，开发推荐使用
    public static ThreadPoolExecutor ITDragonThreadPoolExecutor() {
        // 构建一个，初始线程数量为3，最大线程数据为8，等待时间10分钟 ，队列长度为5 的线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, WORK_QUEUE);
        return threadPoolExecutor;
    }

    /**
     * 固定大小线程池
     * corePoolSize初始线程数和maximumPoolSize最大线程数一样，keepAliveTime参数不起作用，workQueue用的是无界阻塞队列
     */
    public static ThreadPoolExecutor ITDragonFixedThreadPool() {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
        return threadPoolExecutor;
    }

    /**
     * 单线程线程池
     * 等价与Executors.newFixedThreadPool(1);
     */
    public static ThreadPoolExecutor ITDragonSingleThreadExecutor() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
        return threadPoolExecutor;
    }

    /**
     * 无界线程池
     * corePoolSize 初始线程数为零
     * maximumPoolSize 最大线程数无穷大
     * keepAliveTime 60秒类将没有被用到的线程终止
     * workQueue SynchronousQueue 队列，无容量，来任务就直接新增线程
     * 不推荐使用
     */
    public static ThreadPoolExecutor ITDragonCachedThreadPool() {
        ExecutorService executor = Executors.newCachedThreadPool();
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
        return threadPoolExecutor;
    }

}

class MyRunnableTest implements Runnable {
    private Integer num;    // 正在执行的任务数
    public MyRunnableTest(Integer num) {
        this.num = num;
    }
    public void run() {
        System.out.println("正在执行的MyRunnable " + num);
        try {
            Thread.sleep(500);// 模拟执行事务需要耗时
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("MyRunnable " + num + "执行完毕");
    }
}