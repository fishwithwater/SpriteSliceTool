package cn.myjdemo.ui;

import cn.myjdemo.domain.ProjectFile;
import cn.myjdemo.sprite.SpriteService;
import cn.myjdemo.sprite.SpriteUtil;
import com.github.weisj.darklaf.components.OverlayScrollPane;
import com.github.weisj.darklaf.components.tabframe.JTabFrame;
import com.github.weisj.darklaf.iconset.AllIcons;
import com.github.weisj.darklaf.util.Alignment;

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
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

import static cn.myjdemo.domain.ProjectFile.generateByFiles;
import static cn.myjdemo.util.ThreadUtil.GLOBAL_EXECUTOR;

/**
 * @author fishwithwater
 * @date 2024/7/18 16:28
 * @description
 **/
public class MainFrame extends JFrame {

    private final int WINDOW_WIDTH = 1000;
    private final int WINDOW_HEIGHT = 600;
    private final Timer timer = new Timer();
    private MainTabPane mainTabPane;
    private JLabel bottomBarLabel;
    private MenuBar menuBar;
    private FileTree fileTree;
    private JTabFrame jTabFrame;
    private LinkedList<File> imageList = new LinkedList<>();

    public MainFrame() throws HeadlessException {
        super();
        this.setTitle("Sprite Util");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        this.setResizable(false);

        addComponents();

        addListeners();

        addBackgroundThread();

        this.setVisible(true);
    }

    private void addBackgroundThread() {
        long processTaskInterval = 10L;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (imageList.isEmpty()) return;
                SpriteService spriteService = SpriteUtil.getSpriteService();
                for (File file : imageList) {
                    double process = spriteService.getSplitSpriteSheetProcess(file);
                    if (process < 0) {
                        continue;
                    }
                    System.out.println(file.getPath() + ":" + (process * 100));
                }
            }
        }, processTaskInterval, processTaskInterval);
    }

    void addComponents() {
        //MenuBar
        this.menuBar = new MenuBar();
        this.setJMenuBar(menuBar);

        //Layout Component
        JPanel layoutPanel = new JPanel();
        layoutPanel.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        layoutPanel.setLayout(new BorderLayout());

        //Image Previewer Component
        this.mainTabPane = new MainTabPane();
        mainTabPane.setMainFrameRef(this);

        //Bottom Bar Component
        JPanel bottomBarPanel = new JPanel();
        bottomBarLabel = new JLabel("点击Sprite后显示坐标");
        bottomBarPanel.add(bottomBarLabel);
        layoutPanel.add(bottomBarPanel, BorderLayout.SOUTH);

        //TabFrame
        jTabFrame = new JTabFrame();
        jTabFrame.setContent(mainTabPane);
        this.fileTree = new FileTree();
        OverlayScrollPane overlayScrollPane = new OverlayScrollPane(fileTree);
        jTabFrame.addTab(overlayScrollPane, "Project", AllIcons.Files.Folder.get(), Alignment.NORTH_WEST);
        jTabFrame.setAcceleratorAt(1, Alignment.NORTH_WEST, 0);
        layoutPanel.add(jTabFrame, BorderLayout.CENTER);

        add(layoutPanel);
    }

    void addListeners() {
        MainFrame frame = this;

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

    public JLabel getBottomBarLabel() {
        return bottomBarLabel;
    }
}
