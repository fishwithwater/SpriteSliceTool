package cn.myjdemo.ui;

import cn.myjdemo.domain.Rect2;
import cn.myjdemo.sprite.SpriteService;
import cn.myjdemo.sprite.SpriteUtil;
import com.github.weisj.darklaf.components.ClosableTabbedPane;
import com.github.weisj.darklaf.components.TabEvent;
import com.github.weisj.darklaf.components.TabListener;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author maoyingjie
 * @date 2024/7/25 14:15
 * @description
 **/
public class MainTabPane extends ClosableTabbedPane implements TabListener {

    private Map<File, JPanel> tabPanelMap = new HashMap<>();

    private MainFrame mainFrameRef;

    public MainTabPane() {
        super();
        addTabListener(this);
    }

    public void addImageTab(File file) {
        if (tabPanelMap.containsKey(file)) {
            setSelectedComponent(tabPanelMap.get(file));
            return;
        }
        ImagePanel imagePanel = new ImagePanel();
        imagePanel.addClickRect2Listener(rect2 -> {
            mainFrameRef.getBottomBarLabel().setText("点击Sprite后显示坐标: " + rect2.toString());
        });
        BufferedImage curBufferedImg = null;
        try {
            curBufferedImg = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        imagePanel.setSize(getSize());
        imagePanel.updateImage(curBufferedImg);
        tabPanelMap.put(file, imagePanel);
        addTab(file.getName(), imagePanel);
        setSelectedComponent(imagePanel);

        SpriteService spriteService = SpriteUtil.getSpriteService();
        CompletableFuture<List<Rect2>> rect2ListFuture = null;
        try {
            rect2ListFuture = spriteService.processSplitSpriteSheet(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        rect2ListFuture.whenCompleteAsync((rect2List, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            } else if (imagePanel != null) {
                imagePanel.updateRects(rect2List);
            }
        });
    }

    @Override
    public void tabOpened(TabEvent tabEvent) {

    }

    @Override
    public void tabClosing(TabEvent tabEvent) {
        tabPanelMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(getComponentAt(tabEvent.getTabIndex())))
                .map(Map.Entry::getKey)
                .findFirst().ifPresent(tabPanelMap::remove);
    }

    @Override
    public void tabClosed(TabEvent tabEvent) {
    }

    public void setMainFrameRef(MainFrame mainFrameRef) {
        this.mainFrameRef = mainFrameRef;
    }
}
