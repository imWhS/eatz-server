package imwhs.eatz_server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender mailSender;

//    @Value("${spring.mail.sender-email}")
//    private String senderEmail;

    public void sendMail(String to, String subject, String content) {
        SimpleMailMessage message = createSimpleMailMessage(to, subject, content);

        try {
            log.info("이메일 주소로 편지를 발송할게요. | {}", to);
            mailSender.send(message);
        } catch (RuntimeException e) {
            log.error("메일 발송 실패", e);
            throw new RuntimeException("메일을 전송하지 못했어요: " + e.getMessage());
        }
    }

    private SimpleMailMessage createSimpleMailMessage(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom(senderEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        return message;
    }

}
