package cn.myjdemo.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.setting.Setting;
import cn.myjdemo.config.handler.SettingChangeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

/**
 * @author maoyingjie
 * @date 2024/7/29 11:39
 * @description
 **/
public class AppSetting {

    private final Setting setting;

    @Autowired
    private ApplicationContext applicationContext;

    public AppSetting() {
        File configFile = getConfigFile();
        if (FileUtil.exist(configFile)) {
            setting = new Setting(configFile, StandardCharsets.UTF_8, true);
        } else {
            setting = new Setting(FileUtil.touch(configFile), StandardCharsets.UTF_8, true);
            initSetting();
        }
    }

    private void initSetting() {
        putByGroup(SettingConstant.KEY_LOCALE, SettingConstant.GROUP_GENERAL, Locale.getDefault().toString(), false, true);
        flush();
    }

    public void flush() {
        setting.store();
    }

    public void putByGroup(String key, String group, String value, boolean flush) {
        putByGroup(key, group, value, flush, false);
    }

    public void putByGroup(String key, String group, String value, boolean flush, boolean init) {
        String oldValue = setting.getByGroup(key, group);
        setting.putByGroup(key, group, value);

        if (!init) {
            Map<String, SettingChangeHandler> handlerMap = applicationContext.getBeansOfType(SettingChangeHandler.class);
            handlerMap.values().stream().filter(handler -> handler.support(key, group))
                    .forEach(handler -> handler.onChange(key, group, oldValue, value));
        }

        if (flush) {
            setting.store();
        }
    }

    public void putByGroup(String key, String group, String value) {
        putByGroup(key, group, value, false, false);
    }

    public String getByGroup(String key, String group) {
        return setting.getByGroup(key, group);
    }

    private static File getConfigFile() {
        String systemAppDataHome = System.getenv("APPDATA");
        if (FileUtil.exist(systemAppDataHome) && FileUtil.isDirectory(systemAppDataHome)) {
            String appConfigHome = systemAppDataHome + File.separator + "SpriteSliceTool";
            if (!FileUtil.exist(appConfigHome)) {
                FileUtil.mkdir(appConfigHome);
            }
            return new File(appConfigHome + File.separator + "config.setting");
        } else {
            String userHome = System.getProperty("user.home");
            if (FileUtil.exist(userHome) && FileUtil.isDirectory(userHome)) {
                String appConfigHome = userHome + File.separator + ".SpriteSliceTool";
                if (!FileUtil.exist(appConfigHome)) {
                    FileUtil.mkdir(appConfigHome);
                }
                return new File(appConfigHome + File.separator + "config.setting");
            } else {
                return new File("config.setting");
            }
        }
    }
}
