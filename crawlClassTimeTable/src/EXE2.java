import org.json.JSONArray;
import org.json.JSONObject;
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
public class EXE2 {

    private static String[] termStr = {"2017-2018-1-1", "2017-2018-2-1", "2018-2019-1-1",
            "2018-2019-2-1", "2019-2020-1-1", "2019-2020-2-1"};

    private static String[] sessionList = {"bdaVZs4ckwkSU7hPewh1w",
            "bcdpBmqe4rcyO86m4UE0w","bdaV1WitSzYf14qtywh1w"};

    private static boolean sessionValid[] = new boolean[100];
    private static int sessionCnt = -1;

    private static boolean flag = true;
    private static int threadNum = 0;
    private static Set<Thread> threadSet = new HashSet<>();
    private static int cnt = 0;

    private static String prePage;
    private static int samePageCnt = 0;
    private static int spSkipCnt = 0;

    public static void exe() {
        try {
            JSONObject json = initJson();
            DBUtil.init();
            goCrawl(json);
            waitToClose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized static int getValidSessionIdx(int depth) {
        if (depth > sessionList.length)
            return -1;
        int idx = (++sessionCnt) % sessionList.length;
        if (!sessionValid[idx])
            return getValidSessionIdx(depth + 1);
        else
            return idx;
    }

    private static void goCrawl(JSONObject json) throws Exception {
        for (int i = 0; i < 100; ++i)
            sessionValid[i] = true;
        JSONArray schools = json.getJSONArray("schools");
        for (int i = 0; i < termStr.length; ++i) {
            String term = termStr[i];
            for (int j = 0; j < schools.length(); ++j) {
                JSONArray specialities = (JSONArray) ((JSONObject) schools.get(j)).get("specialities");
                //String schoolNo = (String)((JSONObject)schools.get(j)).get("schoolNo");
                for (int k = 0; k < specialities.length(); ++k) {
                    JSONArray classes = (JSONArray) ((JSONObject) specialities.get(k)).get("classes");
                    for (int l = 0; l < classes.length(); ++l) {
                        String classNo = (String) ((JSONObject) classes.get(l)).get("classNo");
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
                        DBUtil.close();
                        System.out.println("close normally!");
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
            flag = false;
            System.out.println("error at " + termNo + ", " + classNo);
            return;
        }
        //System.out.printf("term:%s, schoolNo:%s, classNo:%s\n", term, schoolNo, classNo);
        String pathBase = "http://202.194.116.31/bjKbInfoAction.do?oper=bjkb_xx";
        String classStr = URLEncoder.encode(classNo, "GB2312");
        String path = pathBase + "&xzxjxjhh=" + termNo + "&xbjh=" + classStr + "&xbm=" + classStr;
        int sessionIdx = getValidSessionIdx(0);
        if (sessionIdx == -1) {
            flag = false;
            System.out.println("no valid session available");
            return;
        }
        Document doc = Jsoup.connect(path).cookie("JSESSIONID", sessionList[sessionIdx])
                .ignoreContentType(true).get();
        if ("错误信息".equals(doc.title())) {
            sessionValid[sessionIdx] = false;
            getTimeTable(termNo, classNo, depth + 1, spCnt);
            return;
        }
        String pageStr = doc.toString();
        if(prePage != null && prePage.equals(pageStr)) {
            if(spCnt > 0) {
                System.out.println("Skipped same page " + ++spSkipCnt);
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
