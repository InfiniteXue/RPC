package rpc.common;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public final class ClassUtil {

    /**
     * 获取指定路径下的类
     */
    public static Set<Class<?>> getClasses(String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        try {
            // 读取指定路径下的文件资源
            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url.getProtocol().equalsIgnoreCase("file")) {
                    addClasses(url.getPath(), packageName, classes);
                }
            }
        } catch (IOException e) {
            log.error("An exception occurred when getting the path of {} classes.", packageName, e);
            throw new RuntimeException("failed to get classes", e);
        }
        return classes;
    }

    /**
     * 获取当前线程的类加载器
     */
    private static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * @param filePath    文件路径（绝对路径）
     * @param packageName 包名
     */
    private static void addClasses(String filePath, String packageName, Set<Class<?>> classes) {
        // 过滤指定路径下的class文件和文件夹
        File[] files = new File(filePath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
            }
        });
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()) {
                // 加载class文件
                String className = packageName + "." + fileName.substring(0, fileName.lastIndexOf("."));
                classes.add(loadClass(className, false));
            } else {
                // 递归解析文件夹
                addClasses(filePath + "/" + fileName, packageName + "." + fileName, classes);
            }
        }
    }

    /**
     * 加载指定类
     */
    private static Class<?> loadClass(String className, boolean initialize) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className, initialize, getClassLoader());
        } catch (ClassNotFoundException e) {
            log.error("An exception occurred when loading {}.", className, e);
            throw new RuntimeException("failed to load class", e);
        }
        return clazz;
    }

}
