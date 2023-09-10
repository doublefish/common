package com.tt.common.util;

import lombok.var;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * FileUtils
 *
 * @author Shuang Yu
 */
public class FileUtils {

    private FileUtils() {
    }

    /**
     * convertToMultipart
     *
     * @param file file
     * @return MultipartFile
     */
    public static MultipartFile convertToMultipart(File file) {
        try (var input = new FileInputStream(file)) {
            return convertToMultipart(file.getName(), input);
        } catch (IOException e) {
            throw new IllegalArgumentException("复制内容发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * convertToMultipart
     *
     * @param name  name
     * @param bytes bytes
     * @return MultipartFile
     */
    public static MultipartFile convertToMultipart(String name, byte[] bytes) {
        try (var input = new ByteArrayInputStream(bytes)) {
            return convertToMultipart(name, input);
        } catch (IOException e) {
            throw new IllegalArgumentException("复制内容发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * convertToMultipart
     *
     * @param name  name
     * @param input input
     * @return MultipartFile
     */
    public static MultipartFile convertToMultipart(String name, InputStream input) {
        var item = new DiskFileItemFactory().createItem(name, ContentType.MULTIPART_FORM_DATA.getMimeType(), true, name);
        try (var output = item.getOutputStream()) {
            IOUtils.copy(input, output);
        } catch (IOException e) {
            throw new IllegalArgumentException("复制内容发生异常：" + e.getMessage(), e);
        }
        return new CommonsMultipartFile(item);
    }

    /**
     * 获取资源文件内容
     *
     * @param fileName fileName
     * @return String
     */
    public static String getResourcesFileContent(String fileName) throws IOException {
        var contents = getResourcesFileLines(fileName);
        if (contents == null) {
            return null;
        }
        return String.join("\n", contents);
    }

    /**
     * 获取资源文件内容（多行）
     *
     * @param fileName fileName
     * @return List<String>
     */
    public static List<String> getResourcesFileLines(String fileName) throws IOException {
        try (var inputStream = getResourcesFileInputStream(fileName)) {
            return IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
        }
    }

    /**
     * 获取资源文件流
     *
     * @param fileName fileName
     * @return InputStream
     */
    public static InputStream getResourcesFileInputStream(String fileName) {
        var classLoader = getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }

    /**
     * 获取资源路径
     *
     * @return String
     */
    public static String getResourcePath() {
        var classLoader = getClassLoader();
        var url = classLoader.getResource("");
        return url != null ? url.getPath() : null;
    }

    /**
     * 获取资源路径
     *
     * @return String
     */
    public static ClassLoader getClassLoader() {
        return SystemUtils.isLinux() ? Thread.currentThread().getContextClassLoader() : FileUtils.class.getClassLoader();
    }

}
