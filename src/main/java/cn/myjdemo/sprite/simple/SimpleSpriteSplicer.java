package cn.myjdemo.sprite.simple;

import cn.myjdemo.domain.Rect2;
import cn.myjdemo.sprite.SpriteSliceRunner;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fishwithwater
 * @date 2024/7/19 11:10
 * @description from <a href="https://github.com/var-rain/SpriteSplit/blob/master/src/com/fx/software/spritesplit/utili/ReaderImage.java">SpriteSplit</a>
 * @deprecated too slow and not support get process
 **/
@Deprecated
public class SimpleSpriteSplicer implements SpriteSliceRunner {
    private final BufferedImage image;
    private final int width;
    private final int height;
    private final int minx;
    private final int miny;
    private final List<Rect2> spriteItemList = new ArrayList<>();
    private List<int[]> up;
    private List<int[]> down;
    private List<int[]> left;
    private List<int[]> right;
    private int total = 0;
    private int current = 0;

    public SimpleSpriteSplicer(BufferedImage image) {
        this.image = image;
        width = image.getWidth();
        height = image.getHeight();
        minx = image.getMinX();
        miny = image.getMinY();
        total = (width - minx - 1) * (height - miny - 1);
        setAlphaLine();
    }

    @Override
    public List<Rect2> run(BufferedImage image) {
        return getPixelRGB();
    }

    @Override
    public double getProcess() {
        if (total == 0 || current == 0) {
            return 0;
        }
        // wrong process
        return current * 1.0 / total;
    }

    public List<Rect2> getPixelRGB() {
        while (true) {
            if (readPixel() == -1) {
                return spriteItemList;
            }
        }
    }

    private int readPixel() {
        for (int x = minx; x < width; x++) {
            for (int y = miny; y < height; y++) {
                current = (height - miny) * (x - minx - 1) + (y - miny);
                int pixel = image.getRGB(x, y);
                if ((pixel >>> 24) != 0) {
                    initList(x, y);
                    check(right, up, down, left);
                    int img_x = up.getFirst()[0];
                    int img_y = up.getFirst()[1];
                    int img_w = up.getLast()[0] - up.getFirst()[0];
                    int img_h = down.getLast()[1] - up.getFirst()[1];
                    Rect2 spriteItem = new Rect2(img_x, img_y, img_w, img_h);
                    spriteItemList.add(spriteItem);
                    setAlpha(img_x, img_y, up.getLast()[0], down.getLast()[1]);
                    removeList();
                    return 1;
                }
            }
        }
        return -1;
    }

    private void initList(int x, int y) {
        List<int[]> up = new ArrayList<>();
        List<int[]> down = new ArrayList<>();
        List<int[]> left = new ArrayList<>();
        List<int[]> right = new ArrayList<>();
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        up.add(new int[]{x, y});
        up.add(new int[]{up.getLast()[0] + 1, y});
        up.add(new int[]{up.getLast()[0] + 1, y});
        left.add(new int[]{x, y});
        left.add(new int[]{x, left.getLast()[1] + 1});
        left.add(new int[]{x, left.getLast()[1] + 1});
        down.add(left.getLast());
        down.add(new int[]{down.getLast()[0] + 1, down.getLast()[1]});
        down.add(new int[]{down.getLast()[0] + 1, down.getLast()[1]});
        right.add(up.getLast());
        right.add(new int[]{right.getLast()[0], right.getLast()[1] + 1});
        right.add(new int[]{right.getLast()[0], right.getLast()[1] + 1});
    }

    private void removeList() {
        up.clear();
        down.clear();
        left.clear();
        right.clear();
    }

    private void add_up() {
        left.addFirst(new int[]{left.getFirst()[0], left.getFirst()[1] - 1});
        right.addFirst(new int[]{right.getFirst()[0], right.getFirst()[1] - 1});
        for (int[] ints : up) {
            ints[1] = ints[1] - 1;
        }
    }

    private void add_down() {
        left.add(new int[]{left.getLast()[0], left.getLast()[1] + 1});
        right.add(new int[]{right.getLast()[0], right.getLast()[1] + 1});
        for (int[] ints : down) {
            ints[1] = ints[1] + 1;
        }
    }

    private void add_left() {
        up.addFirst(new int[]{up.getFirst()[0] - 1, up.getFirst()[1]});
        down.addFirst(new int[]{down.getFirst()[0] - 1, down.getFirst()[1]});
        for (int[] ints : left) {
            ints[0] = ints[0] - 1;
        }
    }

    private void add_right() {
        up.add(new int[]{up.getLast()[0] + 1, up.getLast()[1]});
        down.add(new int[]{down.getLast()[0] + 1, down.getLast()[1]});
        for (int[] ints : right) {
            ints[0] = ints[0] + 1;
        }
    }

    private void readerRightLine(List<int[]> right) {
        for (int[] ints : right) {
            if ((image.getRGB(ints[0], ints[1]) >>> 24) != 0) {
                if (ints[0] != width) {
                    add_right();
                    readerRightLine(right);
                }
            }
        }
    }

    private void readerUpLine(List<int[]> up) {
        for (int[] ints : up) {
            if ((image.getRGB(ints[0], ints[1]) >>> 24) != 0) {
                if (ints[1] != 0) {
                    add_up();
                    readerUpLine(up);
                }
            }
        }
    }

    private void readerDownLine(List<int[]> down) {
        for (int[] ints : down) {
            if ((image.getRGB(ints[0], ints[1]) >>> 24) != 0) {
                if (ints[1] != height) {
                    add_down();
                    readerDownLine(down);
                }
            }
        }
    }

    private void readerLeftLine(List<int[]> left) {
        for (int[] ints : left) {
            if ((image.getRGB(ints[0], ints[1]) >>> 24) != 0) {
                if (ints[0] != 0) {
                    add_left();
                    readerLeftLine(left);
                }
            }
        }
    }

    private void check_line(List<int[]> right, List<int[]> up, List<int[]> down, List<int[]> left) {
        readerRightLine(right);
        readerUpLine(up);
        readerDownLine(down);
        readerLeftLine(left);
    }

    private void check(List<int[]> right, List<int[]> up, List<int[]> down, List<int[]> left) {
        check_line(right, up, down, left);
        check_line(right, up, down, left);
        check_line(right, up, down, left);
    }

    private void setAlpha(int startX, int startY, int width, int height) {
        for (int x = startX; x < width; x++) {
            for (int y = startY; y < height; y++) {
                image.setRGB(x, y, 0);
            }
        }
    }

    private void setAlphaLine() {
        for (int i = 0; i < width; i++) {
            image.setRGB(i, 0, 0);
            image.setRGB(i, height - 1, 0);

        }
        for (int i = 0; i < height; i++) {
            image.setRGB(0, i, 0);
            image.setRGB(width - 1, i, 0);
        }
    }
}
