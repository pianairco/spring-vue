package ir.piana.dev.springvue.action;

import ir.piana.dev.springvue.core.sql.SQLExecuter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component("one")
public class ActionOne extends Action {
    @Autowired
    private SQLExecuter sqlExecuter;

    public Function<RequestEntity, ResponseEntity> x = (r) -> {
        sqlExecuter.toString();
        Map body = (Map) r.getBody();
        String firstName = (String)body.get("firstName");
        String lastName = (String)body.get("lastName");
        return ResponseEntity.ok("Hello ".concat(firstName).concat(" ").concat(lastName));
    };
}
