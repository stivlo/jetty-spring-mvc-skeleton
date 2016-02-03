package stivlo.house.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    private static final Logger LOG = LoggerFactory.getLogger(WelcomeController.class);

    @Value("${environment}")
    private String message;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showIndex() {
        LOG.debug("GET /");
        return message;
    }

}