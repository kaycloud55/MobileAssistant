package com.kaycloud.mobileassistant.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author: kaycloud
 * @date: 2017/10/20.
 */

public class FileUtils {

    public static void wirteFile(InputStream inputStream, File file) throws IOException {
        byte[] buf = new byte[2048];
        int len = 0;
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        FileOutputStream fos = new FileOutputStream(file);
        while ((len = bis.read(buf)) != -1) {
            fos.write(buf, 0, len);
        }
        fos.flush();
        fos.close();
        bis.close();
        inputStream.close();
    }
}
