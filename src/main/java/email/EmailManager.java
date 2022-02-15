package email;

import game.Game;
import order.Order;
import user.Customer;

import java.io.*;
import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;

/**
 *
 */
public class EmailManager {
    /**
     *
     */
    private static final String config = "src/main/resources/mail_config.properties";

    /**
     *
     */
    private static final Properties configuration = new Properties();

    /**
     *
     */
    private static Properties setProperties(){
        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        return properties;
    }

    /**
     *
     */
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

    /**
     *
     */
    public static String generateKey() {
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

    /**
     *
     */
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

    /**
     *
     */
    public static MimeMessage getInvoiceMessage(Customer customer, Order order) throws IOException, MessagingException {
        Session session = getSession();

        MimeMessage message = new MimeMessage(session);
        MimeBodyPart attachmentBodyPart = new MimeBodyPart();

        message.setFrom(new InternetAddress(configuration.getProperty("mail.sender")));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(customer.getEmail()));
        message.setSubject("Your Game Store Receipt");

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        String msg = getMessage(customer, order);
        messageBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        File pdf = new File(new Invoice(customer, order).getPath());

        attachmentBodyPart.attachFile(pdf);
        multipart.addBodyPart(attachmentBodyPart);

        message.setContent(multipart);
        message.saveChanges();

        return message;
    }

    /**
     *
     */
    public static MimeMessage getConfirmationMessage(String email, String code) throws IOException, MessagingException {
        Session session = getSession();

        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(configuration.getProperty("mail.sender")));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        message.setSubject("Verify your account");

        MimeBodyPart messageBodyPart = new MimeBodyPart();

        String msg = """
                <h1 style="font-size: 2em;">Confirm your email address</h1>
                <p style="font-size: 1.2em;">Please use the code bellow to confirm your account:<br></p>
                <b style="font-size: 1.1em;">%s</b><p style="font-size: 1.2em;">Cheers,<br>The GameStore Team</p>""".formatted(code);

        messageBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        message.setContent(multipart);
        message.saveChanges();

        return message;
    }

    /**
     *
     */
    public static void sendEmail(MimeMessage message) throws MessagingException {
        Transport.send(message);
        //Files.deleteIfExists(Path.of(pdf.getAbsolutePath()));
    }
}
