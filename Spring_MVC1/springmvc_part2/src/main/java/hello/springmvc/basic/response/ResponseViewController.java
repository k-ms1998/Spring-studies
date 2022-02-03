package hello.springmvc.basic.response;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ResponseViewController {

    @RequestMapping("/response-view-v1")
    public ModelAndView responseViewV1() {
        ModelAndView mav = new ModelAndView("response/hello")
                .addObject("data", "Response View V1");
//      viewName == view directory

        return mav;
    }

    @RequestMapping("/response-view-v2")
    public String responseViewV2(Model model) {
        model.addAttribute("data", "Response View V2");

        return "response/hello";
//  method returns String && doesn't have @ResponseBody annotation => renders View
//  If @ResponseBody, then "response/body" will be returned as a HTTP Response
    }

    @RequestMapping("/response/hello")
    public void responseViewV3(Model model) {
        model.addAttribute("data", "Response View V3");
//  If method is void, Renders the view in the @RequestMapping path => /response/hello
//  **Not recommended**
    }
}
