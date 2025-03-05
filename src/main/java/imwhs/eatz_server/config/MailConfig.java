package imwhs.eatz_server.config;

import imwhs.eatz_server.config.properties.MailConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MailConfig {

    private final MailConfigProperties mailConfigProperties;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailConfigProperties.getHost());
        mailSender.setPort(mailConfigProperties.getPort());
        mailSender.setUsername(mailConfigProperties.getUsername());
        mailSender.setPassword(mailConfigProperties.getPassword());
        mailSender.setDefaultEncoding("UTF-8");
        mailSender.setJavaMailProperties(javaMailProperties());

        log.info("[Mail Config] SMTP Host: {}", mailConfigProperties.getHost());
        log.info("[Mail Config] SMTP Port: {}", mailConfigProperties.getPort());
        log.info("[Mail Config] SMTP Username: {}", mailConfigProperties.getUsername());
        log.info("[Mail Config] SMTP Password: {}", mailConfigProperties.getPassword());

        return mailSender;
    }

    private Properties javaMailProperties() {
        Properties javaMailProperties = new Properties();
        MailConfigProperties.Properties properties = mailConfigProperties.getProperties();

//        logProperty(javaMailProperties, "mail.debug", properties.getMail().getSmtp().isDebug());
        javaMailProperties.put("mail.debug", "true"); // <<<<<<<<<<<<<< 추가
        logProperty(javaMailProperties, "mail.smtp.auth", properties.getMail().getSmtp().isAuth());
        logProperty(javaMailProperties, "mail.smtp.starttls.enable", properties.getMail().getSmtp().getStarttls().isEnable());
        logProperty(javaMailProperties, "mail.smtp.connectiontimeout", properties.getMail().getSmtp().getConnectiontimeout());
        logProperty(javaMailProperties, "mail.smtp.timeout", properties.getMail().getSmtp().getTimeout());
        logProperty(javaMailProperties, "mail.smtp.writetimeout", properties.getMail().getSmtp().getWritetimeout());

        javaMailProperties.put("mail.debug", properties.getMail().getSmtp().isDebug());
        javaMailProperties.put("mail.smtp.auth", properties.getMail().getSmtp().isAuth());
        javaMailProperties.put("mail.smtp.starttls.enable", properties.getMail().getSmtp().getStarttls().isEnable());
        javaMailProperties.put("mail.smtp.connectiontimeout", properties.getMail().getSmtp().getConnectiontimeout());
        javaMailProperties.put("mail.smtp.timeout", properties.getMail().getSmtp().getTimeout());
        javaMailProperties.put("mail.smtp.writetimeout", properties.getMail().getSmtp().getWritetimeout());
        return javaMailProperties;
    }

    private void logProperty(Properties props, String key, Object value) {
        log.info("[Mail Property] {} = {}", key, value);
    }


}
