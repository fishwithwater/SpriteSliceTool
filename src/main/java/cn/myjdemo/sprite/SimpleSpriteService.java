package cn.myjdemo.sprite;

import cn.myjdemo.domain.Rect2;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author fishwithwater
 * @date 2024/7/19 9:46
 * @description
 **/
public class SimpleSpriteService implements SpriteService {

    private final int THREAD_NUM = 4;

    ExecutorService executor = new ThreadPoolExecutor(THREAD_NUM, THREAD_NUM, 1000L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    @Override
    public CompletableFuture<List<Rect2>> processSplitSpriteSheet(BufferedImage image) {
        SimpleSpriteSplicer simpleSpriteSplicer = new SimpleSpriteSplicer(image);
        return CompletableFuture.supplyAsync(() -> {
            try {
                return simpleSpriteSplicer.getPixelRGB();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }
}
