package com.grs.utils;

import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.codec.binary.StringUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class StringUtil {
    public static Boolean isValidString(String str) {
        return str != null && str.trim().length() > 0;
    }

    public static Boolean isValidUnsignedNumber(String str) {
        return str.matches("\\d+(\\.\\d+)?");
    }

    public static Long decodeOfficeIdOnDashboardDrillDown(String encodedOfficeId) {
        encodedOfficeId = encodedOfficeId.substring(20);
        String decodedOfficeId = StringUtils.newStringUtf8(Base64.decodeBase64(encodedOfficeId));
        return Long.parseLong(decodedOfficeId);

    }

    public static StringBuilder replaceAll(StringBuilder builder, String src, String to) {
        int index = builder.indexOf(src);
        while (index != -1) {
            builder.replace(index, index + src.length(), to);
            index += to.length(); // Move to the end of the replacement
            index = builder.indexOf(src, index);
        }
        return builder;
    }

    public static String decodeToUtf8(String id) {
        try{
            if (id.equals("%23")){
                return URLDecoder.decode(id, String.valueOf(StandardCharsets.UTF_8));
            } else {
                return id;
            }
        } catch (Exception e){
            e.fillInStackTrace();
        }
        return "";
    }
}
