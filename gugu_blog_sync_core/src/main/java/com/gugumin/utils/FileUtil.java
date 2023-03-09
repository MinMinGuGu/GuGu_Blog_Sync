package com.gugumin.utils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * The type File util.
 *
 * @author minmin
 * @date 2023 /03/08
 */
public class FileUtil {
    private FileUtil() {
    }

    /**
     * Write.
     *
     * @param path    the path
     * @param context the context
     */
    public static void write(Path path, String context) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path.toFile())) {
            fileOutputStream.write(context.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read string.
     *
     * @param path the path
     * @return the string
     */
    public static String read(Path path) {
        try (BufferedReader inputStream = new BufferedReader(new FileReader(path.toFile()))) {
            StringBuilder result = new StringBuilder();
            inputStream.lines().forEach(line -> result.append(line).append(System.lineSeparator()));
            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
