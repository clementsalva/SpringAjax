package pharmacie.rest;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pharmacie.dto.SendEmailRequest;
import pharmacie.service.SendGridEmailService;

@RestController
public class SendEmailController {

    private final SendGridEmailService sendGridEmailService;

    public SendEmailController(SendGridEmailService sendGridEmailService) {
        this.sendGridEmailService = sendGridEmailService;
    }

    @PostMapping(path = "/send-email")
    public ResponseEntity<Map<String, String>> sendEmail(@Valid @RequestBody SendEmailRequest request) {
        sendGridEmailService.sendEmail(request.to(), request.subject(), request.body());
        return ResponseEntity.ok(Map.of("message", "Email envoye"));
    }
}
