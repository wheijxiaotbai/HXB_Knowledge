package user.controller;

import org.springframework.web.servlet.ModelAndView;
import user.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    public @ResponseBody User hello() {

        return new User.Builder()
                .name("11111")
                .sex("222222222")
                .build();

    }

    @RequestMapping(value = "/hello1")
    public ModelAndView hello1(){

        ModelAndView mav = new ModelAndView();
        mav.setViewName("WEB-INF/jsp/hello.jsp");
        return mav;

    }

}
