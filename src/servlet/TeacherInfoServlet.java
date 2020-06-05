package servlet;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author JQH
 * @since 上午 10:17 20/01/03
 */
public class TeacherInfoServlet extends HttpServlet {
    private static Map<String, JSONArray> teacherInfoMap = new HashMap<>();

    @Override
    public void init() {
        try {
            Scanner scanner = new Scanner(new FileReader(TeacherInfoServlet.class.getResource("/").getPath() + "teacherInfo.json"));
            JSONObject fileJson = new JSONObject(scanner.nextLine());
            scanner.close();
            JSONArray schools = fileJson.getJSONArray("schools");
            for(int i = 0; i < schools.length(); ++i) {
                JSONObject school = schools.getJSONObject(i);
                String schoolName = school.getString("schoolName");
                JSONArray teachers = school.getJSONArray("teachers");
                for(int j = 0; j < teachers.length(); ++j) {
                    JSONObject teacher = teachers.getJSONObject(j);
                    teacher.put("schoolName", schoolName);
                    String teacherName = teacher.getString("teacherName");
                    if(teacherInfoMap.containsKey(teacherName))
                        teacherInfoMap.get(teacherName).put(teacher);
                    else
                        teacherInfoMap.put(teacherName, new JSONArray().put(teacher));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("1".equals(request.getParameter("query"))) {
            showQueryPage(request, response);
        } else if ("1".equals(request.getParameter("result"))) {
            getResult(request, response);
        }
    }

    private void showQueryPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("pages/teacherInfo/teacherInfoQuery.jsp");
        dispatcher.forward(request, response);
    }

    private void getResult(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject respJson = new JSONObject();
        String teacherName = request.getParameter("teacherName");
        if(teacherName == null)
            respJson.put("state", 0);
        else {
            JSONArray info = teacherInfoMap.get(teacherName);
            if(info == null)
                respJson.put("state", 0);
            else {
                respJson.put("state", 1);
                respJson.put("info", info);
            }
        }
        response.getWriter().write(respJson.toString());
    }
}
