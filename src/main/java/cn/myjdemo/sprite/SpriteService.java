package cn.myjdemo.sprite;

import cn.myjdemo.domain.Rect2;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author fishwithwater
 * @date 2024/7/19 9:45
 * @description
 **/
public interface SpriteService {

    CompletableFuture<List<Rect2>> processSplitSpriteSheet(BufferedImage image);

}
