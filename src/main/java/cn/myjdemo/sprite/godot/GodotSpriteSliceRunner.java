package cn.myjdemo.sprite.godot;

import cn.myjdemo.domain.Rect2;
import cn.myjdemo.sprite.SpriteSliceRunner;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author maoyingjie
 * @date 2024/7/22 9:24
 * @description
 **/
public class GodotSpriteSliceRunner implements SpriteSliceRunner {

    private int total = 0;

    private int current = 0;

    public static boolean isPixelOpaque(int pixel) {
        int alpha = (pixel >> 24) & 0xFF;
        return alpha > 1;
    }

    @Override
    public double getProcess() {
        if (total == 0 || current == 0) {
            return 0;
        }
        return current * 1.0 / total;
    }

    @Override
    public List<Rect2> run(BufferedImage image) {
        this.total = (image.getHeight() - 1) * (image.getWidth() - 1);
        List<Rect2> autoSliceCache = new ArrayList<>();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                current = (y - 1) * image.getWidth() + x;
                if (isPixelOpaque(image.getRGB(x, y))) {
                    boolean found = false;
                    Rect2 expandedRect = null;
                    for (Rect2 rect : autoSliceCache) {
                        Rect2 grown = rect.grow(1.5);
                        if (grown.contains(x, y)) {
                            rect.expandTo(x, y);
                            rect.expandTo(x + 1, y + 1);
                            x = (int) (rect.getX() + rect.getWidth() - 1);
                            expandedRect = rect;
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        mergeOverlappingRectangles(expandedRect, autoSliceCache);
                    } else {
                        autoSliceCache.add(new Rect2(x, y, 1, 1));
                    }
                }
            }
        }
        return autoSliceCache;
    }

    private void mergeOverlappingRectangles(Rect2 expandedRect, List<Rect2> autoSliceCache) {
        boolean merged = true;
        while (merged) {
            merged = false;
            ArrayList<Rect2> toRemove = new ArrayList<>();
            for (Rect2 otherRect : autoSliceCache) {
                if (expandedRect.equals(otherRect)) {
                    continue;
                }
                if (expandedRect.grow(1).intersects(otherRect)) {
                    expandedRect.expandTo(otherRect.getX(), otherRect.getY());
                    expandedRect.expandTo(otherRect.getX() + otherRect.getWidth(), otherRect.getY() + otherRect.getHeight());
                    toRemove.add(otherRect);
                    merged = true;
                }
            }
            autoSliceCache.removeAll(toRemove);
        }
    }

}
