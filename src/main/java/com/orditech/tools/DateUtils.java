/**
 *
 */
package com.orditech.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author kimi
 */
public class DateUtils {

    public static final SimpleDateFormat DAY = new SimpleDateFormat ("yyyy-MM-dd");

    /**
     * 获取去年同季度日期
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static String getThisDateInLastYear (String date) throws Exception {
        String year_s = date.split ("-")[0];
        int year_i = Integer.valueOf (year_s);
        return (year_i - 1) + date.substring (4);
    }

    /**
     * 获取上一季度日期
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static String getLastDate (String date) throws Exception {
        return null;
    }

    /**
     * 格式化日期，格式yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String getDayStr (Date date) {
        if (date == null) {
            return null;
        }
        return DAY.format (date);
    }
}
