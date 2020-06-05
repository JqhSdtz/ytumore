package servlet;

import bean.LogSession;
import bean.RoomBean;
import util.DBUtil;
import util.ParseUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class RoomQueryServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("1".equals(request.getParameter("query"))) {
            showQueryPage(request, response);
        } else if ("1".equals(request.getParameter("result"))) {
            String queryType = request.getParameter("queryType");
            if ("1".equals(queryType)) {
                showEmptyRoomByDay(request, response);
            } else if ("2".equals(queryType)) {
                showEmptyRoomBySecNum(request, response);
            }
        }
    }

    private void showEmptyRoomByDay(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String buildingNo = request.getParameter("building");
        String weekNum = request.getParameter("week");
        String weekDay = request.getParameter("weekday");
        String buildingName = request.getParameter("buildingName");
        List<RoomBean> roomList = DBUtil.getEmptyRoomByDay(ParseUtil.getNumFromString(buildingNo, true, 1),
                ParseUtil.getNumFromString(weekNum, true, 1), ParseUtil.getNumFromString(weekDay, true, 1));
        request.setAttribute("roomList", roomList);
        request.setAttribute("buildingName", buildingName);
        RequestDispatcher dispatcher = request.getRequestDispatcher("pages/room/roomResultByDay.jsp");
        dispatcher.forward(request, response);
    }

    private void showEmptyRoomBySecNum(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String buildingNo = request.getParameter("building");
        String weekNum = request.getParameter("week");
        String weekDay = request.getParameter("weekday");
        String secNum = request.getParameter("sec");
        if(secNum == null)
            secNum = "1";
        String buildingName = request.getParameter("buildingName");
        List<RoomBean> roomList;
        roomList = DBUtil.getEmptyRoomBySecNum(ParseUtil.getNumFromString(buildingNo, true, 1),
                ParseUtil.getNumFromString(weekNum, true, 1), ParseUtil.getNumFromString(weekDay, true, 1),
                ParseUtil.getNumFromString(secNum, true, 1));
        if (roomList == null) {
            response.setStatus(LogSession.OUT_OF_DATE);
            return;
        }
        request.setAttribute("roomList", roomList);
        request.setAttribute("buildingName", buildingName);
        request.setAttribute("secNum", secNum);
        RequestDispatcher dispatcher = request.getRequestDispatcher("pages/room/roomResult.jsp");
        dispatcher.forward(request, response);
    }

    private void showQueryPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("pages/room/roomQuery.jsp");
        dispatcher.forward(request, response);
    }

}

/*http://202.194.116.31/xszxcxAction.do?oper=tjcx&zxXaq=01&pageSize=300
&zxxnxq=2019-2020-1-1&zxJxl=5&zxZc=6&zxxq=2&zxJc=5*/
