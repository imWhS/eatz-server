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

        return mailSender;
    }

    private Properties javaMailProperties() {
        Properties javaMailProperties = new Properties();
        MailConfigProperties.Properties properties = mailConfigProperties.getProperties();
        javaMailProperties.put("mail.debug", properties.getMail().getSmtp().isDebug());
        javaMailProperties.put("mail.smtp.auth", properties.getMail().getSmtp().isAuth());
        javaMailProperties.put("mail.smtp.starttls.enable", properties.getMail().getSmtp().getStarttls().isEnable());
        javaMailProperties.put("mail.smtp.connectiontimeout", properties.getMail().getSmtp().getConnectiontimeout());
        javaMailProperties.put("mail.smtp.timeout", properties.getMail().getSmtp().getTimeout());
        javaMailProperties.put("mail.smtp.writetimeout", properties.getMail().getSmtp().getWritetimeout());

        return javaMailProperties;
    }

}
