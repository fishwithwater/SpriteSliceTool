package cn.myjdemo.ui.config;

import cn.myjdemo.i18n.I18nUtil;
import cn.myjdemo.ui.FileTree;
import cn.myjdemo.ui.MainTabPane;
import com.github.weisj.darklaf.components.OverlayScrollPane;
import com.github.weisj.darklaf.components.tabframe.JTabFrame;
import com.github.weisj.darklaf.iconset.AllIcons;
import com.github.weisj.darklaf.util.Alignment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.swing.JLabel;

/**
 * @author maoyingjie
 * @date 2024/7/26 15:05
 * @description
 **/
@Configuration
public class UIConfig {

    @Bean
    @Lazy
    public FileTree getFileTree() {
        return new FileTree();
    }

    @Bean
    @Lazy
    public JTabFrame getJTabFrame() {
        JTabFrame jTabFrame = new JTabFrame();
        jTabFrame.setContent(getMainTabPane());
        OverlayScrollPane overlayScrollPane = new OverlayScrollPane(getFileTree());
        jTabFrame.addTab(overlayScrollPane, I18nUtil.getMessage("workspace.title"), AllIcons.Files.Folder.get(), Alignment.NORTH_WEST);
        jTabFrame.setAcceleratorAt(1, Alignment.NORTH_WEST, 0);
        return jTabFrame;
    }

    @Bean
    @Lazy
    public MainTabPane getMainTabPane() {
        return new MainTabPane();
    }

    @Bean("mainClickRectLabel")
    @Lazy
    public JLabel getMainClickRectLabel() {
        return new JLabel(I18nUtil.getMessage("click.to.show.rect.location"));
    }


}
