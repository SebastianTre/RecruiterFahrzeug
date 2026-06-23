package SeTre.RecruiterFahrzeug.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if(status != null) {
            int errorCode = Integer.parseInt(status.toString());
            model.addAttribute("errorCode", errorCode);
        }
        model.addAttribute("errorMessage", "Ein unerwarteter Fehler ist aufgetreten");
        model.addAttribute("title", "Ein Fehler ist aufgetreten");
        return "error";
    }

}
