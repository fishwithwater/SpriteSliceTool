package cn.myjdemo.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author maoyingjie
 * @date 2024/7/25 15:04
 * @description
 **/
public class ThreadUtil {
    public static ExecutorService GLOBAL_EXECUTOR = Executors.newSingleThreadExecutor();
}
