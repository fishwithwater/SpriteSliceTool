package cn.myjdemo;

import cn.myjdemo.ui.MainFrame;
import com.github.weisj.darklaf.LafManager;
import javax.swing.SwingUtilities;

/**
 * @author fishwithwater
 * @date 2024/7/18 16:27
 * @description
 **/
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LafManager.install();
            new MainFrame();
        });
    }
}