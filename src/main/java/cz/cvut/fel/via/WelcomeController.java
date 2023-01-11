package cz.cvut.fel.via;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WelcomeController {
    @RequestMapping("/app")
    public String loginMessage(){
        return "index.html";
    }

}
