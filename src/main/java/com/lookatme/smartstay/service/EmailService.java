package com.lookatme.smartstay.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    //받은사람 주소, 제목, 내용
    public void sendEmail(String to, String subject, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        String from = "운영자<hyr8433@gmail.com>";

        message.setFrom(from); //보내는 사람
        message.setTo(to); //받은 사람
        message.setSubject(subject); //제목
        message.setText(text); //내용

        try{
            javaMailSender.send(message); //java에서 메일 전송
            System.out.println("전송완료!");
        } catch (MailException e) {
            //메일 보내기 실패시
            System.out.println("전송 오류");
        }
    }
}
