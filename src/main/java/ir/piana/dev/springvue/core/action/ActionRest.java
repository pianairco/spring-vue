package ir.piana.dev.springvue.core.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.xml.parsers.ParserConfigurationException;
import java.util.List;
import java.util.function.Function;

@Controller
public class ActionRest {
    @Autowired
    private ApplicationContext сontext;

    @Autowired
    private SpringVueResource springVueResource;

    @GetMapping(value = "/vu-app", produces = "text/javascript; charset=UTF-8")
    public ResponseEntity getVueApp() throws ParserConfigurationException {
        boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().
                getInputArguments().toString().indexOf("jdwp") >= 0;
        if(isDebug)
            springVueResource.refresh();
        return ResponseEntity.status(200).body(springVueResource.getVueApp());
    }

    @PostMapping(path = "action")
    public ResponseEntity<String> testPost(RequestEntity requestEntity) {
        List<String> beans = requestEntity.getHeaders().get("action");
        List<String> actions = requestEntity.getHeaders().get("activity");
        if(beans != null && !beans.isEmpty()) {
            Action bean = (Action)сontext.getBean(beans.get(0));
            if(actions != null && !actions.isEmpty()) {
                try {
                    Function<RequestEntity, ResponseEntity> field = bean.getField(actions.get(0));
                    return field.apply(requestEntity);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return notFound.apply(requestEntity);
    }

    Function<RequestEntity, ResponseEntity> notFound = (r) -> {
        return ResponseEntity.status(404).body("Not Found");
    };
}
