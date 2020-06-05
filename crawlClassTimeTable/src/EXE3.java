import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author JQH
 * @since 下午 9:59 19/09/15
 */
public class EXE3 {

    private static String sessionId = "badMuwgH4IKY9a45UqU2w";

    private static JSONObject json;
    private static String[] termStr = {"2017-2018-1-1", "2017-2018-2-1", "2018-2019-1-1",
            "2018-2019-2-1", "2019-2020-1-1", "2019-2020-2-1"};

    private static boolean flag = true;
    private static int threadNum = 0;
    private static Set<Thread> threadSet = new HashSet<>();
    private static int cnt = 0;

    private static String prePage;
    private static int samePageCnt = 0;
    private static int spSkipCnt = 0;

    public static void exe() {
        try {
            init();
            goCrawl();
            waitToClose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void init() throws Exception {
        json = initJson();
        DBUtil.init();
        startRefreshThread();
    }

    private static void startRefreshThread() {
        String path = "http://202.194.116.31/xszxcxAction.do?oper=xszxcx_lb";
        Thread thread = new Thread(() -> {
            try {
                Jsoup.connect(path).method(Connection.Method.GET).timeout(10000)
                        .cookie("JSESSIONID", sessionId).ignoreHttpErrors(true).execute();
                TimeUnit.MINUTES.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private static synchronized boolean isSameAsPrePage(String page) {
        if (prePage != null && !"".equals(prePage) && prePage.equals(page))
            return true;
        else {
            prePage = page;
            return false;
        }
    }

    private static void goCrawl() throws Exception {
        JSONArray schools = json.getJSONArray("schools");
        for (int j = 0; j < schools.length(); ++j) {
            JSONArray specialities = (JSONArray) ((JSONObject) schools.get(j)).get("specialities");
            //String schoolNo = (String)((JSONObject)schools.get(j)).get("schoolNo");
            for (int k = 0; k < specialities.length(); ++k) {
                JSONArray classes = (JSONArray) ((JSONObject) specialities.get(k)).get("classes");
                for (int l = 0; l < classes.length(); ++l) {
                    String classNo = (String) ((JSONObject) classes.get(l)).get("classNo");
                    for (int i = 0; i < termStr.length; ++i) {//最后循环学期，因为一个班不同学期课表一定不同
                        String term = termStr[i];
                        if (!flag)
                            return;
                        String a = term, b = classNo;
                        Thread thread = new Thread(() -> {
                            try {
                                getTimeTable(a, b, 0, 0);
                                --threadNum;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        while (threadNum > 60)
                            Thread.sleep(1000);
                        thread.start();
                        threadSet.add(thread);
                        ++threadNum;
                    }
                }
            }
        }
    }

    private static void waitToClose() {//不确定管不管用，可能要改
        Thread thread = new Thread(() -> {
            while (true) {
                boolean flag = false;
                for (Thread thread1 : threadSet) {
                    if (thread1.isAlive()) {
                        flag = true;
                        break;
                    }
                }
                try {
                    if (!flag) {//线程全部结束
                        System.out.println("close normally!");
                        DBUtil.close();
                        break;
                    } else//否则等五秒钟
                        TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private static void getTimeTable(String termNo, String classNo, int depth, int spCnt) throws Exception {
        if (depth > 20) {
            System.out.println("error at " + termNo + ", " + classNo);
            return;
        }
        //System.out.printf("term:%s, schoolNo:%s, classNo:%s\n", term, schoolNo, classNo);
        String pathBase = "http://202.194.116.31/bjKbInfoAction.do?oper=bjkb_xx";
        String classStr = URLEncoder.encode(classNo, "GB2312");
        String path = pathBase + "&xzxjxjhh=" + termNo + "&xbjh=" + classStr;
        Document doc = Jsoup.connect(path).cookie("JSESSIONID", sessionId)
                .ignoreContentType(true).get();
        if ("错误信息".equals(doc.title())) {
            getTimeTable(termNo, classNo, depth + 1, spCnt);
            return;
        }
        String pageStr = doc.toString();
        if (isSameAsPrePage(pageStr)) {
            if (spCnt > 10) {//这里spCnt设定的比较值不小于depth比较值则不会出现跳过
                System.out.printf("Skipped same page %d at termNo:%s, classNo:%s\n", ++spSkipCnt, termNo, classNo);
            } else {
                System.out.println("same page " + ++samePageCnt);
                TimeUnit.MINUTES.sleep(1);
                getTimeTable(termNo, classNo, depth + 1, spCnt + 1);
                return;
            }
        }
        prePage = pageStr;
        Elements trs = doc.getElementsByClass("displayTag").get(0)
                .getElementsByTag("tr");
        int idx[] = {2, 3, 4, 5, 7, 8, 9, 10, 12, 13, 14, 15};
        for (int i = 0; i < 12; ++i) {
            Elements tds = trs.get(idx[i]).getElementsByTag("td");
            int offset;
            if (i == 0 || i == 4 || i == 8)
                offset = 2;
            else
                offset = 1;
            for (int j = 0; j < 7; ++j) {
                String course = tds.get(j + offset).html();
                if (course.length() < 3 || "&nbsp;".equals(course))
                    continue;
                DBUtil.saveCourseInfo(termNo, classNo, course, i, j);
                ++cnt;
                if (cnt % 1000 == 0)
                    System.out.println(cnt);
            }
        }
    }

    private static JSONObject initJson() throws Exception {
        File file = new File("crawlClassTimeTable/src/classInfoJson.json");
        byte[] fileContent = new byte[((Long) file.length()).intValue()];
        FileInputStream in = new FileInputStream(file);
        in.read(fileContent);
        in.close();
        String str = new String(fileContent, "UTF-8");
        return new JSONObject(str);
    }
}
