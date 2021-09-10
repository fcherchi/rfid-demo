package com.fcherchi.demo.fileutils;

import com.fcherchi.demo.RfidFillingApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class FileCopier {

    public static void copyFile(String path) throws IOException {
        File destinationFile = new File(path);

        if (!destinationFile.exists()) {
            InputStream inputStream = RfidFillingApplication.class.getClassLoader().getResourceAsStream(path);

            FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, read);
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}