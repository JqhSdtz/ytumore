package servlet;

import bean.CourseBean;
import bean.GradeBean;
import bean.LogSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.DBUtil;
import util.LogSessionUtil;
import util.ParseUtil;
import util.ThreadUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GradeQueryServlet extends HttpServlet {

    final static int SUCCESS = 1;
    final static int FAIL = 2;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int type = -1;//type为1表示本学期成绩，2表示全部成绩，3表示不及格成绩
        if ("term".equals(request.getParameter("type")))
            type = 1;
        else if ("all".equals(request.getParameter("type")))
            type = 2;
        else if ("failed".equals(request.getParameter("type")))
            type = 3;
        if (request.getServletPath().contains("_async_newGrade")) {
            newGrade(type, request, response);
        } else if (request.getServletPath().contains("grade")) {
            grade(type, request, response);
        }
    }

    private void grade(int type, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uid = (String) request.getSession().getAttribute("clientUid");
        List<GradeBean> gradeList = DBUtil.getGradeList(uid, type);
        if (gradeList != null && gradeList.size() == 0) {//数据库中没有数据，需获取新成绩表
            request.setAttribute("gdDelete", "0");//如果是新用户获取成绩则不需要删除原有成绩表，否则需删除原有成绩表
            request.setAttribute("isFromEmpty", "1");//从数据库为空的情况跳转过去的
            request.getRequestDispatcher("/newGrade.ym").forward(request, response);
            return;
        }
        processGradeListFromDB(request, gradeList, type);
        dispatchGrade(request, response, type, SUCCESS);
    }

    private void newGrade(int type, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int status = FAIL;
        String uid = (String) request.getSession().getAttribute("clientUid");
        LogSession logSession = LogSessionUtil.getLogSession(request, uid);
        boolean delete = "0".equals(request.getAttribute("gdDelete")) ? false : true;
        try {
            if (type == 1) {
                status = getTermGrade(uid, logSession, request, delete);
            } else if (type == 2) {
                status = getAllGrade(uid, logSession, request, delete);
            } else if (type == 3) {
                status = getFailedGrade(uid, logSession, request, delete);
            }
        } catch (IOException e) {
            e.printStackTrace();
            status = FAIL;
        }
        dispatchGrade(request, response, type, status);
        ThreadUtil.completeAsyncTask(request);
    }

    private void dispatchGrade(HttpServletRequest request, HttpServletResponse response, int type, int status)
            throws ServletException, IOException {
        if (type == -1) {//类型请求参数不对
            response.setStatus(404);
            return;
        }
        RequestDispatcher dispatcher = null;
        if (status == FAIL) {
            if (request.getAttribute("asyncTimeout") == null)//不是异步任务超时
                response.setStatus(LogSession.OUT_OF_DATE);
            return;
        }
        if (type == 1)
            dispatcher = request.getRequestDispatcher("pages/grade/termGrade.jsp");
        else if (type == 2)
            dispatcher = request.getRequestDispatcher("pages/grade/allGrade.jsp");
        else if (type == 3)
            dispatcher = request.getRequestDispatcher("pages/grade/failedGrade.jsp");
        dispatcher.forward(request, response);
        return;
    }

    private void processGradeListFromDB(HttpServletRequest request, List<GradeBean> gradeList, int type) {
        if (type == 1) {
            request.setAttribute("gradeList", gradeList);
        } else if (type == 2) {
            processAllTermGradeListFromDB(request, gradeList);
        } else if (type == 3) {
            processFailedGradeListFromDB(request, gradeList);
        }
    }

    private void processAllTermGradeListFromDB(HttpServletRequest request, List<GradeBean> gradeList) {
        Map<String, List<GradeBean>> termGradeMap = new TreeMap<>(
                (o1, o2) -> {//s学期开始年份，e学期结束年份，se学期季节
                    int s1 = Integer.parseInt(o1.substring(0, 4)), s2 = Integer.parseInt(o2.substring(0, 4));
                    int e1 = Integer.parseInt(o1.substring(5, 9)), e2 = Integer.parseInt(o2.substring(5, 9));
                    int se1 = o1.charAt(11) == '秋' ? 1 : 2, se2 = o2.charAt(11) == '秋' ? 1 : 2;
                    if (s1 == s2) {
                        if (e1 == e2)
                            return se1 - se2;
                        else
                            return e1 - e2;
                    } else
                        return s1 - s2;
                }
        );
        for (GradeBean grade : gradeList) {
            String termTitle = grade.getTerm();
            if (termGradeMap.containsKey(termTitle))
                termGradeMap.get(termTitle).add(grade);
            else {
                List<GradeBean> tmpList = new ArrayList<>();
                tmpList.add(grade);
                termGradeMap.put(termTitle, tmpList);
            }
        }
        List<String> termTitleList = new ArrayList<>();
        List<List<GradeBean>> termList = new ArrayList<>();
        for (Map.Entry<String, List<GradeBean>> tmpEntry : termGradeMap.entrySet()) {
            termTitleList.add(tmpEntry.getKey());
            termList.add(tmpEntry.getValue());
        }
        request.setAttribute("termList", termList);
        request.setAttribute("termTitleList", termTitleList);
    }

    private void processFailedGradeListFromDB(HttpServletRequest request, List<GradeBean> gradeList) {
        List<GradeBean> stillFailingList = new ArrayList<>();
        List<GradeBean> passedList = new ArrayList<>();
        for (GradeBean grade : gradeList) {
            if (grade.getStatus() == 1)//尚不及格
                stillFailingList.add(grade);
            else
                passedList.add(grade);
        }
        request.setAttribute("stillFailingList", stillFailingList);
        request.setAttribute("passedList", passedList);
    }

    private int getTermGrade(String uid, LogSession logSession, HttpServletRequest request, boolean delete) throws IOException {
        String path = "http://202.194.116.132/student/integratedQuery/scoreQuery/thisTermScores/data";
        String str = ParseUtil.getResponseStr(path, logSession, null, null);
        if (str == null)
            return FAIL;
        JSONObject json = new JSONArray(str).getJSONObject(0);
        if(!"0".equals(json.getString("state")))
            return FAIL;
        JSONArray gradeJsonList = json.getJSONArray("list");
        int length = gradeJsonList.length();
        List<GradeBean> gradeList;//对成绩表而言是有较大可能出现为空的情况，这时不能判断为登录超时
        if (length == 0) {//没有获取到新的成绩表，看数据库有没有成绩表，若有，则用数据库里的表，否则返回空
            gradeList = DBUtil.getGradeList(uid, 1);
        } else {//获取到了新的成绩表
            gradeList = new ArrayList<>();
            for (int i = 0; i < length; ++i) {
                JSONObject gradeJson = gradeJsonList.getJSONObject(i);
                GradeBean grade = parseGradeJson(gradeJson, 1);
                gradeList.add(grade);
            }
        }

        /*集中处理数据库操作 --开始*/
        if (delete)
            DBUtil.deleteGrade(uid, 1);
        for (GradeBean grade : gradeList)
            DBUtil.saveGrade(1, uid, grade);
        /*集中处理数据库操作 --结束*/

        request.setAttribute("gradeList", gradeList);
        return SUCCESS;
    }

    private int getAllGrade(String uid, LogSession logSession, HttpServletRequest request, boolean delete) throws IOException {
        String path = "http://202.194.116.132/student/integratedQuery/scoreQuery/allPassingScores/callback";
        String str = ParseUtil.getResponseStr(path, logSession, null, null);
        if (str == null)
            return FAIL;
        JSONArray termJsonList = new JSONObject(str).getJSONArray("lnList");
        List<List<GradeBean>> termList = new ArrayList<>();
        List<String> termTitleList = new ArrayList<>();
        if(termJsonList.length() == 0)//没有获取到新的成绩表，看数据库有没有成绩表，若有，则用数据库里的表，否则返回空
            processAllTermGradeListFromDB(request, DBUtil.getGradeList(uid, 2));
        else {
            for(int i = 0; i < termJsonList.length(); ++i) {
                JSONObject termJson = termJsonList.getJSONObject(i);
                String termTitle = termJson.getString("cjlx");
                termTitleList.add(termTitle);
                JSONArray gradeJsonList = termJson.getJSONArray("cjList");
                List<GradeBean> gradeList = new ArrayList<>();
                for(int j = 0; j < gradeJsonList.length(); ++j) {//遍历每个学期的各门课
                    JSONObject gradeJson = gradeJsonList.getJSONObject(j);
                    GradeBean grade = parseGradeJson(gradeJson, 2);
                    grade.setTerm(termTitle);//设置该课程成绩所属的学期
                    gradeList.add(grade);//添加课程成绩
                }
                termList.add(gradeList);//添加学期
            }
        }

        /*集中处理数据库操作 --开始*/
        if (delete)
            DBUtil.deleteGrade(uid, 2);
        for (List<GradeBean> gradeList : termList) {
            for (GradeBean grade : gradeList)
                DBUtil.saveGrade(2, uid, grade);
        }
        /*集中处理数据库操作 --结束*/

        request.setAttribute("termList", termList);
        request.setAttribute("termTitleList", termTitleList);
        return SUCCESS;
    }

    private int getFailedGrade(String uid, LogSession logSession, HttpServletRequest request, boolean delete) throws IOException {
        String path = "http://202.194.116.132/student/integratedQuery/scoreQuery/unpassedScores/callback";
        String str = ParseUtil.getResponseStr(path, logSession, null, null);
        if (str == null)
            return FAIL;
        JSONArray secsJsonList = new JSONArray(str);
        List<GradeBean> stillFailingList = new ArrayList<>();
        List<GradeBean> passedList = new ArrayList<>();
        JSONArray stillFailingGradeJsonList = secsJsonList.getJSONObject(0).getJSONArray("cjList");
        JSONArray passedGradeJsonList = secsJsonList.getJSONObject(1).getJSONArray("cjList");
        int length1 = stillFailingGradeJsonList.length();
        int length2 = passedGradeJsonList.length();
        if (length1 == 0 && length2 == 0) {
            //没有获取到新的成绩表，看数据库有没有成绩表，若有，则用数据库里的表，否则返回空
            processFailedGradeListFromDB(request, DBUtil.getGradeList(uid, 3));
        } else {//获取到了新的成绩表
            //课程状态，1表示尚不及格，2表示曾不及格
            for (int i = 0; i < length1; ++i) {//尚不及格
                JSONObject gradeJson = stillFailingGradeJsonList.getJSONObject(i);
                GradeBean grade = parseGradeJson(gradeJson, 3);
                grade.setStatus(1);
                stillFailingList.add(grade);
            }
            for (int i = 0; i < length2; ++i) {//曾不及格
                JSONObject gradeJson = passedGradeJsonList.getJSONObject(i);
                GradeBean grade = parseGradeJson(gradeJson, 3);
                grade.setStatus(2);
                passedList.add(grade);
            }

            /*集中处理数据库操作 --开始*/
            if (delete)
                DBUtil.deleteGrade(uid, 3);
            for (GradeBean grade : stillFailingList)
                DBUtil.saveGrade(3, uid, grade);
            for (GradeBean grade : passedList)
                DBUtil.saveGrade(3, uid, grade);
            /*集中处理数据库操作 --结束*/

            request.setAttribute("stillFailingList", stillFailingList);
            request.setAttribute("passedList", passedList);
        }
        return SUCCESS;
    }

    private GradeBean parseGradeJson(JSONObject gradeJson, int type) {//type 1:term 2:all 3:failed
        GradeBean grade = new GradeBean();
        CourseBean course = new CourseBean();
        course.setcNo(gradeJson.getJSONObject("id").getString("courseNumber"));
        course.setcName(gradeJson.getString("courseName"));
        if(type == 1) {
            course.setcSeq(ParseUtil.getNumFromString(gradeJson.getString("coureSequenceNumber"), false, -1));
            course.setcCredit(String.valueOf(gradeJson.getDouble("credit")));
            grade.setExamTime(gradeJson.getJSONObject("id").getString("examtime"));
        } else {
            course.setcSeq(ParseUtil.getNumFromString(gradeJson.getJSONObject("id").getString("coureSequenceNumber"), false, -1));
            course.setcCredit(gradeJson.getString("credit"));
            grade.setExamTime(gradeJson.getString("examTime"));
        }
        if(gradeJson.has("courseAttributeName") && gradeJson.get("courseAttributeName") instanceof String)//课程属性，有哪个设置哪个，没有不设
            course.setcAttr(gradeJson.getString("courseAttributeName"));
        else if(gradeJson.has("coursePropertyName") && gradeJson.get("coursePropertyName") instanceof String)
            course.setcAttr(gradeJson.getString("coursePropertyName"));
        if(gradeJson.get("courseScore") instanceof String)
            grade.setCourseGrade("".equals(gradeJson.getString("courseScore")) ? "无" : gradeJson.getString("courseScore"));
        else if(gradeJson.get("courseScore") instanceof Double)
            grade.setCourseGrade(String.valueOf(gradeJson.getDouble("courseScore")));
        String courseGrade = grade.getCourseGrade();
        if ("不及格".equals(courseGrade) || "不合格".equals(courseGrade) || (courseGrade.charAt(0) >= '0' && courseGrade.charAt(0) <= '9'
                && Float.valueOf(courseGrade) < 60))
            grade.setLevel(0);
        else
            grade.setLevel(1);
        grade.setCourse(course);
        return grade;
    }

}
