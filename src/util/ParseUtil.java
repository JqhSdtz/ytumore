package util;

import bean.LogSession;
import info.SystemInfo;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author JQH
 * @since 下午 10:08 19/10/22
 */
public class ParseUtil {
    public static int getNumFromString(String str, boolean notZero, int defVal) {
        int l = str.length(), num = 0;
        char ch;
        boolean flag = false;//flag为true表示没有数字
        for (int i = 0; i < l; ++i) {
            ch = str.charAt(i);
            if (ch >= '0' && ch <= '9') {
                if (!flag)
                    flag = true;
                num = num * 10 + ch - '0';
            }
        }
        if (!flag)//没有数字，取默认值
            num = defVal;
        else if (notZero && num == 0)//得到值为0且不允许0值，取默认值
            num = defVal;
        return num;
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches())
            return false;
        return true;
    }

    public static String getResponseStr(String path, LogSession logSession, Map<String, String> headers, Map<String, String> data) {
        Connection.Response rs;
        try {
            Connection connection = Jsoup.connect(path).timeout(SystemInfo.CONNECTION_TIMEOUT)
                    .cookie("JSESSIONID", logSession.sessionId)
                    .ignoreContentType(true);
            if (headers != null)
                connection.headers(headers);
            if (data != null)
                connection.data(data);
            rs = connection.execute();
        } catch (IOException e) {
            System.out.println("error:get response of " + path + " failed with message " + e.getMessage() + " curUid:" + logSession.curUid + " " + new Date());
            return null;
        }
        if (rs.url().toString().contains("login"))
            return null;
        return rs.body();
    }

    public static String getParamOfUrl(String name, String url) {
        if (url == null)
            return "";
        String query = url.split("\\?").length > 1 ? url.split("\\?")[1] : "?";
        String[] vars = query.split("&");
        for (int i = 0; i < vars.length; i++) {
            String[] pair = vars[i].split("=");
            if (name.equals(pair[0])) {
                return pair[1];
            }
        }
        return "";
    }

}
