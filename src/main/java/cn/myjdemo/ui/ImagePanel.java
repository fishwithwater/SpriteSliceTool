package cn.myjdemo.ui;

import cn.myjdemo.domain.Rect2;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fishwithwater
 * @date 2024/7/19 9:21
 * @description
 **/
public class ImagePanel extends JPanel {

    private Image image;

    private Point clickPoint;

    private final AffineTransform transform = new AffineTransform();

    private final List<Rect2> rects = new ArrayList<>();

    boolean dragging = false;

    public ImagePanel(Image image) {
        this.image = image;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                clickPoint = e.getPoint();
                System.out.println(clickPoint.getX() + "," + clickPoint.getY());
                // 创建逆变换
                AffineTransform inverseTransform = null;
                try {
                    inverseTransform = transform.createInverse();
                } catch (NoninvertibleTransformException ex) {
                    // 处理不可逆的情况，通常不会发生
                    ex.printStackTrace();
                }

                // 将点击点转换回图像的原始坐标
                Point2D originalPoint = new Point2D.Double(clickPoint.x, clickPoint.y);
                inverseTransform.transform(originalPoint, originalPoint);

                System.out.println("原始图像上的点击点：" + originalPoint.getX() + "," + originalPoint.getY());

            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                dragging = true;
                int dx = e.getX() - clickPoint.x;
                int dy = e.getY() - clickPoint.y;
                transform.translate(dx, dy);
                clickPoint = e.getPoint();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (dragging) {
                    dragging = false;
                    int dx = e.getX() - clickPoint.x;
                    int dy = e.getY() - clickPoint.y;
                    transform.translate(dx, dy);
                }
            }
        });

        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double scale = 1.0 - e.getPreciseWheelRotation() / 10.0;
                transform.scale(scale, scale);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setTransform(transform);
        g2d.drawImage(image, 0, 0, this);
        if (!rects.isEmpty()) {
            g2d.setColor(Color.RED);
            rects.forEach(rect -> {
                g2d.drawRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
            });
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(this), image.getHeight(this));
    }

    public void setImage(Image image) {
        this.image = image;
        rects.clear();
        transform.setToIdentity();
        repaint();
    }

    public void setRects(List<Rect2> rect2List) {
        rects.clear();
        rects.addAll(rect2List);
        repaint();
    }
}
