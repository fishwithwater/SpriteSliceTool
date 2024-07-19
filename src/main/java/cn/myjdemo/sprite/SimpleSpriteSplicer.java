package cn.myjdemo.sprite;

import cn.myjdemo.domain.Rect2;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fishwithwater
 * @date 2024/7/19 11:10
 * @description from <a href="https://github.com/var-rain/SpriteSplit/blob/master/src/com/fx/software/spritesplit/utili/ReaderImage.java">SpriteSplit</a>
 **/
public class SimpleSpriteSplicer {
    private BufferedImage image;
    private int width;
    private int height;
    private int minx;
    private int miny;
    private List<int[]> up;
    private List<int[]> down;
    private List<int[]> left;
    private List<int[]> right;

    private List<Rect2> spriteItemList = new ArrayList<>();

    public SimpleSpriteSplicer(BufferedImage image) {
        this.image = image;
        width = image.getWidth();
        height = image.getHeight();
        minx = image.getMinX();
        miny = image.getMinY();
        setAlphaLine();
    }

    public List<Rect2> getPixelRGB() throws IOException {
        while (true) {
            if (readPixel() == -1) {
                return spriteItemList;
            }
        }
    }

    private int readPixel() {
        for (int x = minx; x < width; x++) {
            for (int y = miny; y < height; y++) {
                int pixel = image.getRGB(x, y);
                if ((pixel >>> 24) != 0) {
                    initList(x, y);
                    check(right, up, down, left);
                    int img_x = up.get(0)[0];
                    int img_y = up.get(0)[1];
                    int img_w = up.get(up.size() - 1)[0] - up.get(0)[0];
                    int img_h = down.get(down.size() - 1)[1] - up.get(0)[1];
//                    outImage(img_x, img_y, img_w, img_h);
                    Rect2 spriteItem = new Rect2(img_x, img_y, img_w, img_h);
                    spriteItemList.add(spriteItem);
                    setAlpha(img_x, img_y, up.get(up.size() - 1)[0], down.get(down.size() - 1)[1]);
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
        up.add(new int[]{up.get(up.size() - 1)[0] + 1, y});
        up.add(new int[]{up.get(up.size() - 1)[0] + 1, y});
        left.add(new int[]{x, y});
        left.add(new int[]{x, left.get(left.size() - 1)[1] + 1});
        left.add(new int[]{x, left.get(left.size() - 1)[1] + 1});
        down.add(left.get(left.size() - 1));
        down.add(new int[]{down.get(down.size() - 1)[0] + 1, down.get(down.size() - 1)[1]});
        down.add(new int[]{down.get(down.size() - 1)[0] + 1, down.get(down.size() - 1)[1]});
        right.add(up.get(up.size() - 1));
        right.add(new int[]{right.get(right.size() - 1)[0], right.get(right.size() - 1)[1] + 1});
        right.add(new int[]{right.get(right.size() - 1)[0], right.get(right.size() - 1)[1] + 1});
    }

    private void removeList() {
        for (int i = 0; i < up.size(); i++) up.remove(i);
        for (int i = 0; i < down.size(); i++) down.remove(i);
        for (int i = 0; i < left.size(); i++) left.remove(i);
        for (int i = 0; i < right.size(); i++) right.remove(i);
    }

    private void add_up() {
        left.add(0, new int[]{left.get(0)[0], left.get(0)[1] - 1});
        right.add(0, new int[]{right.get(0)[0], right.get(0)[1] - 1});
        for (int[] ints : up) {
            ints[1] = ints[1] - 1;
        }
    }

    private void add_down() {
        left.add(new int[]{left.get(left.size() - 1)[0], left.get(left.size() - 1)[1] + 1});
        right.add(new int[]{right.get(right.size() - 1)[0], right.get(right.size() - 1)[1] + 1});
        for (int[] ints : down) {
            ints[1] = ints[1] + 1;
        }
    }

    private void add_left() {
        up.add(0, new int[]{up.get(0)[0] - 1, up.get(0)[1]});
        down.add(0, new int[]{down.get(0)[0] - 1, down.get(0)[1]});
        for (int[] ints : left) {
            ints[0] = ints[0] - 1;
        }
    }

    private void add_right() {
        up.add(new int[]{up.get(up.size() - 1)[0] + 1, up.get(up.size() - 1)[1]});
        down.add(new int[]{down.get(down.size() - 1)[0] + 1, down.get(down.size() - 1)[1]});
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
