package cn.myjdemo.config.handler;

/**
 * @author maoyingjie
 * @date 2024/7/29 13:13
 * @description
 **/
public interface SettingChangeHandler {

    void onChange(String key, String group, String oldValue, String newValue);

    boolean support(String key, String group);

}
