package cn.myjdemo;

import cn.myjdemo.config.AppSetting;
import cn.myjdemo.config.SettingConstant;
import cn.myjdemo.ui.MainFrame;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.util.PropertyValue;
import org.pbjar.jxlayer.plaf.ext.TransformUI;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import javax.swing.SwingUtilities;
import java.util.Locale;

/**
 * @author fishwithwater
 * @date 2024/7/18 16:27
 * @description
 **/
@ComponentScan("cn.myjdemo")
public class Main {

    public static ApplicationContext APPLICATION_CONTEXT;

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
        APPLICATION_CONTEXT = context;
        System.setProperty(TransformUI.BUFFERED_REPAINT_FLAG, PropertyValue.TRUE);
        SwingUtilities.invokeLater(() -> {
            LafManager.install();
            //i18n
            AppSetting appSetting = context.getBean(AppSetting.class);
            Locale locale = Locale.of(appSetting.getByGroup(SettingConstant.KEY_LOCALE, SettingConstant.GROUP_GENERAL));
            Locale.setDefault(locale);
            //start
            context.getBean(MainFrame.class);
        });
    }
}