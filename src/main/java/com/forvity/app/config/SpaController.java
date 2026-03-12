package com.forvity.app.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

    @RequestMapping(value = { "/", "/{path:^(?!api|actuator|h2-console|assets)[^\\.]*}", "/{path:^(?!api|actuator|h2-console|assets)[^\\.]*}/**" })
    public String forward() {
        return "forward:/index.html";
    }
}
