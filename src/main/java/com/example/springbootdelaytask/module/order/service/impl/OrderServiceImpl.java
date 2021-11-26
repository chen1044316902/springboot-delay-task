package com.example.springbootdelaytask.module.order.service.impl;

import com.example.springbootdelaytask.module.order.service.OrderService;
import com.example.springbootdelaytask.task.TaskDelayedQueue;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class OrderServiceImpl implements OrderService {


    @Override
    @PostConstruct
    public void addQueue() {
        TaskDelayedQueue.addDelayQueue(1L,1L);
    }
}
