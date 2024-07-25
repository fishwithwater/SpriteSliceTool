package cn.myjdemo;

import cn.myjdemo.ui.MainFrame;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.util.PropertyValue;
import org.pbjar.jxlayer.plaf.ext.TransformUI;

import javax.swing.SwingUtilities;

/**
 * @author fishwithwater
 * @date 2024/7/18 16:27
 * @description
 **/
public class Main {

    public static void main(String[] args) {
        System.setProperty(TransformUI.BUFFERED_REPAINT_FLAG, PropertyValue.TRUE);
        SwingUtilities.invokeLater(() -> {
            LafManager.install();
            new MainFrame();
        });
    }
}