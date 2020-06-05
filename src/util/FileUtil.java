package util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

/**
 * @author JQH
 * @since 下午 9:10 19/11/14
 */
public class FileUtil {
    public static void readSetFromFile(Set<String> set, String fileName) {
        try {
            FileReader fileReader = new FileReader(FileUtil.class.getResource("/").getPath() + fileName);
            Scanner scanner = new Scanner(fileReader);
            int size = Integer.valueOf(scanner.nextLine());
            for(int i = 0; i < size; ++i)
                set.add(scanner.nextLine());
            scanner.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeSetToFile(Set<String> set, String fileName) {
        try {
            File file = new File(FileUtil.class.getResource("/").getPath() + fileName);
            if (!file.exists())
                file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(set.size() + "\n");
            for(String str: set) {
                fileWriter.write(str + "\n");
            }
            fileWriter.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
