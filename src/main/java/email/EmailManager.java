package email;

import dao.UserDAO;
import game.Game;
import order.Order;
import user.Customer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailManager {
    private static final String config = "src/main/resources/mail_config.properties";
    private static final Properties configuration = new Properties();

    private static Properties setProperties(){
        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        return properties;
    }

    private static Session getSession() throws IOException {
        Properties systemProperties = setProperties();

        try (InputStream input = new FileInputStream(config)) {
            configuration.load(input);

            return Session.getInstance(systemProperties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(configuration.getProperty("mail.sender"), configuration.getProperty("mail.password"));
                }
            });
        }
    }

    private static String generateKey() {
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private static String getMessage(Customer customer, Order order) {
        int i = 1;
        StringBuilder message = new StringBuilder("""
                <h1 style="font-size: 3em;">Hi %s!</h1>
                <p style="font-size: 1.2em;">Thanks for your purchase from Game Store.<br>
                Here you have the keys for your products.</p>"""
                .formatted(customer.getFirstName()));
        message.append("<p style=\"font-size: 1.2em;\">");

        for (Game key : order.getOrder().keySet()){
            int quantity = order.getOrder().get(key);
            while(quantity-- != 0) {
                message.append("<br>").append(i++).append(". ");
                message.append(key.getName()).append(": ");
                message.append("<b>").append(generateKey()).append("</b>");
            }
        }
        message.append("</p>");

        return message.toString();
    }

    public static void sendEmail(Customer customer, Order order) throws MessagingException, IOException {
        Session session = getSession();

        MimeMessage message = new MimeMessage(session);
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        Multipart multipart = new MimeMultipart();
        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        String msg = getMessage(customer, order);
        File pdf = new File(new Invoice(customer, order).getPath());

        message.setFrom(new InternetAddress(configuration.getProperty("mail.sender")));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(customer.getEmail()));
        message.setSubject("Your Game Store Receipt");

        messageBodyPart.setContent(msg, "text/html; charset=utf-8");

        multipart.addBodyPart(messageBodyPart);
        attachmentBodyPart.attachFile(pdf);
        multipart.addBodyPart(attachmentBodyPart);

        message.setContent(multipart);
        message.saveChanges();

        Transport.send(message);

        Files.deleteIfExists(Path.of(pdf.getAbsolutePath()));
    }

    public static void main(String[] args) {
        try {
            var c = (Customer)new UserDAO().checkLogin("marius_bubui@yahoo.com", "nzaf5y");
            var o = c.getOrders().get(0);
            EmailManager.sendEmail(c, o);
        } catch (SQLException | MessagingException | IOException e) {
            e.printStackTrace();
        }

    }

}
