/**
 * @author JQH
 * @since 下午 5:21 19/09/09
 */
public class Main {
    public static void main(String[] args) {
        try {
            C3P0Util.init();
            DBUtil.resortSid();
            C3P0Util.closeDataSource();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
