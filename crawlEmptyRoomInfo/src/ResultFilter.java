import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author JQH
 * @since 下午 12:31 19/09/20
 */
public class ResultFilter {
    private static List<File> fileList = new ArrayList<>();
    private static Set<String> resultSet = new HashSet<>();
    private static PrintWriter resultOut;
    public static void filter() {
        try {
            init();
            doFilter();
            writeOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void init() throws Exception{
        fileList.add(new File("crawlEmptyRoomInfo/src/bak/unfiltered/room.sql"));
        fileList.add(new File("crawlEmptyRoomInfo/src/bak/unfiltered/room2.sql"));
        resultOut = new PrintWriter(new File("crawlEmptyRoomInfo/src/bak/unfiltered/result.sql"));
    }

    private static void doFilter() throws Exception{
        for(File file: fileList) {
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader);
            while(scanner.hasNext()) {
                resultSet.add(scanner.nextLine());
            }
        }
    }

    private static void writeOut() {
        for(String str: resultSet) {
            resultOut.println(str);
        }
        resultOut.close();
    }

}
