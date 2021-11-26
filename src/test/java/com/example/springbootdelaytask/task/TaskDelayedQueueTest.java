package com.example.springbootdelaytask.task;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskDelayedQueueTest {


    @Test
    void addDelayQueue() {
       TaskDelayedQueue.addDelayQueue(1L,1L);
    }
}
