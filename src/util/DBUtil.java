package util;

import bean.*;
import info.SystemInfo;

import java.sql.*;
import java.util.*;

public class DBUtil {

    /**
     * 下面是用户信息相关操作
     */

    public static boolean insertUser(String uid, int sid, String randKey, int lastLoginCode) {
        Connection con = C3P0Util.getMysqlConn();
        PreparedStatement st = null;
        boolean succeed;
        try {
            String sql = "insert into s_user(uid,sid,randKey,lastLoginCode) values(?,?,?,?);";
            st = con.prepareStatement(sql);
            st.setString(1, uid);
            st.setInt(2, sid);
            st.setString(3, randKey);
            st.setInt(4, lastLoginCode);
            st.execute();
            succeed = true;
        } catch (SQLException e) {
            e.printStackTrace();
            succeed = false;
        } finally {
            C3P0Util.closeAll(null, st, con);
        }
        return succeed;
    }

    public static void updateLGCode(String uid, int lastLoginCode, int loginCnt) {
        Connection con = C3P0Util.getMysqlConn();
        PreparedStatement st = null;
        try {
            String sql = "update s_user set lastLoginCode=?, loginCnt=? where uid=?;";
            st = con.prepareStatement(sql);
            st.setInt(1, lastLoginCode);
            st.setInt(2, loginCnt + 1);
            st.setString(3, uid);
            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            C3P0Util.closeAll(null, st, con);
        }
    }

    /**
     * 返回sid==-1表示用户不存在
     */
    public static UserBean getUserByUid(String uid) {
        Connection con = C3P0Util.getMysqlConn();
        PreparedStatement st = null;
        ResultSet rs = null;
        int sid = -1;
        String randKey = null;
        int lastLoginCode = -1, loginCnt = -1;
        try {
            String sql = "select sid,randKey,lastLoginCode,loginCnt from s_user where uid = ?;";
            st = con.prepareStatement(sql);
            st.setString(1, uid);
            rs = st.executeQuery();
            if (rs.next()) {
                sid = rs.getInt(1);
                randKey = rs.getString(2);
                lastLoginCode = rs.getInt(3);
                loginCnt = rs.getInt(4);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            C3P0Util.closeAll(rs, st, con);
        }
        return new UserBean(uid, sid, randKey, lastLoginCode, loginCnt);
    }

    public static int getUserNum() {
        Connection con = C3P0Util.getMysqlConn();
        PreparedStatement st = null;
        ResultSet rs = null;
        int num = 0;
        try {
            String sql = "select count(*) from s_user;";
            st = con.prepareStatement(sql);
            rs = st.executeQuery();
            rs.next();
            num = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            C3P0Util.closeAll(rs, st, con);
        }
        return num;
    }

    public static void createLog(String uid, String env, int type, String ip) {
        Connection con = C3P0Util.getMysqlConn();
        PreparedStatement st = null;
        int plat = 0;
        if(env == null || "".equals(env))
            plat = 1;
        else if("app".equals(env))
            plat = 2;
        else if("wxmp".equals(env))
            plat = 3;
        else if("qqmp".equals(env))
            plat = 4;
        else if("wxgzh".equals(env))
            plat = 5;
        else if("qqgzh".equals(env))
            plat = 6;
        try {
            String sql = "insert into s_log(uUid,type,plat,ip) values(?,?,?,?);";
            st = con.prepareStatement(sql);
            st.setString(1, uid);
            st.setInt(2, type);
            st.setInt(3, plat);
            st.setString(4, ip);
            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            C3P0Util.closeAll(null, st, con);
        }
    }

    /**
     * 下面是成绩相关操作
     */

    /**type==-1表示删除全部分类的成绩表*/
    public static void deleteGrade(String uid, int type) {
        Connection con = C3P0Util.getMysqlConn();
        PreparedStatement st = null;
        try {
            String sql;
            if(type == -1)
                sql = "delete from s_usr_grade where uUid=?;";
            else
                sql = "delete from s_usr_grade where uUid=? and gType=?;";
            st = con.prepareStatement(sql);
            st.setString(1, uid);
            if(type != -1)
                st.setInt(2, type);
            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            C3P0Util.closeAll(null, st, con);
        }
    }

    public static void saveGrade(int type, String uid, GradeBean grade) {
        Connection con = C3P0Util.getMysqlConn();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            String sql0 = "select g_cUid from s_g_course where cNo = ? and cSeq = ?;";
            st = con.prepareStatement(sql0);
            st.setString(1, grade.getCourse().getcNo());
            st.setInt(2, grade.getCourse().getcSeq());
            rs = st.executeQuery();
            if (rs.next()) {//已存在该成绩对应的课程
                grade.getCourse().setcUid(rs.getInt(1));
                saveExistedGrade(con, type, uid, grade);
            } else {
                saveNewGrade(con, grade);
                saveExistedGrade(con, type, uid, grade);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            C3P0Util.closeAll(rs, st, con);
        }
    }

    public static void saveNewGrade(Connection con, GradeBean grade) throws SQLException {
        String sql = "insert into s_g_course (cNo, cSeq, cName, cCredit, cAttr) value (?,?,?,?,?)";
        PreparedStatement st = con.prepareStatement(sql);
        st.setString(1, grade.getCourse().getcNo());
        st.setInt(2, grade.getCourse().getcSeq());
        st.setString(3, grade.getCourse().getcName());
        st.setString(4, grade.getCourse().getcCredit());
        st.setString(5, grade.getCourse().getcAttr());
        st.execute();
        C3P0Util.closeAll(null, st, null);
        String sql3 = "select last_insert_id() from s_g_course;";
        st = con.prepareStatement(sql3);
        ResultSet rs = st.executeQuery();
        rs.next();
        grade.getCourse().setcUid(rs.getInt(1));
        C3P0Util.closeAll(rs, st, null);
    }

    public static void saveExistedGrade(Connection con, int type, String uid, GradeBean grade) throws SQLException {
        String sql = "insert ignore into s_usr_grade (gType, uUid, gcUid, tGrade, tLevel, tExamTime, tTerm, tStatus)" +
                " values(?,?,?,?,?,?,?,?);";
        PreparedStatement st = con.prepareStatement(sql);
        st.setInt(1, type);
        st.setString(2, uid);
        st.setInt(3, grade.getCourse().getcUid());
        st.setString(4, grade.getCourseGrade());
        st.setInt(5, grade.getLevel());
        st.setString(6, grade.getExamTime());
        st.setString(7, grade.getTerm());
        st.setInt(8,grade.getStatus());
        st.execute();
        C3P0Util.closeAll(null, st, null);
    }

    public static List<GradeBean> getGradeList(String uid, int type) {
        if(type == -1)
            return null;
        Connection con = C3P0Util.getMysqlConn();
        PreparedStatement st = null;
        ResultSet rs = null;
        List<GradeBean> gradeList = new ArrayList<>();
        try {
            String sql = "select cNo, cSeq, cName, cCredit, cAttr, tGrade, tLevel, tExamTime, tTerm, tStatus " +
                    "from s_usr_grade, s_g_course " +
                    "where s_usr_grade.uUid=? and s_usr_grade.gType=? and s_usr_grade.gcUid=s_g_course.g_cUid";
            st = con.prepareStatement(sql);
            st.setString(1, uid);
            st.setInt(2, type);
            rs = st.executeQuery();
            while(rs.next()) {
                GradeBean grade = new GradeBean();
                CourseBean course = new CourseBean();
                course.setcNo(rs.getString(1));
                course.setcSeq(rs.getInt(2));
                course.setcName(rs.getString(3));
                course.setcCredit(rs.getString(4));
                course.setcAttr(rs.getString(5));
                grade.setCourse(course);
                grade.setCourseGrade(rs.getString(6));
                grade.setLevel(rs.getInt(7));
                grade.setExamTime(rs.getString(8));
                grade.setTerm(rs.getString(9));
                grade.setStatus(rs.getInt(10));
                gradeList.add(grade);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            C3P0Util.closeAll(rs, st, con);
        }
        return gradeList;
    }

    /**
     * 下面是考试安排相关操作
     */

    public static void deleteExam(String uid) {
        Connection con = C3P0Util.getMysqlConn();
        PreparedStatement st = null;
        try {
            String sql = "delete from s_usr_exam where uUid=?;";
            st = con.prepareStatement(sql);
            st.setString(1, uid);
            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            C3P0Util.closeAll(null, st, con);
        }
    }

    public static void saveExam(String uid, ExamBean exam) {
        Connection con = C3P0Util.getMysqlConn();
        PreparedStatement st = null;
        ResultSet rs = null;
        exam.seteTerm(SystemInfo.CUR_TERM);
        try {
            String sql0 = "select eUid from s_exam where eName = ? and eBuilding = ? and eRoom = ? and eWeekNum = ? and eWeekDay = ? and eTime = ? and eTerm = ?;";
            st = con.prepareStatement(sql0);
            st.setString(1, exam.geteName());
            st.setString(2, exam.geteBuilding());
            st.setString(3, exam.geteRoom());
            st.setInt(4, exam.geteWeekNum());
            st.setInt(5, exam.geteWeekDay());
            st.setString(6, exam.geteTime());
            st.setString(7, exam.geteTerm());
            rs = st.executeQuery();
            if (rs.next()) {//已存在该考试安排
                exam.seteUid(rs.getInt(1));
                saveExistedExam(con, uid, exam);
            } else {
                saveNewExam(con, exam);
                saveExistedExam(con, uid, exam);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            C3P0Util.closeAll(rs, st, con);
        }
    }

    private static void saveExistedExam(Connection con, String uid, ExamBean exam) throws SQLException {
        String sql = "insert ignore into s_usr_exam (uUid, eUid) values(?,?);";
        int eUid = exam.geteUid();
        PreparedStatement st = con.prepareStatement(sql);
        st.setString(1, uid);
        st.setInt(2, eUid);
        st.execute();
        C3P0Util.closeAll(null, st, null);
    }

    private static void saveNewExam(Connection con, ExamBean exam) throws SQLException {
        String sql = "insert into s_exam (eName, eBuilding, eRoom, eWeekNum, eWeekDay, eTime, eTerm) values(?,?,?,?,?,?,?);";
        PreparedStatement st = con.prepareStatement(sql);
        st.setString(1, exam.geteName());
        st.setString(2, exam.geteBuilding());
        st.setString(3, exam.geteRoom());
        st.setInt(4, exam.geteWeekNum());
        st.setInt(5, exam.geteWeekDay());
        st.setString(6, exam.geteTime());
        st.setString(7, exam.geteTerm());
        st.execute();
        C3P0Util.closeAll(null, st, null);
        String sql3 = "select last_insert_id() from s_exam;";
        st = con.prepareStatement(sql3);
        ResultSet rs = st.executeQuery();
        rs.next();
        int lasteUid = rs.getInt(1);
        C3P0Util.closeAll(rs, st, null);
        exam.seteUid(lasteUid);
    }

    public static List<ExamBean> getExamList(String uid) {
        Connection con = C3P0Util.getMysqlConn();
        PreparedStatement st = null;
        ResultSet rs = null;
        List<ExamBean> examList = new ArrayList<>();
        try {
            String sql = "select eName, eBuilding, eRoom, eWeeknum, eWeekday, eTime, eTerm " +
                    "from s_usr_exam, s_exam " +
                    "where s_usr_exam.uUid=? and s_usr_exam.eUid=s_exam.eUid";
            st = con.prepareStatement(sql);
            st.setString(1, uid);
            rs = st.executeQuery();
            while(rs.next()) {
                ExamBean exam = new ExamBean();
                exam.seteName(rs.getString(1));
                exam.seteBuilding(rs.getString(2));
                exam.seteRoom(rs.getString(3));
                exam.seteWeekNum(rs.getInt(4));
                exam.seteWeekDay(rs.getInt(5));
                exam.seteTime(rs.getString(6));
                exam.seteTerm(rs.getString(7));
                examList.add(exam);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            C3P0Util.closeAll(rs, st, con);
        }
        return examList;
    }

    /**
     * 下面是课表相关操作
     */

    public static void deleteTimeTable(String uid) {
        Connection con = C3P0Util.getMysqlConn();
        PreparedStatement st = null;
        try {
            String sql = "delete from s_usr_crs where uUid=?;";
            st = con.prepareStatement(sql);
            st.setString(1, uid);
            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            C3P0Util.closeAll(null, st, con);
        }
    }

    public static void saveCourse(String uid, CourseBean course) {
        Connection con = C3P0Util.getMysqlConn();
        PreparedStatement st = null;
        ResultSet rs = null;
        //course.setcTerm(SystemInfo.CUR_TERM);
        try {
            String sql0 = "select cUid from s_course where cNo = ? and cSeq = ? and cTerm = ?;";
            st = con.prepareStatement(sql0);
            st.setString(1, course.getcNo());
            st.setInt(2, course.getcSeq());
            st.setString(3, course.getcTerm());
            rs = st.executeQuery();
            if (rs.next()) {//已存在该课程
                course.setcUid(rs.getInt(1));
                saveExistedCourse(con, course, uid);
            } else {
                saveNewCourse(con, course);
                saveExistedCourse(con, course, uid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            C3P0Util.closeAll(rs, st, con);
        }
    }

    private static void saveExistedCourse(Connection con, CourseBean course, String uid) throws SQLException {
        String sql = "insert ignore into s_usr_crs (uUid, cUid) values(?,?);";
        int cUid = course.getcUid();
        PreparedStatement st = con.prepareStatement(sql);
        st.setString(1, uid);
        st.setInt(2, cUid);
        st.execute();
        C3P0Util.closeAll(null, st, null);
    }

    private static void saveNewCourse(Connection con, CourseBean course) throws SQLException {
        // 所以要确保插入和获取两个操作之间不被打断
        if(course.getcTeacher().length() > 25)
            course.setcTeacher(course.getcTeacher().substring(0, 25));
        String sql = "insert into s_course (cNo, cSeq, cTerm, cName, cCredit, cAttr, cTeacher) values(?,?,?,?,?,?,?);";
        PreparedStatement st = con.prepareStatement(sql);
        st.setString(1, course.getcNo());
        st.setInt(2, course.getcSeq());
        st.setString(3, course.getcTerm());
        st.setString(4, course.getcName());
        st.setString(5, course.getcCredit());
        st.setString(6, course.getcAttr());
        st.setString(7, course.getcTeacher());
        st.execute();
        C3P0Util.closeAll(null, st, null);
        String sql3 = "select last_insert_id() from s_course;";
        st = con.prepareStatement(sql3);
        ResultSet rs = st.executeQuery();
        rs.next();
        int lastcUid = rs.getInt(1);
        C3P0Util.closeAll(rs, st, null);
        course.setcUid(lastcUid);
        String sql2 = "insert into s_schedule(cUid,scWeeks,scWeekDay,scStartSec,scSecNum,scBuilding,scRoom)" +
                " values(?,?,?,?,?,?,?);";
        st = con.prepareStatement(sql2);
        List<CourseScheduleBean> scheduleList = course.getCourseScheduleList();
        st.setInt(1, lastcUid);
        for (CourseScheduleBean courseSchedule : scheduleList) {
            st.setString(2, courseSchedule.getWeeks());
            st.setInt(3, courseSchedule.getWeekDay());
            st.setInt(4, courseSchedule.getStartSec());
            st.setInt(5, courseSchedule.getSecNum());
            st.setString(6, courseSchedule.getBuilding());
            st.setString(7, courseSchedule.getRoom());
            st.execute();
        }
        C3P0Util.closeAll(null, st, null);
    }

    public static CourseScheduleBean[][][] getTimeTable(String uid, String term) {
        Connection con = C3P0Util.getMysqlConn();
        PreparedStatement st = null;
        ResultSet rs = null;
        List<CourseBean> courseList = new ArrayList<>();
        try {
            String sql = "select s_course.cUid,s_course.cNo,s_course.cSeq,cName,cCredit,cAttr,cTeacher" +
                    " from s_usr_crs,s_course " +
                    "where s_usr_crs.uUid = ? and s_course.cUid = s_usr_crs.cUid and s_course.cTerm = ?;";
            st = con.prepareStatement(sql);
            st.setString(1, uid);
            st.setString(2, term);
            rs = st.executeQuery();
            while (rs.next()) {
                CourseBean course = new CourseBean();
                course.setcUid(rs.getInt(1));
                course.setcNo(rs.getString(2));
                course.setcSeq(rs.getInt(3));
                course.setcTerm(SystemInfo.CUR_TERM);
                course.setcName(rs.getString(4));
                course.setcCredit(rs.getString(5));
                course.setcAttr(rs.getString(6));
                course.setcTeacher(rs.getString(7));
                courseList.add(course);
            }
            C3P0Util.closeAll(rs, st, null);
            if (courseList.size() == 0)//用户还未保存课表
                return null;
            String sql2 = "select scWeeks,scWeekDay,scStartSec,scSecNum,scBuilding,scRoom " +
                    "from s_schedule where cUid = ?;";
            st = con.prepareStatement(sql2);
            for (CourseBean course : courseList) {
                st.setInt(1, course.getcUid());
                rs = st.executeQuery();
                while (rs.next()) {
                    CourseScheduleBean courseSchedule = new CourseScheduleBean();
                    courseSchedule.setCourse(course);
                    courseSchedule.setWeeks(rs.getString(1));
                    courseSchedule.setWeekDay(rs.getInt(2));
                    courseSchedule.setStartSec(rs.getInt(3));
                    courseSchedule.setSecNum(rs.getInt(4));
                    courseSchedule.setBuilding(rs.getString(5));
                    courseSchedule.setRoom(rs.getString(6));
                    course.getCourseScheduleList().add(courseSchedule);
                }
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            C3P0Util.closeAll(rs, st, con);
        }
        return getTimeTableByCourseList(courseList);
    }

    private static CourseScheduleBean[][][] getTimeTableByCourseList(List<CourseBean> courseList) {
        CourseScheduleBean[][][] scheduleTable = new CourseScheduleBean[12][7][5];
        for (CourseBean course : courseList) {
            for (CourseScheduleBean courseSchedule : course.getCourseScheduleList()) {
                processSchedule(scheduleTable, courseSchedule);
            }
        }
        return scheduleTable;
    }

    private static void processSchedule(CourseScheduleBean[][][] scheduleTable, CourseScheduleBean courseSchedule) {
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

    /**
     * 下面是空教室查询相关操作
     */

    public static List<RoomBean> getEmptyRoomBySecNum(int buildingNo, int weekNum, int weekDay, int secNum) {
        List<RoomBean> roomList = new ArrayList<>();
        Connection con = C3P0Util.getSQLiteConn();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            String sql = "select s_room.* from s_room,s_room_schedule " +
                    "where s_room_schedule.buildingNo=? " +
                    "and s_room_schedule.weekNum=? " +
                    "and s_room_schedule.weekDay=? " +
                    "and s_room_schedule.secNum=? " +
                    "and s_room.buildingNo=s_room_schedule.buildingNo and s_room.roomNo=s_room_schedule.roomNo;";
            st = con.prepareStatement(sql);
            st.setInt(1, buildingNo);
            st.setInt(2, weekNum);
            st.setInt(3, weekDay);
            st.setInt(4, secNum);
            rs = st.executeQuery();
            while (rs.next()) {
                RoomBean room = new RoomBean();
                room.setRoomNo(rs.getString(2));
                room.setRoomType(rs.getString(3));
                room.setSeatNum(rs.getInt(4));
                roomList.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            C3P0Util.closeAll(rs, st, con);
        }
        return roomList;
    }

    public static List<RoomBean> getEmptyRoomByDay(int buildingNo, int weekNum, int weekDay) {
        Map<String, RoomBean> roomMap = new HashMap<>();
        List<RoomBean> roomList = new ArrayList<>();
        Connection con = C3P0Util.getSQLiteConn();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            String sql = "select s_room.*,s_room_schedule.secNum from s_room,s_room_schedule " +
                    "where s_room_schedule.buildingNo=? " +
                    "and s_room_schedule.weekNum=? " +
                    "and s_room_schedule.weekDay=? " +
                    "and s_room.buildingNo=s_room_schedule.buildingNo and s_room.roomNo=s_room_schedule.roomNo;";
            st = con.prepareStatement(sql);
            st.setInt(1, buildingNo);
            st.setInt(2, weekNum);
            st.setInt(3, weekDay);
            rs = st.executeQuery();
            while (rs.next()) {
                String roomNo = rs.getString(2);
                RoomBean room;
                boolean[] available;
                if (roomMap.containsKey(roomNo)) {
                    room = roomMap.get(roomNo);
                    available = room.getAvailable();
                } else {
                    room = new RoomBean();
                    available = new boolean[13];
                    room.setRoomNo(roomNo);
                    room.setRoomType(rs.getString(3));
                    room.setSeatNum(rs.getInt(4));
                    room.setAvailable(available);
                    roomMap.put(roomNo, room);
                }
                int secNum = rs.getInt(5);
                available[secNum] = true;
                room.setSecNum(room.getSecNum() + 1);
            }
            for (Map.Entry<String, RoomBean> roomEntry : roomMap.entrySet()) {
                roomList.add(roomEntry.getValue());
            }
            roomList.sort((o1, o2) -> o2.getSecNum() - o1.getSecNum());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            C3P0Util.closeAll(rs, st, con);
        }
        return roomList;
    }

    /**
     * 下面是班级课表查询相关操作
     */

    public static String[][] getClassTimeTable(String termNo, String classNo) {
        Connection con = C3P0Util.getSQLiteConn();
        PreparedStatement st = null;
        ResultSet rs = null;
        String sql = "select cCourse, cRow, cCol from s_class_course where cTerm=? and cClass=?";
        String[][] timeTable = new String[12][7];
        try{
            st = con.prepareStatement(sql);
            st.setString(1, termNo);
            st.setString(2, classNo);
            rs = st.executeQuery();
            int row, col;
            while(rs.next()) {
                String course = rs.getString(1);
                row = rs.getInt(2);
                col = rs.getInt(3);
                if(row >= 0 && row < 12 && col >= 0 && col < 7)
                    timeTable[row][col] = course;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            C3P0Util.closeAll(rs, st, con);
        }
        return timeTable;
    }

}
