package com.liang.config;

import com.liang.AutomaticDriver;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author ：宁鑫
 * @date ：2022/1/14 18:08
 * @description：监听spring启动
 */
@Component
public class AutomaticDriveMonitor implements InitializingBean {
    @Autowired
    private AutomaticDriver automaticDriver;

    @Override
    public void afterPropertiesSet() {
        try {
            System.out.println("automatic_driver_mongodb:监听到了spring启动");
            automaticDriver.init(null, null, null);
        } catch ( IOException e) {
            e.printStackTrace();
        }
    }
}
