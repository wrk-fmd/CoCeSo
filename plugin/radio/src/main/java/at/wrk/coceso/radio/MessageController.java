package at.wrk.coceso.radio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data/radio/messages")
public class MessageController {

    private final RadioService radioService;

    @Autowired
    public MessageController(RadioService radioService) {
        this.radioService = radioService;
    }

    @PostMapping
    public void receiveMessage(@RequestBody IncomingMessageDto message, @RequestHeader("Authorization") String key) {
        radioService.receiveMessage(message, key);
    }
}
