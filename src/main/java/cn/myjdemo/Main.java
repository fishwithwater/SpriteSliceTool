package cn.myjdemo;

import cn.myjdemo.ui.MainFrame;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author fishwithwater
 * @date 2024/7/18 16:27
 * @description
 **/
public class Main {

    public static void main(String[] args) {
        try {
            String theme = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
            UIManager.setLookAndFeel(theme);
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException |
                 IllegalAccessException e) {
            e.printStackTrace();
        }
        new MainFrame();
    }
}