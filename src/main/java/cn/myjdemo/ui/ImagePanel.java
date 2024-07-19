package cn.myjdemo.ui;

import cn.myjdemo.domain.Rect2;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author fishwithwater
 * @date 2024/7/19 10:26
 * @description
 **/
class ImagePanel extends JPanel {

    public static Color BG_COLOR = new Color(128, 128, 128);
    private final List<Rect2> rects = new ArrayList<>();
    private final List<Consumer<Rect2>> clickRect2Listeners = new ArrayList<>();
    BufferedImage mSrcBufferedImg = null;
    // 记录最初原始坐标系，用于清除背景
    AffineTransform mOriginTransform;
    BufferedImage mViewBufferImg;
    Graphics2D mViewG2d;
    private double mScale = 1.0;
    private double mCurX = 0;
    private double mCurY = 0;
    private double mStartX = 0;
    private double mStartY = 0;
    private double mTranslateX = 0;
    private double mTranslateY = 0;
    private Integer selectedRectIndex = null;

    public ImagePanel() {

        // 启用双缓存
        setDoubleBuffered(true);

        this.addMouseWheelListener(e -> {
            if (mViewG2d == null) {
                return;
            }
            mCurX = e.getX();
            mCurY = e.getY();

            int notches = e.getWheelRotation();
            if (notches < 0) {
                // 滚轮向上，放大画布
                mScale = 1.1;

            } else {
                // 滚轮向下，缩小画布
                mScale = 0.9;
            }

            Point imagePoint = internalGetImagePoint(e.getX(), e.getY());
            int imageX = imagePoint.x;
            int imageY = imagePoint.y;

            double tralateX = mScale * imageX - imageX;
            double tralateY = mScale * imageY - imageY;

            mViewG2d.scale(mScale, mScale);
            mViewG2d.translate(-tralateX / mScale, -tralateY / mScale); // 图片方大，就需要把坐标往左移动，移动的尺度是要考虑缩放的
            // 先恢复一下原始状态，保证清空的坐标是全部，执行清空，然后再切会来
            AffineTransform temp = mViewG2d.getTransform();
            mViewG2d.setTransform(mOriginTransform);
            mViewG2d.clearRect(0, 0, mViewBufferImg.getWidth(), mViewBufferImg.getHeight());
            mViewG2d.setTransform(temp);

            mViewG2d.drawImage(mSrcBufferedImg, 0, 0, null);
            repaint(); // 重新绘制画布
        });

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                mStartX = e.getX();
                mStartY = e.getY();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    AffineTransform inverse = mViewG2d.getTransform().createInverse();
                    Point originalPoint = new Point(e.getX(), e.getY());
                    inverse.transform(originalPoint, originalPoint);
                    for (int i = 0; i < rects.size(); i++) {
                        Rect2 rect = rects.get(i);
                        if (rect.contains((int) originalPoint.getX(), (int) originalPoint.getY())) {
                            notifyClickRect2Listener(rect);
                            selectedRectIndex = i;
                            repaint();
                        }
                    }
                } catch (NoninvertibleTransformException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        this.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {

                if (mViewG2d == null) {
                    return;
                }

                mCurX = e.getX();
                mCurY = e.getY();

                // 平移坐标，也是相对于变换后的坐标系而言的，所以
                double scaleX = mViewG2d.getTransform().getScaleX();
                double scaleY = mViewG2d.getTransform().getScaleY();

                // 解决方案，把移动 ，全部在原始坐标系上做，也就是最后绘制缓冲区的时候，drawimage(transX,transY)
                mTranslateX = (mCurX - mStartX) / scaleX;
                mTranslateY = (mCurY - mStartY) / scaleY;

                // 自身就是累计的
                mViewG2d.translate(mTranslateX, mTranslateY);

                mStartX = mCurX;
                mStartY = mCurY;

                // 先恢复一下原始状态，保证清空的坐标是全部，执行清空，然后再切会来
                AffineTransform temp = mViewG2d.getTransform();
                mViewG2d.setTransform(mOriginTransform);
                mViewG2d.clearRect(0, 0, mViewBufferImg.getWidth(), mViewBufferImg.getHeight());
                mViewG2d.setTransform(temp);

                mViewG2d.drawImage(mSrcBufferedImg, 0, 0, null);
                repaint();
            }
        });

    }

    void refreshView() {
        clearBuffer(mViewG2d, mOriginTransform, mViewBufferImg);
        mViewG2d.drawImage(mSrcBufferedImg, 0, 0, null);
        repaint();
    }

    void clearBuffer(Graphics2D g2d, AffineTransform org, BufferedImage bufImg) {
//			将保存的测量数据，重新在经过变换后的坐标系上进行绘制
        // 先恢复一下原始状态，保证清空的坐标是全部，执行清空，然后再切会来
        AffineTransform temp = g2d.getTransform();
        g2d.setTransform(org);
        g2d.clearRect(0, 0, bufImg.getWidth(), bufImg.getHeight());
        g2d.setTransform(temp);
    }

    public void updateImage(BufferedImage srcImage) {
        mSrcBufferedImg = srcImage;
        rects.clear();
        selectedRectIndex = null;
        mViewBufferImg = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        mViewG2d = mViewBufferImg.createGraphics();
        mViewG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        mViewG2d.setBackground(BG_COLOR);
        mOriginTransform = mViewG2d.getTransform();
        refreshView();
    }

    public void updateRects(List<Rect2> rectList) {
        rects.clear();
        selectedRectIndex = null;
        rects.addAll(rectList);
        refreshView();
    }

    private Point internalGetImagePoint(double mouseX, double mouseY) {

        // 不管是先平移后缩放还是先缩放后平移，都以 先减 再缩放的方式可以获取正确
        double rawTranslateX = mViewG2d.getTransform().getTranslateX();
        double rawTranslateY = mViewG2d.getTransform().getTranslateY();
        // 获取当前的 Scale Transform
        double scaleX = mViewG2d.getTransform().getScaleX();
        double scaleY = mViewG2d.getTransform().getScaleY();

        // 管是先平移后缩放还是先缩放后平移，都以 先减 再缩放的方式可以获取正确
        int imageX = (int) ((mouseX - rawTranslateX) / scaleX);
        int imageY = (int) ((mouseY - rawTranslateY) / scaleY);

        return new Point(imageX, imageY);
    }

    public void resetScale() {
        // 恢复到1.0 缩放，0,0 左上角对齐
        mCurX = 0;
        mCurY = 0;
        mScale = 1.0;
        mViewG2d.setTransform(mOriginTransform);
        mViewG2d.clearRect(0, 0, mViewBufferImg.getWidth(), mViewBufferImg.getHeight());
        mViewG2d.drawImage(mSrcBufferedImg, 0, 0, null);
        repaint(); // 重新绘制画布
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        if (mViewBufferImg == null) {
            return;
        }
        // 如果有多个“图层”要注意图层的顺序
        Graphics2D g2d = ((Graphics2D) g);
        if (!rects.isEmpty()) {
            mViewG2d.setColor(Color.RED);
            for (int i = 0; i < rects.size(); i++) {
                if (selectedRectIndex != null && selectedRectIndex == i) {
                    mViewG2d.setColor(Color.BLUE);
                }
                Rect2 rect = rects.get(i);
                mViewG2d.drawRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
                if (selectedRectIndex != null && selectedRectIndex == i) {
                    mViewG2d.setColor(Color.RED);
                }
            }
        }
        g2d.drawImage(mViewBufferImg, 0, 0, null);
    }

    public void addClickRect2Listener(Consumer<Rect2> listener) {
        clickRect2Listeners.add(listener);
    }

    public void removeClickRect2Listener(Consumer<Rect2> listener) {
        clickRect2Listeners.remove(listener);
    }

    public void notifyClickRect2Listener(Rect2 rect) {
        clickRect2Listeners.forEach(listener -> listener.accept(rect));
    }
}