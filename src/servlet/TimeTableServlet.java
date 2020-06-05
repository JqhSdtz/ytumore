package servlet;

import bean.CourseBean;
import bean.CourseScheduleBean;
import bean.LogSession;
import info.SystemInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TimeTableServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getServletPath().contains("_async_newTimeTable")) {
            newTimeTable(request, response);
        } else if (request.getServletPath().contains("timeTable")) {
            timeTable(request, response);
        }
    }

    private void timeTable(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uid = (String) request.getSession().getAttribute("clientUid");
        CourseScheduleBean[][][] scheduleTable = DBUtil.getTimeTable(uid, SystemInfo.CUR_TERM);
        dispatchTimeTable(request, response, scheduleTable, false);
    }

    private void newTimeTable(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CourseScheduleBean[][][] scheduleTable;
        String uid = (String) request.getSession().getAttribute("clientUid");
        LogSession logSession = LogSessionUtil.getLogSession(request, uid);
        boolean delete = "0".equals(request.getAttribute("ttDelete")) ? false : true;
        //如果是新用户获取课表则不需要删除原有课表，否则需删除原有课表
        scheduleTable = getNewTimeTable(uid, logSession, delete);
        dispatchTimeTable(request, response, scheduleTable, true);
        ThreadUtil.completeAsyncTask(request);
    }

    private void dispatchTimeTable(HttpServletRequest request, HttpServletResponse response,
                                   CourseScheduleBean[][][] scheduleTable, boolean isNew) throws ServletException, IOException {
        //获取数据库课表时课表为空，一般是新用户，这时跳转到获取新课表操作
        if (!isNew && scheduleTable == null) {
            request.setAttribute("ttDelete", "0");//如果是新用户获取课表则不需要删除原有课表，否则需删除原有课表
            request.setAttribute("isFromEmpty", "1");//从数据库为空的情况跳转过去的
            request.getRequestDispatcher("/newTimeTable.ym").forward(request, response);
            return;
        }
        //获取新课表时课表为空，可能是LogSession过期了
        if (isNew && scheduleTable == null) {
            if (request.getAttribute("asyncTimeout") == null)//不是异步任务超时
                response.setStatus(LogSession.OUT_OF_DATE);
            return;//跳转操作交给过滤器
        }
        //获取新课表的过程中发现教务系统关闭了课表，则跳转到提示页面
        if (isNew && scheduleTable[0][0][0] != null && scheduleTable[0][0][0].getBuilding().equals("Unavailable")) {
            response.sendRedirect(request.getContextPath() + "/pages/notice/jump.jsp?ref=1&env=" + request.getParameter("env"));
            return;
        }
        //一切正常
        request.setAttribute("timeTable", scheduleTable);
        RequestDispatcher dispatcher = request.getRequestDispatcher("pages/timeTable/timeTable.jsp");
        dispatcher.forward(request, response);
    }

    private CourseScheduleBean[][][] getNewTimeTable(String uid, LogSession logSession, boolean delete) {
        //String path = "http://202.194.116.132/student/courseSelect/thisSemesterCurriculum/ajaxStudentSchedule/callback";
        String path = "http://202.194.116.132/student/courseSelect/thisSemesterCurriculum/ajaxStudentSchedule/curr/callback";
        String str = ParseUtil.getResponseStr(path, logSession, null, null);
        if(str == null)
            return null;
//            Document doc = Jsoup.parse(str);
//            if (doc.getElementsByClass("errorTop").get(0).text().contains("非选课阶段")) {
//                CourseScheduleBean[][][] tmpTable = new CourseScheduleBean[12][7][5];
//                tmpTable[0][0][0] = new CourseScheduleBean();
//                tmpTable[0][0][0].setBuilding("Unavailable");
//                return tmpTable;
//            }
        /*课程信息*/
        JSONObject courseJsonList = new JSONObject(str).getJSONArray("xkxx").getJSONObject(0);
        if (courseJsonList.keySet().size() == 0)//没有获取到新课表，则返回原来的课表
            return DBUtil.getTimeTable(uid, SystemInfo.CUR_TERM);
        CourseScheduleBean[][][] scheduleTable = new CourseScheduleBean[12][7][5];
        List<CourseBean> courseList = new ArrayList<>();
        for(String key: courseJsonList.keySet()) {
            JSONObject courseJson = courseJsonList.getJSONObject(key);
            if(!courseJson.has("timeAndPlaceList"))
                continue;
            CourseBean course = parseCourseJson(scheduleTable, courseJson);
            courseList.add(course);
        }

        /*集中处理数据库操作 --开始*/
        if (delete)//更新课表前先删除原有课表
            DBUtil.deleteTimeTable(uid);
        for (CourseBean course : courseList)
            DBUtil.saveCourse(uid, course);
        /*集中处理数据库操作 --结束*/

        return scheduleTable;
    }

    private CourseBean parseCourseJson(CourseScheduleBean[][][] scheduleTable, JSONObject courseJson) {
        CourseBean course = new CourseBean();
        course.setcNo(courseJson.getJSONObject("id").getString("coureNumber"));
        course.setcSeq(ParseUtil.getNumFromString(courseJson.getJSONObject("id").getString("coureSequenceNumber"), false, -1));
        course.setcName(courseJson.getString("courseName"));
        course.setcCredit(String.valueOf(courseJson.getDouble("unit")));
        course.setcAttr(courseJson.getString("coursePropertiesName"));
        course.setcTeacher(courseJson.getString("attendClassTeacher"));
        //这里改成了从JSON数据中获取该课程所属学期，而不是直接存储为当前学期
        String courseTerm = courseJson.getJSONObject("id").getString("executiveEducationPlanNumber");
        processTermStr(courseTerm);
        course.setcTerm(courseTerm);
        JSONArray scheduleJsonList = courseJson.getJSONArray("timeAndPlaceList");
        List<CourseScheduleBean> scheduleList = new ArrayList<>();
        for(int i = 0; i < scheduleJsonList.length(); ++i) {
            JSONObject scheduleJson = scheduleJsonList.getJSONObject(i);
            CourseScheduleBean courseSchedule = parseCourseScheduleJson(scheduleTable, scheduleJson);
            courseSchedule.setCourse(course);
            scheduleList.add(courseSchedule);
        }
        course.setCourseScheduleList(scheduleList);
        return course;
    }

    private void processTermStr(String courseTerm) {
        if(SystemInfo.CUR_TERM.equals(courseTerm))
            return;
        String[] courseParts = courseTerm.split("-");
        String[] curParts = SystemInfo.CUR_TERM.split("-");
        boolean flag = false;
        for(int i = 0; i < 4; ++i) {
            if(!Integer.valueOf(courseParts[i]).equals(Integer.valueOf(curParts[i]))) {
                flag = Integer.valueOf(courseParts[i]) > Integer.valueOf(curParts[i]);
                break;
            }
        }
        if(flag){//表明该课程的学期已经是下一个学期，则更改系统的学期时间
            SystemInfo.CUR_TERM = courseTerm;
            MailUtil.sendMail("NEW CUR_TERM:" + courseTerm);
        }
    }

    private CourseScheduleBean parseCourseScheduleJson(CourseScheduleBean[][][] scheduleTable, JSONObject scheduleJson) {
        CourseScheduleBean courseSchedule = new CourseScheduleBean();
        String weekDescription = scheduleJson.getString("weekDescription");
        String weeks = scheduleJson.getString("classWeek");
        courseSchedule.setWeeks(weekDescription + "|" + weeks);
        courseSchedule.setWeekDay(scheduleJson.getInt("classDay"));
        courseSchedule.setStartSec(scheduleJson.getInt("classSessions"));
        courseSchedule.setSecNum(scheduleJson.getInt("continuingSession"));
        courseSchedule.setBuilding(scheduleJson.getString("teachingBuildingName"));
        courseSchedule.setRoom(scheduleJson.getString("classroomName"));
        processSchedule(scheduleTable, courseSchedule);
        return courseSchedule;
    }


    private void processSchedule(CourseScheduleBean[][][] scheduleTable, CourseScheduleBean courseSchedule) {
        int r = courseSchedule.getStartSec() - 1;
        int c = courseSchedule.getWeekDay() - 1;
        int s = 0;
        while (s < 5 && scheduleTable[r][c][s] != null)
            ++s;
        scheduleTable[r][c][s] = courseSchedule;
        int secLen = courseSchedule.getSecNum() - 1;
        for (int k = 1; k <= secLen; ++k) {
            if (scheduleTable[r + k][c][s] == null)
                scheduleTable[r + k][c][s] = new CourseScheduleBean();
            scheduleTable[r + k][c][s].setSecNum(-1);
        }
    }

}
