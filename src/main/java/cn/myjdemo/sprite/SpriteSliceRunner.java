package cn.myjdemo.sprite;

import cn.myjdemo.domain.Rect2;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * @author maoyingjie
 * @date 2024/7/22 9:22
 * @description
 **/
public interface SpriteSliceRunner extends ProcessRunner {

    List<Rect2> run(BufferedImage image);

}
