package io.adopteunops.etl.rules.metrics.processor;

import io.adopteunops.etl.rules.metrics.domain.Keys;
import io.adopteunops.etl.rules.metrics.domain.MetricResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@AllArgsConstructor
@Slf4j
public class MetricsEmailProcessor extends AbstractProcessor<Keys, MetricResult> {

    private final JavaMailSender javaMailSender;
    private final String destinationMail;

    @Override
    public void process(Keys key, MetricResult value) {

        // Create a thread safe "copy" of the template message and customize it
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(destinationMail);
        //TODO: should be replaced via a template engine such as velocity freemarker or thymeleaf
        msg.setText(value.getRuleName() + " triggered with value " + value.getResult());
        try {
            this.javaMailSender.send(msg);
        } catch (MailException ex) {
            log.error("can't send mail to " + destinationMail + " in " + value.getRuleName(), ex);
        }
    }
}
