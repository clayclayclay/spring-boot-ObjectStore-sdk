package com.max.spring_boot_simple_test.simple.util;

import java.util.Date;

public class CommonUtil {
    
    public static String getKeyName(String bucketName, String userId, String fileName) {
        StringBuffer sb = new StringBuffer();
        long now = new Date().getTime();
        return sb.append(bucketName + "-").append(userId + "-").append(now + "-").append(fileName).toString();
    }
}
