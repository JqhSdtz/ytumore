package servlet;

import util.DBUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ClassTimeTableServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("1".equals(request.getParameter("query"))) {
            showQueryPage(request, response);
        } else if ("1".equals(request.getParameter("result"))) {
            showClassTimeTableFromDB(request, response);
        }
    }

    private void showQueryPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("pages/timeTable/classTimeTableQuery.jsp");
        dispatcher.forward(request, response);
    }

    private void showClassTimeTableFromDB(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String termNo = request.getParameter("termNo");
        String classNo = request.getParameter("classNo");
        String className = request.getParameter("className");
        String[][][] timeTable = new String[12][7][2];
        String courses[][] = DBUtil.getClassTimeTable(termNo, classNo);
        for (int i = 0; i < 12; ++i) {
            for(int j = 0; j < 7; ++j) {
                if(courses[i][j] == null)
                    timeTable[i][j][0] = "";
                else{
                    timeTable[i][j] = courses[i][j].split("<br>", 2);
                    if (timeTable[i][j].length > 1) {
                        if ("".equals(timeTable[i][j][1]))
                            timeTable[i][j][1] = null;
                        else {
                            timeTable[i][j][1] = (timeTable[i][j][0] + "<br/>").concat(timeTable[i][j][1]);
                        }
                    }
                }
            }
        }
        request.setAttribute("timeTable", timeTable);
        request.setAttribute("className", className);
        RequestDispatcher dispatcher = request.getRequestDispatcher("pages/timeTable/classTimeTableResult.jsp");
        dispatcher.forward(request, response);
    }

}
