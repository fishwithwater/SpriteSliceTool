package cn.myjdemo.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author maoyingjie
 * @date 2024/7/26 9:25
 * @description
 **/
public class I18nUtil {

    private static ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

    public static String getMessage(String key, Object... args) {
        String messageTemplate = bundle.getString(key);
        return String.format(messageTemplate, args);
    }

}
