package com.example.borrowingbooks.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationCode(String toEmail, String code) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        try {
            helper.setFrom("2761162439@qq.com");
            helper.setTo(toEmail);
            helper.setSubject("验证码邮件");
            helper.setText("您的验证码是：" + code, true);

            logger.info("准备发送邮件至：{}，验证码：{}", toEmail, code);
            mailSender.send(message);
            logger.info("邮件发送成功至：{}", toEmail);
        } catch (Exception e) {
            logger.error("邮件发送失败！", e);
            throw new Exception("邮件发送失败", e);
        }
    }
}

