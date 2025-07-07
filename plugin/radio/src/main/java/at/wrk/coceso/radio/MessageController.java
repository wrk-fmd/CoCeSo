package at.wrk.coceso.radio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/data/radio/messages")
public class MessageController {
    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);

    private final RadioService radioService;

    private final String authToken;

    @Autowired
    public MessageController(final RadioService radioService, final Environment env) {
        this.radioService = radioService;
        this.authToken = env.getProperty("radio.authenticationToken");
        if (this.authToken == null) {
            LOG.warn("No authentication token for radio message API configured! All incoming messages will be silently dropped.");
        }
    }

    @PostMapping
    public void receiveMessage(@RequestBody final IncomingMessageDto message, @RequestHeader("Authorization") final String key) {
        String authKey = key != null ? key.replaceFirst("Bearer ", "").trim() : null;
        if (authToken != null && Objects.equals(authToken, authKey)) {
            radioService.receiveMessage(message);
        } else {
            LOG.debug("Mismatch of authentication token. Dropping radio message.");
        }
    }
}
