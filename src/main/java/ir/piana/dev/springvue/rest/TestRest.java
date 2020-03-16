package ir.piana.dev.springvue.rest;

import ir.piana.dev.springvue.action.Action;
import ir.piana.dev.springvue.core.sql.SQLExecuter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.function.Function;

@Controller
public class TestRest {
    @Autowired
    private ApplicationContext сontext;

    @GetMapping(path = "test")
    public ResponseEntity<String> test(RequestEntity requestEntity) {
        return ResponseEntity.ok("hello");
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
        return x.apply(requestEntity);
    }

    @Autowired
    private SQLExecuter sqlExecuter;

    Function<RequestEntity, ResponseEntity> x = (r) -> {
        sqlExecuter.toString();
        return ResponseEntity.ok("Hello World");
    };
}
