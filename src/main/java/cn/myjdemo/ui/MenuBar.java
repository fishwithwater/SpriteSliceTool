package cn.myjdemo.ui;

import com.github.weisj.darklaf.iconset.AllIcons;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author fishwithwater
 * @date 2024/7/18 16:36
 * @description
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class MenuBar extends JMenuBar {

    private List<Consumer<List<File>>> fileListeners = new ArrayList<>();

    public MenuBar() {
        super();
        createFileMenu();
        createSettingMenu();
        this.setVisible(true);
    }

    private void createFileMenu() {
        JMenu fileMenu = new JMenu("文件");
        JMenuItem openFileItem = new JMenuItem("打开文件");
        openFileItem.setIcon(AllIcons.Files.General.get());
        fileMenu.add(openFileItem);

        JMenuItem openFolderItem = new JMenuItem("打开文件夹");
        openFolderItem.setIcon(AllIcons.Files.Folder.get());
        fileMenu.add(openFolderItem);

        add(fileMenu);

        openFileItem.addActionListener(a -> {
            handleOpenFile(false);
        });

        openFolderItem.addActionListener(a -> {
            handleOpenFile(true);
        });

    }

    private void handleOpenFile(boolean folderMode) {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(!folderMode);
        chooser.setFileSelectionMode(folderMode ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (folderMode) {
                    return f.isDirectory();
                }
                return f.isDirectory() || f.getPath().endsWith(".png");
            }

            @Override
            public String getDescription() {
                return folderMode ? "文件夹" : "*.png";
            }
        });
        chooser.showDialog(new JLabel(), "选择");
        File[] selectedFiles = chooser.getSelectedFiles();
        if (selectedFiles != null && selectedFiles.length > 0) {
            notifyFileChange(Arrays.stream(selectedFiles).toList());
        } else if (chooser.getSelectedFile() != null) {
            notifyFileChange(List.of(chooser.getSelectedFile()));
        }
    }

    private void createSettingMenu() {
        JMenu settingMenu = new JMenu("设置");
        JMenuItem autoSliceItem = new JCheckBoxMenuItem("自动分割");
        settingMenu.add(autoSliceItem);
        add(settingMenu);
    }

    public void addFileListener(Consumer<List<File>> listener) {
        fileListeners.add(listener);
    }

    private void notifyFileChange(List<File> fileList) {
        fileListeners.forEach(listener -> listener.accept(fileList));
    }

    public void removeFileListener(Consumer<List<File>> listener) {
        fileListeners.remove(listener);
    }
}
