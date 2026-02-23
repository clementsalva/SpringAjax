package pharmacie.rest;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.StringUtils;

import pharmacie.dto.SendEmailRequest;
import pharmacie.service.SendGridEmailService;

@RestController
public class SendEmailController {

    private final SendGridEmailService sendGridEmailService;

    public SendEmailController(SendGridEmailService sendGridEmailService) {
        this.sendGridEmailService = sendGridEmailService;
    }

    @PostMapping(path = { "/mail/send", "/send-email" })
    public ResponseEntity<Map<String, String>> sendEmail(
            @RequestBody(required = false) SendEmailRequest request,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String body) {

        String resolvedTo = firstNonBlank(request == null ? null : request.to(), to);
        String resolvedSubject = firstNonBlank(request == null ? null : request.subject(), subject);
        String resolvedBody = firstNonBlank(request == null ? null : request.body(), body);

        if (!StringUtils.hasText(resolvedTo) || !StringUtils.hasText(resolvedSubject) || !StringUtils.hasText(resolvedBody)) {
            throw new IllegalArgumentException("Fournir to, subject, body via JSON ou query params.");
        }

        sendGridEmailService.sendEmail(resolvedTo, resolvedSubject, resolvedBody);
        return ResponseEntity.ok(Map.of("message", "Mail envoye a " + resolvedTo));
    }

    private String firstNonBlank(String primary, String fallback) {
        return StringUtils.hasText(primary) ? primary : fallback;
    }
}
