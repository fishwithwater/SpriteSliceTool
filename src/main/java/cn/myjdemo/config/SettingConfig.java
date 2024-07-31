package cn.myjdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author maoyingjie
 * @date 2024/7/29 11:26
 * @description
 **/
@Configuration
public class SettingConfig {

    @Bean
    public AppSetting getAppSetting() {
        return new AppSetting();
    }


}
