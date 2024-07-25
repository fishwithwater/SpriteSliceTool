package cn.myjdemo.sprite.godot;

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
 * @date 2024/7/19 13:22
 * @description from <a href="https://github.com/godotengine/godot/blob/ff8a2780ee777c2456ce42368e1065774c7c4c3f/editor/plugins/texture_region_editor_plugin.cpp#L759">Godot Region Editor</a>
 **/
public class GodotSpriteService implements SpriteService {

    @Override
    public CompletableFuture<List<Rect2>> processSplitSpriteSheet(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        GodotSpriteSliceRunner godotSpriteSliceRunner = new GodotSpriteSliceRunner();
        addProcessRunner(file, godotSpriteSliceRunner);
        return CompletableFuture.supplyAsync(() -> godotSpriteSliceRunner.run(image), EXECUTOR);
    }

}
