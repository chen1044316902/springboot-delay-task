package com.example.springbootdelaytask.abs;

import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonShutdownException;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: AbstractDelayedQueue
 * @Description: 延时队列
 * @author: 55555
 * @date: 2021年03月26日 3:11 下午
 */
@Slf4j
public abstract class AbstractDelayedQueue<T> {

    @Autowired
    RedissonClient redissonClient;
    /**
     * 线程池
     */
    protected final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 20,
            60L * 15L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(200),
            new DefaultThreadFactory("message-task"), new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 线程池
     */
    protected final ThreadPoolExecutor BOSS = new ThreadPoolExecutor(1, 1,
            60L * 15L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(200),
            new DefaultThreadFactory("BOSS-task"), new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 获取队列名称
     * @return
     */
    protected abstract String getQueueName();

    /**
     * 获取任务监听
     * @param t
     * @return
     */
    protected abstract  TaskEventListener<T> getTaskEventListener(T t);



    /**
     * 任务回调监听
     *
     * @param <T>
     */
    public abstract static class TaskEventListener<T> implements Runnable {

        public TaskEventListener(T t){
            this.t = t;
        }

        private T t;

        @Override
        public void run() {
            invoke(t);
        }

        /**
         * 方法执行实体
         * @param t
         */
        protected abstract void invoke(T t);


    }

    /**
     * 时间前置处理
     * @param t  信息对象
     * @param delay    时间数量
     * @param timeUnit 时间单位
     */
    protected void preProcessing(T t, long delay, TimeUnit timeUnit){
        if(delay <= 0){
            throw new RuntimeException("延时时间必须大于0!");
        }
    }

    /**
     * 添加队列
     *
     * @param t  信息对象
     */
    protected void remove(T t) {
        RBlockingQueue<T> blockingFairQueue = redissonClient.getBlockingQueue(getQueueName());
        final RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(blockingFairQueue);
        delayedQueue.remove(t);
    }

    /**
     * 添加队列
     *
     * @param t  信息对象
     * @param delay    时间数量
     * @param timeUnit 时间单位
     */
    protected void addQueue(T t, long delay, TimeUnit timeUnit) {
        preProcessing(t, delay, timeUnit);
        RBlockingQueue<T> blockingFairQueue = redissonClient.getBlockingQueue(getQueueName());
        RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(blockingFairQueue);
        delayedQueue.offer(t, delay, timeUnit);
    }

    /**
     * 获取队列
     *
     * @return
     */
    @PostConstruct
    private void getQueue() {
        BOSS.execute(() -> {
            for (;;){
                try {
                    RBlockingQueue<T> blockingFairQueue = redissonClient.getBlockingQueue(getQueueName());
                    T take = blockingFairQueue.poll(1, TimeUnit.SECONDS);
                    if (take == null) {
                        continue;
                    }
                    log.info("获取到任务,{}",take);
                    threadPoolExecutor.execute(getTaskEventListener(take));
                } catch (InterruptedException e) {
                    log.error("关闭获取延时推送");
                } catch (RedissonShutdownException e){
                    log.error("redisson 关闭");
                }catch (Exception e){
                    log.error("执行推送任务失败！", e);
                }
            }
        });
    }

}
