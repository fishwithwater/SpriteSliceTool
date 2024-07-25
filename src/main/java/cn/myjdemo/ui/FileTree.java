package cn.myjdemo.ui;

import cn.myjdemo.domain.ProjectFile;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author maoyingjie
 * @date 2024/7/22 17:35
 * @description
 **/
public class FileTree extends JTree {

    private List<Consumer<ProjectFile>> fileOpenListener = new ArrayList<>();

    public FileTree() {
        super();
        setModel(createTreeModel(null));
        setRootVisible(false);
        setLargeModel(true);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int clickCount = e.getClickCount();
                if (clickCount >= 2) {
                    Object value = getLastSelectedPathComponent();
                    if (value instanceof DefaultMutableTreeNode node) {
                        if (node.getUserObject() != null && node.getUserObject() instanceof ProjectFile userObject) {
                            if (userObject.getFile().isFile()) {
                                notifyFileOpenListener(userObject);
                            }
                        }
                    }
                }
            }
        });
    }

    public void updateFileTreeNode(List<ProjectFile> projectFileList) {
        List<MutableTreeNode> mutableTreeNodeList = projectFileList.stream().map(this::createTreeNodeByProjectFile).toList();
        if (mutableTreeNodeList.size() == 1) {
            setModel(new DefaultTreeModel(mutableTreeNodeList.getFirst()));
            setRootVisible(true);
        } else {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
            mutableTreeNodeList.forEach(root::add);
            setModel(new DefaultTreeModel(root));
            setRootVisible(false);
        }
        repaint();
    }

    private MutableTreeNode createTreeNodeByProjectFile(ProjectFile rootFile) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootFile);
        if (rootFile.getChildren() != null && !rootFile.getChildren().isEmpty()) {
            rootFile.getChildren().forEach(child -> {
                root.add(createTreeNodeByProjectFile(child));
            });
        }
        return root;
    }

    public void addFileOpenListener(Consumer<ProjectFile> listener) {
        fileOpenListener.add(listener);
    }

    public void removeFileOpenListener(Consumer<ProjectFile> listener) {
        fileOpenListener.remove(listener);
    }

    private void notifyFileOpenListener(ProjectFile projectFile) {
        fileOpenListener.forEach(listener -> listener.accept(projectFile));
    }
}
