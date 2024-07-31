package cn.myjdemo.ui;

import cn.myjdemo.config.AppSetting;
import cn.myjdemo.config.SettingConstant;
import cn.myjdemo.i18n.I18nUtil;
import com.github.weisj.darklaf.iconset.AllIcons;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * @author fishwithwater
 * @date 2024/7/18 16:36
 * @description
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Component
@Lazy
public class MenuBar extends JMenuBar {

    private List<Consumer<List<File>>> fileListeners = new ArrayList<>();

    @Autowired
    private AppSetting appSetting;

    public MenuBar() {
        super();
        createFileMenu();
        createSettingMenu();
        this.setVisible(true);
    }

    private void createFileMenu() {
        JMenu fileMenu = new JMenu(I18nUtil.getMessage("file"));
        JMenuItem openFileItem = new JMenuItem(I18nUtil.getMessage("file.open"));
        openFileItem.setIcon(AllIcons.Files.General.get());
        fileMenu.add(openFileItem);

        JMenuItem openFolderItem = new JMenuItem(I18nUtil.getMessage("file.folder.open"));
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
                return folderMode ? I18nUtil.getMessage("folder") : "*.png";
            }
        });
        chooser.showDialog(new JLabel(), I18nUtil.getMessage("select"));
        File[] selectedFiles = chooser.getSelectedFiles();

        if (ArrayUtils.isNotEmpty(selectedFiles)) {
            notifyFileChange(Arrays.stream(selectedFiles).toList());
        } else if (chooser.getSelectedFile() != null) {
            notifyFileChange(List.of(chooser.getSelectedFile()));
        }
    }

    private void createSettingMenu() {
        JMenu settingMenu = new JMenu(I18nUtil.getMessage("settings"));
        JMenuItem autoSliceItem = new JCheckBoxMenuItem(I18nUtil.getMessage("auto.slice"));
        settingMenu.add(autoSliceItem);
        JMenu languageItem = new JMenu(I18nUtil.getMessage("languages"));
        ButtonGroup bg = new ButtonGroup();

        Locale currentLocale = Locale.getDefault();

        JRadioButtonMenuItem zhCN = new JRadioButtonMenuItem(I18nUtil.getMessage("language.zh_CN"));
        zhCN.addActionListener(e -> changeLocale(Locale.CHINA));
        bg.add(zhCN);
        languageItem.add(zhCN);
        if (currentLocale.toString().equals("zh_CN")) {
            zhCN.setSelected(true);
        }

        JRadioButtonMenuItem en = new JRadioButtonMenuItem(I18nUtil.getMessage("language.en"));
        en.addActionListener(e -> changeLocale(Locale.ENGLISH));
        bg.add(en);
        languageItem.add(en);
        if (currentLocale.toString().equals("en")) {
            en.setSelected(true);
        }

        settingMenu.add(languageItem);
        add(settingMenu);
    }

    private void changeLocale(Locale locale) {
        appSetting.putByGroup(SettingConstant.KEY_LOCALE, SettingConstant.GROUP_GENERAL, locale.toString(), true);
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
