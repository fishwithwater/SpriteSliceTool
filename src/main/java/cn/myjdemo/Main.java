package cn.myjdemo;

import cn.myjdemo.ui.MainFrame;

import javax.swing.*;

/**
 * @author fishwithwater
 * @date 2024/7/18 16:27
 * @description
 **/
public class Main {
    private static String theme = "javax.swing.plaf.nimbus.NimbusLookAndFeel";

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(theme);
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }
        new MainFrame();
    }
}