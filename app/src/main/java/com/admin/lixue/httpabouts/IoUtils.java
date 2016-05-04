package com.admin.lixue.httpabouts;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by lixue on 16/5/3.
 */
public class IoUtils {
    public static void closeQuitly(Closeable closeable){
        if (closeable != null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
