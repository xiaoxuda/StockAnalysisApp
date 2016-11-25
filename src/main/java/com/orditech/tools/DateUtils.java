/**
 *
 */
package com.orditech.tools;

/**
 * @author kimi
 */
public class DateUtils {
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
}
