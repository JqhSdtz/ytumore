

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.*;

public class FileUtil {

    private static PrintWriter roomWriter;
    private static PrintWriter scheduleWriter;
    private static PrintStream outStream;

    public static void init() throws FileNotFoundException {
        File roomFile = new File("crawlEmptyRoomInfo/src/room.txt");
        File scheduleFile = new File("crawlEmptyRoomInfo/src/schedule.txt");
        File outFile = new File("crawlEmptyRoomInfo/src/out.txt");
        roomWriter = new PrintWriter(roomFile);
        scheduleWriter = new PrintWriter(scheduleFile);
        outStream = new PrintStream(outFile);
        writeRoomStr("insert into s_room(buildingNo, roomNo, roomType, seatNum) values ");
        writeScheduleStr("insert into s_room_schedule(buildingNo, roomNo, weekNum, weekDay, secNum) values ");
        System.setOut(outStream);
    }

    public static void close() {
        roomWriter.close();
        scheduleWriter.close();
        outStream.close();
    }

    private static synchronized void writeRoomStr(String str){
        roomWriter.write(str);
    }

    private static synchronized void writeScheduleStr(String str){
        scheduleWriter.write(str);
    }

    public static void saveRoomInfo(int buildingNo, RoomBean room) {
        String str = String.format("(%d,'%s','%s',%d),\n",
                buildingNo, room.getRoomNo(), room.getRoomType(), room.getSeatNum());
        writeRoomStr(str);
    }

    public static void saveScheduleInfo(int buildingNo, int weekNum, int weekDay, int secNum, RoomBean room) {
        String str = String.format("(%d,'%s',%d,%d,%d),\n",
                buildingNo, room.getRoomNo(), weekNum, weekDay, secNum);
        writeScheduleStr(str);
    }
}
