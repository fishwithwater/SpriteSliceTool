package cn.myjdemo.sprite;

import cn.myjdemo.domain.Rect2;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author fishwithwater
 * @date 2024/7/19 9:45
 * @description
 **/
public interface SpriteService {

    ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    Map<String, ProcessRunner> PROCESS_RUNNER_MAP = new HashMap<>();

    CompletableFuture<List<Rect2>> processSplitSpriteSheet(File file) throws IOException;

    default void addProcessRunner(File file, ProcessRunner processRunner) {
        PROCESS_RUNNER_MAP.put(file.getPath(), processRunner);
    }

    default double getSplitSpriteSheetProcess(File file) {
        ProcessRunner processRunner = PROCESS_RUNNER_MAP.get(file.getPath());
        if (processRunner == null) {
            return -1;
        }
        return processRunner.getProcess();
    }
}
