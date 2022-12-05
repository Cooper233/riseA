package org.rise.utils;

public class RandNumUtils {
    /***
     * 获取a到b之间的随机数
     * @param a
     * @param b
     * @return
     */
    public static int getRand(int a, int b) {
        return (int) (Math.random() * (b - a) + a);
    }
}
