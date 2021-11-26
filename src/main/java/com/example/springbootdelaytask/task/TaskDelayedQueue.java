package com.example.springbootdelaytask.task;

import com.example.springbootdelaytask.abs.AbstractDelayedQueue;
import com.example.springbootdelaytask.utils.SpringContextHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;


/**
 * @ClassName: OfferDelayedQueue
 * @Description: offer定时取消任务
 * @author: 55555
 * @date: 2021年07月23日 3:25 PM
 */
@Component
@Slf4j
public class TaskDelayedQueue extends AbstractDelayedQueue<Long> {


    @PostConstruct
    public void addTask() {
        System.out.println(DELAYED_OFFER_QUEUE_KEY);
    }

    /**
     * Offer延时任务队列
     */
    public static final String DELAYED_OFFER_QUEUE_KEY = "delayed:task_key";


    /**
     * 获取队列名称
     *
     * @return
     */
    @Override
    protected String getQueueName() {
        return DELAYED_OFFER_QUEUE_KEY;
    }

    /**
     * 获取任务监听
     *
     * @param offerId
     * @return
     */
    @Override
    protected TaskEventListener<Long> getTaskEventListener(Long offerId) {
        return new TaskEventListener<Long>(offerId) {
            @Override
            protected void invoke(Long offerId) {
                final TaskDelayedQueue bean = SpringContextHelper.getBean(TaskDelayedQueue.class);

                bean.addQueue(10L, 10L, TimeUnit.SECONDS);
            }
        };
    }



    /**
     * 插入前
     * @param offerId
     * @param delay    时间数量
     * @param timeUnit 时间单位
     */
    @Override
    protected void preProcessing(Long offerId, long delay, TimeUnit timeUnit) {
        super.preProcessing(offerId, delay, timeUnit);
        if(offerId == null){
            throw new RuntimeException("businessId不能为null!");
        }
    }

    /**
     * 插入Offer延期任务
     * @param offerId 添加
     * @param delay 秒
     */
    public static void addDelayQueue(Long offerId, long delay){
        final TaskDelayedQueue bean = SpringContextHelper.getBean(TaskDelayedQueue.class);

        bean.addQueue(100L, 10L, TimeUnit.SECONDS);
    }

    /**
     * 删除延期任务
     * @param offerId
     */
    public static void deleteOfferTask(Long offerId){
        final TaskDelayedQueue bean = SpringContextHelper.getBean(TaskDelayedQueue.class);
        bean.remove(offerId);
    }

}
