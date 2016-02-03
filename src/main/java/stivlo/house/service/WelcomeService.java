package stivlo.house.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class WelcomeService {

    private static final Logger LOG = LoggerFactory.getLogger(WelcomeService.class);

    @PostConstruct
    public void welcome() {
        LOG.info("***** WELCOME! *****");
    }

}
