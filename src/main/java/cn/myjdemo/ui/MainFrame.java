package cn.myjdemo.ui;

import cn.myjdemo.domain.Rect2;
import cn.myjdemo.sprite.SpriteService;
import cn.myjdemo.sprite.SpriteUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author fishwithwater
 * @date 2024/7/18 16:28
 * @description
 **/
public class MainFrame extends JFrame {

    private final int WINDOW_WIDTH = 1000;
    private final int WINDOW_HEIGHT = 600;

//    private ImagePanel imagePanel;

    private ImagePanle imagePanel;

    private Rect2 selectedRect;

    private JLabel bottomBarLabel;

    public MainFrame() throws HeadlessException {
        this.setTitle("Sprite Util");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        this.setResizable(false);
        JPanel jPanel = new JPanel();
        jPanel.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        jPanel.setLayout(new BorderLayout());
        add(jPanel);

        SelectFilePanel selectFilePanel = new SelectFilePanel();
        jPanel.add(selectFilePanel, BorderLayout.NORTH);

        MainFrame frame = this;
        selectFilePanel.addFilePathListener(filePath -> {
            BufferedImage curBufferedImg = null;
            try {
                curBufferedImg = ImageIO.read(new File(filePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (imagePanel == null) {
                imagePanel = new ImagePanle();
                imagePanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
                imagePanel.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
                imagePanel.setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
                imagePanel.updateImage(curBufferedImg);
                imagePanel.addClickRect2Listener(rect -> {
                    selectedRect = rect;
                    bottomBarLabel.setText("点击Sprite后显示坐标: " + rect.toString());
                });
                jPanel.add(imagePanel, BorderLayout.CENTER);
            } else {
                imagePanel.updateImage(curBufferedImg);
            }
            SpriteService spriteService = SpriteUtil.getSpriteService();
            CompletableFuture<List<Rect2>> rect2ListFuture = null;
            try {
                rect2ListFuture = spriteService.processSplitSpriteSheet(ImageIO.read(new File(filePath)));
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
            frame.refresh();
        });
        JPanel bottomBarPanel = new JPanel();
        bottomBarLabel = new JLabel("点击Sprite后显示坐标");
        bottomBarPanel.add(bottomBarLabel);
        jPanel.add(bottomBarPanel, BorderLayout.SOUTH);
        this.setVisible(true);
    }

    public void refresh() {
        this.setVisible(false);
        this.setVisible(true);
    }

}
