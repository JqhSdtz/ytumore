

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class FileUtil {

    private static PrintWriter writer;

    public static void init() throws FileNotFoundException {
        File outFile = new File("changeDB/src/out.txt");
        writer = new PrintWriter(outFile);
    }

    public static void close() {
        writer.close();
    }

    public static synchronized void write(String str){
        writer.write(str);
    }

}
