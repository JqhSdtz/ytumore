import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author JQH
 * @since 下午 5:24 19/09/09
 */
public class DBUtil {
    static void resortSid() throws Exception {
        FileUtil.init();
        Connection con = C3P0Util.getMysqlConn();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            String sql0 = "select count(*) from s_user;";
            st = con.prepareStatement(sql0);
            rs = st.executeQuery();
            rs.next();
            int num = rs.getInt(1);
            String sql = "select uid from s_user;";
            st = con.prepareStatement(sql);
            rs = st.executeQuery();
            String sql2 = "update s_user set sid=? where uid=?";
            st = con.prepareStatement(sql2);
            for (int i = 0; i < num; ++i) {
                rs.next();
                String uid = rs.getString(1);
                st.setInt(1, getHashSidOfUid(uid));
                st.setString(2, uid);
                st.execute();
                //FileUtil.write(rs.getString(1) + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            C3P0Util.closeAll(rs, st, con);
        }
        FileUtil.close();
    }

    public static int getHashSidOfUid(String uid) {
        int[] a = new int[5];
        a[0] = Integer.valueOf(uid.substring(2, 4));
        a[1] = Integer.valueOf(uid.substring(4, 7));
        a[2] = Integer.valueOf(uid.substring(7, 9));
        a[3] = Integer.valueOf(uid.substring(9, 10));
        a[4] = Integer.valueOf(uid.substring(10, 12));
        return getHashValue(a);
    }

    private static int getHashValue(int a[]) {
        int hash = a[4], prime = 2399;
        for (int i = 0; i < a.length; ++i)
            hash = (hash << 4) ^ (hash >> 28) ^ a[i];
        hash = hash < 0 ? hash * -1 : hash;
        return hash % prime;
    }

}
