package com.tt.common.util;

import lombok.var;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;

/**
 * HttpUtils
 *
 * @author Shuang Yu
 */
public class HttpUtils {

    private HttpUtils() {
    }

    /**
     * 下载文件
     *
     * @param urlString urlString
     * @param file      file
     * @param timeout   timeout
     * @throws IOException IOException
     */
    public static void downLoad(String urlString, File file, int timeout) throws IOException {
        var url = new URL(urlString);
        var conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(timeout);
        // 防止屏蔽程序抓取而返回403错误
        // conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36 Edg/100.0.1185.50");

        // 得到输入流
        var is = conn.getInputStream();
        var buffer = readInputStream(is);
        var fos = new FileOutputStream(file);
        fos.write(buffer);
        fos.close();
        is.close();
    }

    /**
     * 下载文件
     *
     * @param urlString urlString
     * @param file      file
     * @throws IOException IOException
     */
    public static void downLoad(String urlString, File file) throws IOException {
        downLoad(urlString, file, 3000);
    }

    /**
     * 下载文件
     *
     * @param urlString urlString
     * @param fileName  fileName
     * @param savePath  savePath
     * @throws IOException IOException
     */
    public static File downLoad(String urlString, String fileName, String savePath) throws IOException {
        // 文件保存位置
        var saveDir = new File(savePath);
        if (!saveDir.exists() && !saveDir.mkdir()) {
            throw new RuntimeException("创建目录失败");
        }
        var file = new File(saveDir + File.separator + fileName);
        downLoad(urlString, file);
        return file;
    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream inputStream
     * @return buffer
     * @throws IOException IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        var buffer = new byte[1024];
        var len = 0;
        var bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    /**
     * 下载文件
     *
     * @param urlString urlString
     * @param name      文件名
     */
    public static void downloadUsingStream(String urlString, String name) throws IOException {
        var url = new URL(urlString);
        var bis = new BufferedInputStream(url.openStream());
        var fis = new FileOutputStream(name);
        var size = 1024;
        var buffer = new byte[size];
        var len = 0;
        while ((len = bis.read(buffer, 0, size)) != -1) {
            fis.write(buffer, 0, len);
        }
        fis.close();
        bis.close();
    }

    /**
     * 下载文件
     *
     * @param urlString urlString
     * @param name      文件名
     */
    public static void downloadUsingNIO(String urlString, File name) throws IOException {
        var url = new URL(urlString);
        var rbc = Channels.newChannel(url.openStream());
        var fos = new FileOutputStream(name);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

}
