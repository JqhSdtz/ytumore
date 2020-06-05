package util;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailUtil {
    public static void sendMail(String msg) {
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");// 连接协议
        properties.put("mail.smtp.host", "smtp.qq.com");// 主机名
        properties.put("mail.smtp.port", 465);// 端口号
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");// 设置是否使用ssl安全连接 ---一般都使用
        //properties.put("mail.debug", "true");// 设置是否显示debug信息 true 会在控制台显示相关信息
        Session session = Session.getInstance(properties);
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress("jiangqihan_sdtz@qq.com"));
            //message.setRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress("xxx@qq.com"),new InternetAddress("xxx@qq.com"),new InternetAddress("xxx@qq.com")});
            message.setRecipient(Message.RecipientType.TO, new InternetAddress("920444182@qq.com"));//一个收件人
            message.setSubject("from ytumore");
            message.setText(msg);
            Transport transport = session.getTransport();
            transport.connect("jiangqihan_sdtz@qq.com", "cjnaaxbpnxgibdca");// 密码为QQ邮箱开通的stmp服务后得到的客户端授权码
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
