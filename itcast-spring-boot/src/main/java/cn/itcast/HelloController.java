package cn.itcast;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/itcast")
public class HelloController {

    @Autowired
    private Environment environment;

    @GetMapping("info")
    public String info(){
        return "hello word!!!"+environment.getProperty("url");
    }
}
