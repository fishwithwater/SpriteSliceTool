package cn.myjdemo.sprite.simple;

import cn.myjdemo.domain.Rect2;
import cn.myjdemo.sprite.SpriteService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author fishwithwater
 * @date 2024/7/19 9:46
 * @description
 * @deprecated too slow and not support get process
 **/
@Deprecated
public class SimpleSpriteService implements SpriteService {

    @Override
    public CompletableFuture<List<Rect2>> processSplitSpriteSheet(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        SimpleSpriteSplicer simpleSpriteSplicer = new SimpleSpriteSplicer(image);
        addProcessRunner(file, simpleSpriteSplicer);
        return CompletableFuture.supplyAsync(() -> simpleSpriteSplicer.run(image), EXECUTOR);
    }
}
