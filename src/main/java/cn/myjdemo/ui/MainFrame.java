package cn.myjdemo.ui;

import cn.myjdemo.domain.ProjectFile;
import cn.myjdemo.i18n.I18nUtil;
import com.github.weisj.darklaf.components.tabframe.JTabFrame;
import com.github.weisj.darklaf.util.Alignment;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CompletableFuture;

import static cn.myjdemo.domain.ProjectFile.generateByFiles;
import static cn.myjdemo.util.ThreadUtil.GLOBAL_EXECUTOR;

/**
 * @author fishwithwater
 * @date 2024/7/18 16:28
 * @description
 **/
@Lazy
@Component
public class MainFrame extends JFrame implements InitializingBean {

    private final int WINDOW_WIDTH = 1000;
    private final int WINDOW_HEIGHT = 600;
    private final Timer timer = new Timer();
    private final LinkedList<File> imageList = new LinkedList<>();
    @Autowired
    private MainTabPane mainTabPane;
    @Autowired
    @Qualifier("mainClickRectLabel")
    private JLabel bottomBarLabel;
    @Autowired
    private MenuBar menuBar;
    @Autowired
    private FileTree fileTree;
    @Autowired
    private JTabFrame jTabFrame;

    public MainFrame() throws HeadlessException {
        super();
        this.setTitle(I18nUtil.getMessage("project.title"));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        this.setResizable(true);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        addComponents();

        addListeners();

        addBackgroundThread();

        this.setVisible(true);
    }

    private void addBackgroundThread() {
//        long processTaskInterval = 10L;
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (imageList.isEmpty()) return;
//                SpriteService spriteService = SpriteUtil.getSpriteService();
//                for (File file : imageList) {
//                    double process = spriteService.getSplitSpriteSheetProcess(file);
//                    if (process < 0) {
//                        continue;
//                    }
//                    System.out.println(file.getPath() + ":" + (process * 100));
//                }
//            }
//        }, processTaskInterval, processTaskInterval);
    }

    void addComponents() {
        //MenuBar
        setJMenuBar(menuBar);
        //Layout Component
        JPanel layoutPanel = new JPanel();
        layoutPanel.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        layoutPanel.setLayout(new BorderLayout());

        //Image Previewer Component

        //Bottom Bar Component
        JPanel bottomBarPanel = new JPanel();
        bottomBarPanel.add(bottomBarLabel);
        layoutPanel.add(bottomBarPanel, BorderLayout.SOUTH);

        //TabFrame
        layoutPanel.add(jTabFrame, BorderLayout.CENTER);

        add(layoutPanel);
    }

    void addListeners() {
        //Select File Listener
        menuBar.addFileListener(fileList -> {
            CompletableFuture.runAsync(() -> {
                List<ProjectFile> projectFiles = generateByFiles(fileList);
                this.fileTree.updateFileTreeNode(projectFiles);
                this.jTabFrame.openTab(Alignment.NORTH_WEST, 0);
            }, GLOBAL_EXECUTOR);
        });

        fileTree.addFileOpenListener(projectFile -> {
            mainTabPane.addImageTab(projectFile.getFile());
        });
    }

}
