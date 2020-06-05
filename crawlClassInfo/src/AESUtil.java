
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AESUtil {
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/NoPadding";//默认的加密算法

    /**
     * AES 加密操作
     *
     * @param content  待加密内容
     * @param key 加密密码
     * @return 返回Base64转码后的加密数据
     */
    public static String encrypt(String content, String key) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes("UTF-8"), "AES"));// 初始化为加密模式的密码器
            byte[] result = cipher.doFinal(byteContent);// 加密
            return Base64.encodeBase64String(result);//通过Base64转码返回
        } catch (Exception ex) {
            Logger.getLogger(AESUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * AES 解密操作
     *
     * @param content
     * @param key
     * @return
     */
    public static String decrypt(String content, String key) {
        try {
            //实例化
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            //使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes("UTF-8"), "AES"));
            //执行操作
            byte[] base64 = Base64.decodeBase64(content);
            byte[] result = cipher.doFinal(base64);
            return new String(result, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成加密秘钥
     *
     * @return
     */
//    private static SecretKeySpec getSecretKey(final String key) {
//        //返回生成指定算法密钥生成器的 KeyGenerator 对象
//        KeyGenerator kg = null;
//        try {
//            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
//            random.setSeed(key.getBytes("utf-8"));
//            kg = KeyGenerator.getInstance(KEY_ALGORITHM);
//            //AES 要求密钥长度为 128
//            kg.init(128, random);
//            //生成一个密钥
//            SecretKey secretKey = kg.generateKey();
//            return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);// 转换为AES专用密钥
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
