

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class DBUtil {

    private static PrintWriter courseWriter;
    private static PrintStream outStream;

    public static void init() throws FileNotFoundException {
        File roomFile = new File("crawlClassTimeTable/src/course.txt");
        File outFile = new File("crawlClassTimeTable/src/out.txt");
        courseWriter = new PrintWriter(roomFile);
        outStream = new PrintStream(outFile);
        writeCourseStr("INSERT INTO s_class_course (cTerm, cClass, cCourse, cRow, cCol) VALUES ");
        System.setOut(outStream);
    }

    public static void close() {
        courseWriter.close();
        outStream.close();
    }

    private static synchronized void writeCourseStr(String str){
        courseWriter.write(str);
    }


    public static void saveCourseInfo(String termStr, String classNo, String course, int row, int col) {
        String str = String.format("('%s','%s','%s',%d,%d),\n", termStr, classNo, course, row, col);
        writeCourseStr(str);
    }
}
