package cn.myjdemo.config.handler;

import cn.myjdemo.config.SettingConstant;
import cn.myjdemo.i18n.I18nUtil;
import cn.myjdemo.ui.MainFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.swing.JOptionPane;

/**
 * @author maoyingjie
 * @date 2024/7/29 13:14
 * @description
 **/
@Component
@Slf4j
@Lazy
public class RestartRequireHandler implements SettingChangeHandler {

    @Override
    public void onChange(String key, String group, String oldValue, String newValue) {
        Object[] options = {I18nUtil.getMessage("restart.now"), I18nUtil.getMessage("not.now")};
        int n = JOptionPane.showOptionDialog(null, I18nUtil.getMessage("restart.to.apply.changes"), I18nUtil.getMessage("attention"),
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (n == JOptionPane.YES_OPTION) {
            log.info("click restart now");
            //todo restart logic
        } else{
            log.info("click restart later");
        }
    }

    @Override
    public boolean support(String key, String group) {
        return SettingConstant.GROUP_GENERAL.equals(group) && SettingConstant.KEY_LOCALE.equals(key);
    }
}
