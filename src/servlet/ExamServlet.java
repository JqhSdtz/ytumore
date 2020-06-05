package servlet;

import bean.ExamBean;
import bean.LogSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author JQH
 * @since 下午 10:12 19/12/10
 */
public class ExamServlet extends HttpServlet {

    final static int SUCCESS = 1;
    final static int FAIL = 2;

    public static SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getServletPath().contains("_async_newExam")) {
            newExam(request, response);
        } else if (request.getServletPath().contains("exam")) {
            exam(request, response);
        }
    }

    private void exam(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uid = (String) request.getSession().getAttribute("clientUid");
        List<ExamBean> examList = DBUtil.getExamList(uid);
        if(examList != null && examList.size() == 0) {//数据库中没有数据，需获取考试安排
            request.setAttribute("exDelete", "0");
            request.setAttribute("isFromEmpty", "1");//从数据库为空的情况跳转过去的
            request.getRequestDispatcher("/newExam.ym").forward(request, response);
            return;
        }
        processExamList(request, examList);
        dispatchExam(request, response, SUCCESS);
    }

    private void newExam(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int status;
        String uid = (String) request.getSession().getAttribute("clientUid");
        LogSession logSession = LogSessionUtil.getLogSession(request, uid);
        boolean delete = "0".equals(request.getAttribute("exDelete")) ? false : true;
        try {
            status = getExam(uid, logSession, request, delete);
        } catch (IOException e) {
            e.printStackTrace();
            status = FAIL;
        }
        dispatchExam(request, response, status);
        ThreadUtil.completeAsyncTask(request);
    }

    private int getExam(String uid, LogSession logSession, HttpServletRequest request, boolean delete) throws IOException {
        String path0 = "http://202.194.116.132/student/examinationManagement/examPlan/index";
        Jsoup.connect(path0).cookie("JSESSIONID", logSession.sessionId).execute();
        String path = "http://202.194.116.132/student/examinationManagement/examPlan/detail";
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
        String str = ParseUtil.getResponseStr(path, logSession, headers, null);
        if(str == null)
            return FAIL;
        JSONArray examJsonList;
        List<ExamBean> examList = new ArrayList<>();
        try {
            examJsonList = new JSONArray(str);
        } catch (JSONException e) {
            processExamList(request, examList);
            return SUCCESS;
        }
        int length = examJsonList.length();
        for(int i = 0; i < length; ++i) {
            JSONObject examJson = examJsonList.getJSONObject(i);
            ExamBean exam = parseExamJson(examJson);
            examList.add(exam);
        }
        /*集中处理数据库操作 --开始*/
        if(delete)
            DBUtil.deleteExam(uid);
        for(ExamBean exam : examList)
            DBUtil.saveExam(uid, exam);
        /*集中处理数据库操作 --结束*/

        processExamList(request, examList);
        return SUCCESS;
    }

    private void dispatchExam(HttpServletRequest request, HttpServletResponse response, int status) throws ServletException, IOException {
        if (status == FAIL) {
            if (request.getAttribute("asyncTimeout") == null)//不是异步任务超时
                response.setStatus(LogSession.OUT_OF_DATE);
            return;
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("pages/exam/examSchedule.jsp");
        dispatcher.forward(request, response);
        return;
    }

    private void processExamList(HttpServletRequest request, List<ExamBean> examList) {
        request.setAttribute("examList", examList);
    }

    private ExamBean parseExamJson(JSONObject examJson) {
        ExamBean exam = new ExamBean();
        String titleStr = examJson.getString("title");
        String[] strArray = titleStr.split("\n");
        for(String item: strArray)
            item.trim();
        exam.seteName(strArray[0]);
        exam.seteTime(strArray[1]);
        exam.seteBuilding(strArray[3]);
        exam.seteRoom(strArray[4]);
        try {
            Date date = dateFormat.parse(examJson.getString("start"));
            exam.seteWeekNum(CalendarUtil.getSpecificWeekNum(date));
            exam.seteWeekDay(CalendarUtil.getSpecificWeekDay(date));
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return exam;
    }

}
