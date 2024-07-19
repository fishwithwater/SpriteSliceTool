package cn.myjdemo.sprite;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fishwithwater
 * @date 2024/7/19 9:46
 * @description
 **/
public class SpriteUtil {

    public static Map<Class<? extends SpriteService>, SpriteService> map = new HashMap<>();

    public static SpriteService getSpriteService() {
        return getSpriteService(GodotSpriteService.class);
    }

    public static synchronized SpriteService getSpriteService(Class<? extends SpriteService> clazz) {
        if(clazz == null){
            clazz = GodotSpriteService.class;
        }
        try {
            if (!map.containsKey(clazz)) {
                map.put(clazz, clazz.getConstructor().newInstance());
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return map.get(clazz);
    }

}
