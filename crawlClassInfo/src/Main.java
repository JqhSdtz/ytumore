import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static String sessionId = "bcaV8f5ShG5pSf6_nJKYw";
    private static String termStr = "2019-2020-1-1";

    public static void main(String[] args) {
        //getClassInfo();
        //filterJSON1();
        String[][][] test = new String[10][10][10];
        System.out.println(test == null ? 0 : 1);
    }

    private static void getClassInfo() {
        String path = "http://202.194.116.31/bjkbcxAction.do?oper=bjkb_lb";
        try {
            Connection.Response rs = Jsoup.connect(path)//每次手动登录后更换sessionid
                    .cookie("JSESSIONID", sessionId).execute();
            Document doc = Jsoup.parse(rs.body());
            Elements schools = doc.getElementsByAttributeValue("name", "bjxsh").get(0).children();
            JSONArray jsonArray = new JSONArray();
            for (int i = 1; i < schools.size(); ++i) {//leap over the first
                JSONObject jsonObject = new JSONObject();
                String schoolNo = schools.get(i).attr("value");
                jsonObject.put("schoolNo", schoolNo);
                jsonObject.put("schoolName", schools.get(i).text());
                jsonObject.put("specialities", getSpecialitiesJSONArray(schoolNo));
                jsonArray.put(jsonObject);
            }
            System.out.println("{\"schools\":" + jsonArray + "}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JSONArray getSpecialitiesJSONArray(String schoolNo) throws IOException {
        JSONArray jsonArray = new JSONArray();
        String path = "http://202.194.116.31/bjkbcxAction.do?oper=ld&bjxnxq=" + termStr + "&bjzyh=&nj=&bj=&pageSize=300&page=1&currentPage=1&pageNo=&bjxsh=" + schoolNo;
        Connection.Response rs = Jsoup.connect(path)
                .cookie("JSESSIONID", sessionId).execute();
        Document doc = Jsoup.parse(rs.body());
        Elements specialities = doc.getElementsByAttributeValue("name", "bjzyh").get(0).children();
        for (int i = 1; i < specialities.size(); ++i) {//leap over the first
            JSONObject jsonObject = new JSONObject();
            String specialityNo = specialities.get(i).attr("value");
            jsonObject.put("specialityNo", specialityNo);
            jsonObject.put("specialityName", specialities.get(i).text());
            jsonObject.put("classes", getClassesJSONArray(schoolNo, specialityNo));
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    private static JSONArray getClassesJSONArray(String schoolNo, String specialityNo) throws IOException {
        JSONArray jsonArray = new JSONArray();
        String path = "http://202.194.116.31/bjkbcxAction.do?oper=ld";
        Connection.Response rs = Jsoup.connect(path)
                .cookie("JSESSIONID", sessionId)
                .method(Connection.Method.POST).data("bjxnxq", termStr)
                .data("bjxsh", schoolNo)
                .data("bjzyh", specialityNo).execute();
        Document doc = Jsoup.parse(rs.body());
        Elements classes = doc.getElementsByAttributeValue("name", "bj").get(0).children();
        for (int i = 1; i < classes.size(); ++i) {//leap over the first
            String classNo = classes.get(i).attr("value");
            if (hasInfo(schoolNo, specialityNo, classNo)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("classNo", classNo);
                jsonObject.put("className", classes.get(i).text());
                jsonArray.put(jsonObject);
            }
        }
        return jsonArray;
    }

    private static boolean hasInfo(String schoolNo, String specialityNo, String classNo) throws IOException {
        String path = "http://202.194.116.31/bjkbcxAction.do?oper=kbtjcx";
        Connection.Response rs = Jsoup.connect(path).method(Connection.Method.POST)
                .cookie("JSESSIONID", sessionId)
                .data("bjxnxq", termStr)
                .data("bjxsh", schoolNo).data("bjzyh", specialityNo)
                .data("bj", classNo)
                .data("pageSize", "300")
                .data("page", "1")
                .data("currentPage", "1")
                .postDataCharset("GB2312").execute();
        Document doc = Jsoup.parse(rs.body());
        if (doc.getElementsByClass("odd").size() == 0)
            return false;
        return true;
    }

//    private static void formatJSON2() {
//        try {
//            FileReader fileReader = new FileReader("src/classInfo.json");
//            Scanner scanner = new Scanner(fileReader);
//            String source = scanner.next();
//            JSONArray oriArray = new JSONObject(source).getJSONArray("schools");
//            JSONObject newJSON = new JSONObject();
//            newJSON.put("name", "ytu");
//            JSONArray schools = new JSONArray();
//            newJSON.put("schools", schools);
//            for (int i = 0; i < oriArray.length(); i += 2) {
//                JSONObject school = oriArray.getJSONObject(i);
//                JSONArray oriSepArray = oriArray.getJSONArray(i + 1);
//                JSONArray newArray = new JSONArray();
//                schools.put(school);
//                school.put("specialities", newArray);
//                for (int j = 0; j < oriSepArray.length(); j += 2) {
//                    JSONObject spec = oriSepArray.getJSONObject(j);
//                    JSONArray oriClassArray = oriSepArray.getJSONArray(j + 1);
//                    spec.put("classes", oriClassArray);
//                    newArray.put(spec);
//                }
//            }
//            System.out.println(newJSON);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private static void filterJSON1() {
        try {
            FileReader fileReader = new FileReader("src/classInfoOri.json");
            Scanner scanner = new Scanner(fileReader);
            StringBuffer buffer = new StringBuffer();
            while (scanner.hasNext()) {
                buffer.append(scanner.next());
            }
            JSONArray schools = new JSONObject(buffer.toString()).getJSONArray("schools");
            JSONObject newJSON = new JSONObject();
            JSONArray newSchools = new JSONArray();
            newJSON.put("schools", newSchools);
            newJSON.put("name", "ytu");
            for (int i = 0; i < schools.length(); ++i) {
                if (schools.getJSONObject(i).getJSONArray("specialities").length() != 0) {
                    newSchools.put(schools.getJSONObject(i));
                }
            }
            System.out.println(newJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
