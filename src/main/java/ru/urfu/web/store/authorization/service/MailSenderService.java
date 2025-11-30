package ru.urfu.web.store.authorization.service;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.logging.log4j.util.Strings;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.urfu.web.store.authorization.repository.RedisMailVerificationRepository;

import java.util.Random;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class MailSenderService {
    private final RedisMailVerificationRepository redisMailVerificationRepository;
    private final JavaMailSender mailSender;
    private final Random random = new Random();

    public final int VERIFICATION_CODE_LENGTH = 6;

    public void processSendMailEvent(String email) {
        var verificationCode = generateAndGetCode(email);
        sendVerificationCode(email, verificationCode);
    }

    private int generateAndGetCode(String email) {
        int verificationCode = Integer.parseInt(
                IntStream.generate(() -> random.nextInt(11))
                        .limit(VERIFICATION_CODE_LENGTH)
                        .boxed()
                        .map(Object::toString)
                        .reduce(Strings.EMPTY, (first, second) -> first + second)
        );
        redisMailVerificationRepository.put(
                email,
                verificationCode
        );
        return verificationCode;
    }

    @SneakyThrows
    public void sendVerificationCode(String toEmail, Integer verificationCode) {
        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(toEmail);
        helper.setFrom("mesler.roman@yandex.ru");
        helper.setSubject("Код верификации");
        String htmlContent = """
            <html>
                <body>
                    <h2>Ваш код верификации для входа в аккаунт</h2>
                    <p style="font-size: 24px; font-weight: bold; color: #2563eb;">
                        %s
                    </p>
                    <p>Код действителен в течение 10 минут.</p>
                </body>
            </html>
            """.formatted(verificationCode);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}
