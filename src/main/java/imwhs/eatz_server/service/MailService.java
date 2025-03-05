package imwhs.eatz_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender mailSender;

    public void sendMail(String to, String subject, String content) {
        SimpleMailMessage message = createSimpleMailMessage(to, subject, content);

        try {
            mailSender.send(message);
        } catch (RuntimeException e) {
            throw new RuntimeException("메일을 전송하지 못했어요: " + e.getMessage());
        }
    }

    private SimpleMailMessage createSimpleMailMessage(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        return message;
    }

}
