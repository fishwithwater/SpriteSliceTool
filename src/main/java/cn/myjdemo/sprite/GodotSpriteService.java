package cn.myjdemo.sprite;

import cn.myjdemo.domain.Rect2;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author fishwithwater
 * @date 2024/7/19 13:22
 * @description from <a href="https://github.com/godotengine/godot/blob/ff8a2780ee777c2456ce42368e1065774c7c4c3f/editor/plugins/texture_region_editor_plugin.cpp#L759">Godot Region Editor</a>
 **/
public class GodotSpriteService implements SpriteService {

    ExecutorService executor = Executors.newSingleThreadExecutor();

    public static boolean isPixelOpaque(int pixel) {
        int alpha = (pixel >> 24) & 0xFF;
        return alpha > 1;
    }

    @Override
    public CompletableFuture<List<Rect2>> processSplitSpriteSheet(BufferedImage image) {

        return CompletableFuture.supplyAsync(() -> {
            List<Rect2> autoSliceCache = new ArrayList<>();

            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    if (isPixelOpaque(image.getRGB(x, y))) {
                        boolean found = false;
                        Rect2 expandedRect = null;
                        for (Rect2 rect : autoSliceCache) {
                            Rect2 grown = rect.grow(1.5); // 注意：grow方法需要自定义实现
                            if (grown.contains(x, y)) {
                                rect.expandTo(x, y);
                                rect.expandTo(x + 1, y + 1);
                                x = (int) (rect.getX() + rect.getWidth() - 1); // 注意：Rectangle的属性名可能不同，这里假设使用x和width
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
        }, executor);
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
