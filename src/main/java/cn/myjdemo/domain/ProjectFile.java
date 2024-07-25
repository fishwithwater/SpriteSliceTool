package cn.myjdemo.domain;

import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author maoyingjie
 * @date 2024/7/25 9:34
 * @description
 **/
@Data
public class ProjectFile {

    private File file;

    private boolean isSelected;

    private List<ProjectFile> children;


    public static List<ProjectFile> generateByFiles(List<File> fileList) {
        return fileList.stream().map(file -> {
            ProjectFile projectFile = new ProjectFile();
            projectFile.setFile(file);
            projectFile.setChildren(generateByFolder(file));
            return projectFile;
        }).collect(Collectors.toList());
    }

    private static List<ProjectFile> generateByFolder(File folder) {
        if (folder.isFile()) return new ArrayList<>();
        File[] files = folder.listFiles(file -> file.isDirectory() || file.getName().endsWith(".png"));
        if (files == null || files.length == 0) return new ArrayList<>();
        return generateByFiles(Arrays.asList(files));
    }

    @Override
    public String toString() {
        return file == null ? "null" : file.getName();
    }

}
