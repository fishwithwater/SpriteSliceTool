package cn.myjdemo.ui;

import lombok.Data;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author fishwithwater
 * @date 2024/7/18 16:36
 * @description
 **/
@Data
public class SelectFilePanel extends JPanel {

    private JTextField filePath;

    private List<Consumer<String>> filePathListeners = new ArrayList<>();

    public SelectFilePanel() {
        add(new JLabel("选择Sprite图"));
        filePath = new JTextField();
        filePath.setPreferredSize(new Dimension(800, 28));
        add(filePath);
        JButton jBtn = new JButton("选择文件");
        add(jBtn);
        jBtn.addActionListener(a -> {
            JFileChooser chooser = new JFileChooser(filePath.getText());
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory() || f.getPath().endsWith(".jpg") || f.getPath().endsWith(".png")) {
                        return true;
                    }
                    return false;
                }

                @Override
                public String getDescription() {
                    return "*.png,*.jpg";
                }
            });
            chooser.showDialog(new JLabel(), "选择");
            if (chooser.getSelectedFile() != null) {
                filePath.setText(chooser.getSelectedFile().getPath());
                notifyFilePathChange(filePath.getText());
            }
        });
        this.setVisible(true);
    }

    public void addFilePathListener(Consumer<String> listener) {
        filePathListeners.add(listener);
    }

    private void notifyFilePathChange(String filePath) {
        filePathListeners.forEach(listener -> listener.accept(filePath));
    }

    public void removeFilePathListener(Consumer<String> listener) {
        filePathListeners.remove(listener);
    }
}
