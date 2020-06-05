import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author JQH
 * @since 下午 11:08 19/09/20
 */
public class RetryFilter {
    private static Scanner scanner;
    private static String testPattern = "parse building \\d+, weekNum \\d+, weekDay \\d+, secNum \\d+ failed!";
    private static String patternStr = "\\d+";
    private static Pattern pattern;

    public static void doFilter() {
        try {
            init();
            filter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void init() throws Exception {
        FileReader fileReader = new FileReader(new File("crawlEmptyRoomInfo/src/bak/unfiltered/retry.txt"));
        scanner = new Scanner(fileReader);
        pattern = Pattern.compile(patternStr);
    }

    private static void filter() throws Exception{
        EXE4.init();
        while (scanner.hasNext()) {
            String str = scanner.nextLine();
            if(!str.matches(testPattern))
                continue;
            Matcher matcher = pattern.matcher(str);
            int[] args = new int[4];
            int i = 0;
            while (matcher.find()) {
                args[i] = Integer.parseInt(matcher.group());
                ++i;
            }
            //System.out.println(args[0] + " " +  args[1] + " " +  args[2] + " " +  args[3]);
            EXE4.getRoomInfoWithFullArgs(args[0], args[1], args[2], args[3]);
        }
        EXE4.waitToClose();
    }

}
