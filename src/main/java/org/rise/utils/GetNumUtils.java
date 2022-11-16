package org.rise.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetNumUtils {
    /***
     * 提取第一次出现的浮点数
     * @param tmp
     * @return
     */
    public static double getDouble(String tmp)//只加第一次出现的浮点数
    {
        double res = 0;
        tmp = tmp.replaceAll("§[0-9]", "§f");
        Pattern p = Pattern.compile("[0-9]+(\\.[0-9]+)?");
        Matcher m = p.matcher(tmp);
        if (m.find()) res = Float.parseFloat(m.group());
        return res;
    }
}
