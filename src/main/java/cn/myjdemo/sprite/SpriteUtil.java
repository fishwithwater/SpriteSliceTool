package cn.myjdemo.sprite;

import cn.myjdemo.sprite.godot.GodotSpriteService;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fishwithwater
 * @date 2024/7/19 9:46
 * @description
 **/
public class SpriteUtil {

    public static Map<Class<? extends SpriteService>, SpriteService> SERVICE_MAP = new HashMap<>();

    public static SpriteService getSpriteService() {
        return getSpriteService(GodotSpriteService.class);
    }

    public static synchronized SpriteService getSpriteService(Class<? extends SpriteService> clazz) {
        if (clazz == null) {
            clazz = GodotSpriteService.class;
        }
        try {
            if (!SERVICE_MAP.containsKey(clazz)) {
                SERVICE_MAP.put(clazz, clazz.getConstructor().newInstance());
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return SERVICE_MAP.get(clazz);
    }

}
