package cn.myjdemo.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * @author fishwithwater
 * @date 2024/7/19 9:38
 * @description
 **/
@EqualsAndHashCode
@Data
public class Rect2 {

    private double x;

    private double y;

    private double width;

    private double height;

    public Rect2(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rect2(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    //判断坐标是否在矩形内
    public boolean contains(double x, double y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
    }

    //扩大矩形
    public Rect2 grow(double v) {
        Rect2 g = new Rect2(this.x, this.y, this.width, this.height);
        g.x -= v;
        g.y -= v;
        g.width += v * 2;
        g.height += v * 2;
        return g;
    }

    public void expandTo(double x, double y) {
        Point2D begin = new Point2D.Double(this.x, this.y);
        Point2D end = new Point2D.Double(this.x + this.width, this.y + this.height);
        if (x < begin.getX()) {
            begin.setLocation(x, begin.getY());
        }
        if (x > end.getX()) {
            end.setLocation(x, end.getY());
        }
        if (y < begin.getY()) {
            begin.setLocation(begin.getX(), y);
        }
        if (y > end.getY()) {
            end.setLocation(end.getX(), y);
        }
        this.x = begin.getX();
        this.y = begin.getY();
        this.width = end.getX() - begin.getX();
        this.height = end.getY() - begin.getY();
    }

    public boolean intersects(Rect2 p) {
        if(x >= (p.x + p.width)){
            return false;
        }
        if(y >= (p.y + p.height)){
            return false;
        }
        if(p.x >= (x + width)){
            return false;
        }
        if(p.y >= (y + height)){
            return false;
        }
        return true;
    }
}
