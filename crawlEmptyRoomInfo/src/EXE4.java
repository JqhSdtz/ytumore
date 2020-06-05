import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class EXE4 {
    private static String sessionId;
    private static String termStr;
    private static Set<String> roomSet = new HashSet<>();
    private static boolean flag = true;
    private static int threadNum = 0;
    private static int cnt = 0;

    private static Set<Thread> threadSet = new HashSet<>();

    private static String prePage;
    private static int samePageCnt;
    private static int spSkipCnt;

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
        FileUtil.init();
        initSession();
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

    private static void initSession() throws IOException {
        FileReader fileReader = new FileReader("crawlEmptyRoomInfo/src/in.txt");
        Scanner scanner = new Scanner(fileReader);
        sessionId = scanner.nextLine();
        termStr = scanner.nextLine();
    }

    private static void goCrawl() throws Exception {
        getRoomInfo(1, 32, 1, 24);
    }

    public static void getRoomInfo(int bs, int be, int ws, int we) throws Exception {
        getRoomInfo(bs, be, ws, we, 1, 7, 1, 12);
    }

    public static void getRoomInfoWithFullArgs(int buildingNo, int weekNum, int weekDay, int secNum) throws Exception {
        getRoomInfo(buildingNo, buildingNo, weekNum, weekNum, weekDay, weekDay, secNum, secNum);
    }

    /**
     * bs/be 教室号开始/结束 最大32， ws/we 周次开始/结束 最大24
     */
    public static void getRoomInfo(int bs, int be, int ws, int we, int wds, int wde, int scs, int sce) throws Exception {
        //note 将buildingNo放到最里层，因为不同的building的空教室肯定是不一样的
        for (int weekNum = ws; weekNum <= we; ++weekNum) {
            for (int weekDay = wds; weekDay <= wde; ++weekDay) {
                for (int secNum = scs; secNum <= sce; ++secNum) {
                    for (int buildingNo = bs; buildingNo <= be; ++buildingNo) {
                        if (!flag)
                            return;
                        int a = buildingNo, b = weekNum, c = weekDay, d = secNum;
                        Thread thread = new Thread(() -> {
                            try {
                                getAndParse(a, b, c, d, 0, 0);
                                --threadNum;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        while (threadNum > 50)
                            Thread.sleep(1000);
                        thread.start();
                        ++threadNum;
                    }
                }
            }
        }
    }

    private static void getAndParse(int buildingNo, int weekNum, int weekDay, int secNum, int depth, int spCnt) throws Exception {
        if (depth > 20) {
            System.out.printf("parse building %s, weekNum %s, weekDay %s, secNum %s failed!\n", buildingNo, weekNum, weekDay, secNum);
            return;
        }
        String path = "http://202.194.116.31/xszxcxAction.do?oper=tjcx&zxXaq=01&pageSize=300";
        try {
            Map<String, String> queryData = new HashMap<>();
            queryData.put("zxxnxq", termStr);
            queryData.put("zxZc", String.valueOf(weekNum));
            queryData.put("zxxq", String.valueOf(weekDay));
            queryData.put("zxJc", String.valueOf(secNum));
            queryData.put("zxJxl", String.valueOf(buildingNo));
            Connection.Response rs = Jsoup.connect(path).timeout(20000)
                    .cookie("JSESSIONID", sessionId).data(queryData)
                    .execute();
            Document doc = Jsoup.parse(rs.body());
            if ("错误信息".equals(doc.title())) {
                getAndParse(buildingNo, weekNum, weekDay, secNum, depth + 1, spCnt);
                return;
            }
            Elements tbody = doc.getElementsByClass("odd");
            String pageStr = tbody.size() == 0 ? "" : tbody.html();
            if (isSameAsPrePage(pageStr)) {
                if (spCnt > 10) {//这里spCnt设定的比较值不小于depth比较值则不会出现跳过
                    System.out.printf("Skipped same page %d at buildingNo:%d, weekNum:%d, weekDay:%d, secNum:%d\n", ++spSkipCnt, buildingNo, weekNum, weekDay, secNum);
                } else {
                    System.out.println("same page " + ++samePageCnt);
                    TimeUnit.SECONDS.sleep(60);
                    getAndParse(buildingNo, weekNum, weekDay, secNum, depth + 1, spCnt + 1);
                    return;
                }
            }
            prePage = pageStr;
            int length = tbody.size();
            Elements tr;
            for (int i = 0; i < length; ++i) {
                tr = tbody.get(i).getElementsByTag("td");
                RoomBean room = getRoomByElem(tr);
                saveScheduleInfo(buildingNo, weekNum, weekDay, secNum, room);
                ++cnt;
                if (cnt % 1000 == 0)
                    System.out.println(cnt);
            }
        } catch (IOException e) {
            Thread.sleep(1000);
            getAndParse(buildingNo, weekNum, weekDay, secNum, depth + 1, spCnt);
            e.printStackTrace();
        }
    }

    private static RoomBean getRoomByElem(Elements td) {
        RoomBean room = new RoomBean();
        room.setRoomNo(td.get(3).text());
        room.setRoomType(td.get(4).text());
        room.setSeatNum(Integer.valueOf(td.get(5).text()));
        return room;
    }

    private static void saveScheduleInfo(int buildingNo, int weekNum, int weekDay, int secNum, RoomBean room) {
        String roomNo = buildingNo + room.getRoomNo();
        if (!roomSet.contains(roomNo)) {
            FileUtil.saveRoomInfo(buildingNo, room);
            roomSet.add(roomNo);
        }
        FileUtil.saveScheduleInfo(buildingNo, weekNum, weekDay, secNum, room);
    }

    public static void waitToClose() {//不确定管不管用，可能要改
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
                        FileUtil.close();
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

}
