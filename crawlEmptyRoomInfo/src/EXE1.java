import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

public class EXE1 {
    private static String sessionId;
    private static String termStr;
    private static Set<String> roomSet = new HashSet<>();
    private static boolean flag = true;
    private static int threadNum = 0;
    private static int cnt = 0;
    private static String[] buildingName = {"一教", "二教", "三教", "四教", "五教", "六教", "七教", "综合楼", "计算中心"
            , "外院公共语音室", "工程实训中心", "建筑馆", "外院", "网球场", "外院专业语音室",
            "足球场", "数学机房", "外院视听室", "物理实验中心", "体教部舞蹈房", "育秀大楼", "科技馆",
            "大学生活动", "校园管理", "社区中心", "实验室", "设计实习地", "音乐系",
            "体育课", "外院"};

    public static void exe() {
        try {
            //C3P0Util.init();
            FileUtil.init();
            initSession();
            getRoomInfo();
            FileUtil.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void initSession() throws IOException {
        FileReader fileReader = new FileReader("crawlEmptyRoomInfo/src/in.txt");
        Scanner scanner = new Scanner(fileReader);
        sessionId = scanner.nextLine();
        termStr = scanner.nextLine();
    }

    private static void getRoomInfo() throws Exception {
        for (int buildingNo = 1; buildingNo <= 32; ++buildingNo) {
            for (int weekNum = 1; weekNum <= 24; ++weekNum) {
                for (int weekDay = 1; weekDay <= 7; ++weekDay) {
                    for (int secNum = 1; secNum <= 12; ++secNum) {
                        if (!flag)
                            return;
                        int a = buildingNo, b = weekNum, c = weekDay, d = secNum;
                        Thread thread = new Thread(() -> {
                            try{
                                getAndParse(a, b, c, d, 0);
                                --threadNum;
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        });
                        while(threadNum > 60)
                            Thread.sleep(1000);
                        thread.start();
                        ++threadNum;
                    }
                }
            }
        }
    }

    private static void getAndParse(int buildingNo, int weekNum, int weekDay, int secNum, int depth) throws Exception{
        if(depth > 10) {
            System.out.printf("parse building %s, weekNum %s, weekDay %s, secNum %s failed!", buildingNo, weekNum, weekDay, secNum);
            return;
        }
        String path0 = "http://202.194.116.31/xszxcxAction.do?oper=xszxcx_lb";
        String path = "http://202.194.116.31/xszxcxAction.do?oper=tjcx&zxXaq=01&pageSize=300";
        try{
            Jsoup.connect(path0)
                    .cookie("JSESSIONID", sessionId).execute();
            Map<String, String> queryData = new HashMap<>();
            queryData.put("zxxnxq", termStr);
            queryData.put("zxZc", String.valueOf(weekNum));
            queryData.put("zxxq", String.valueOf(weekDay));
            queryData.put("zxJc", String.valueOf(secNum));
            queryData.put("zxJxl", String.valueOf(buildingNo));
            Connection.Response rs = Jsoup.connect(path)
                    .cookie("JSESSIONID", sessionId).data(queryData)
                    .execute();
            Document doc = Jsoup.parse(rs.body());
            if ("错误信息".equals(doc.title())) {
                flag = false;
                System.out.println("Error!");
                return;
            }
            Elements tbody = doc.getElementsByClass("odd");
            int length = tbody.size();
            Elements tr;
            for (int i = 0; i < length; ++i) {
                tr = tbody.get(i).getElementsByTag("td");
                RoomBean room = getRoomByElem(tr);
                saveScheduleInfo(buildingNo, weekNum, weekDay, secNum, room);
                ++cnt;
                if(cnt % 1000 == 0)
                    System.out.println(cnt);
            }
        }catch (IOException e){
            Thread.sleep(1000);
            getAndParse(buildingNo, weekNum, weekDay, secNum, depth +1);
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
}
