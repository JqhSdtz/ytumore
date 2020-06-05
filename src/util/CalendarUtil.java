package util;

import info.SystemInfo;

import java.util.Calendar;
import java.util.Date;

public class CalendarUtil {
    private static int[] hourType = new int[25];
    private static int[] secNumOfHour = new int[25];
    private static boolean initialized = false;

    public static void init() {
        if(initialized)
            return;
        Calendar.getInstance().setFirstDayOfWeek(Calendar.MONDAY);
        hourType[8] = hourType[10] = hourType[14] = hourType[16] = hourType[19] = 1;
        hourType[9] = hourType[11] = hourType[15] = hourType[17] = hourType[20] = 2;
        hourType[21] = hourType[22] = 3;
        secNumOfHour[8] = 1;
        secNumOfHour[10] = 3;
        secNumOfHour[14] = 5;
        secNumOfHour[16] = 7;
        secNumOfHour[19] = 9;
        initialized = true;
    }

    public static int getCurWeekNum() {
        return _getWeekNum(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));
    }

    public static int getCurWeekDay() {
        return _getWeekDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
    }

    public static int getSpecificWeekNum(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return _getWeekNum(calendar.get(Calendar.WEEK_OF_YEAR));
    }

    public static int getSpecificWeekDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return _getWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
    }

    public static int getCurSecNum() {
        Calendar.getInstance().setTime(new Date());
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        if(hourType[hour] == 1){
            if(minute <= 45)
                return secNumOfHour[hour];
            else
                return secNumOfHour[hour] + 1;
        }else if(hourType[hour] == 2){
            if(minute <= 40)
                return secNumOfHour[hour - 1] + 1;
            else
                return secNumOfHour[hour - 1] + 2;
        }else if(hourType[hour] == 3){
            if(hour == 21 && minute <= 35)
                return 11;
            else
                return 12;
        }else{
            if(hour < 8)
                return 1;
            else if(hour < 14)
                return 5;
            else if(hour < 19)
                return 9;
            else
                return 12;
        }
    }

    private static int _getWeekNum(int curWeekNum) {
        if(getCurWeekDay() == 7)
            --curWeekNum;
        int num = curWeekNum - SystemInfo.TERM_START_WEEK_NUM;
        if (num < 0 && num < -10) //当前周次距开学周次大于10才被认为跨年，前提是默认假期少于10周
            num += 52;
        return num + 1;
    }

    private static int _getWeekDay(int n) {
        --n;
        if (n == 0)
            n = 7;
        return n;
    }

}
