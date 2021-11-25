package com.example.springbootdelaytask.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
* @ClassName: SpringContextHelper
* @Description: 工具类
* @author: 55555
* @date: 2020年09月07日 11:36 下午
*/
@Order(0)
@Component
public class SpringContextHelper implements ApplicationContextAware, InstantiationAwareBeanPostProcessor {



    /**
     * Spring应用上下文环境
     */
    private static ApplicationContext applicationContext;


    /**
     * 重写并初始化上下文
     *
     * @param applicationContext spring 上下文
     * @throws BeansException bean 异常
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHelper.applicationContext = applicationContext;
    }


    /**
     * 通过类获取
     *
     * @param clazz 注入的类
     * @param <T>   返回类型
     * @return 返回这个bean
     * @throws BeansException bean异常
     */
    public static <T> T getBean(Class<T> clazz) throws BeansException {
        return applicationContext.getBean(clazz);
    }

    /**
     * 通过名字获取
     *
     * @param name 名字
     * @param <T>  返回类型
     * @return 返回这个bean
     * @throws BeansException bean异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 从配置文件获取
     *
     * @param key 属性名
     * @return
     */
    public static String getApplicationProValue(String key) {
        return applicationContext.getEnvironment().getProperty(key);
    }


}
