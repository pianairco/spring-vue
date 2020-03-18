package ir.piana.dev.springvue.core;

import ir.piana.dev.springvue.core.reflection.VUModuleInstaller;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.xml.parsers.ParserConfigurationException;

@Controller
public class VUAppController {
    @GetMapping(value = "/vu-app", produces = "text/javascript; charset=UTF-8")
    public ResponseEntity getVUApp() throws ParserConfigurationException {
        return ResponseEntity.status(200).body(VUModuleInstaller.getInstance().getAppBuffer());
    }
}
