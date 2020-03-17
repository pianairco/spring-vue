package ir.piana.dev.springvue.core.sql;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MySQLExecuter implements SQLExecuter {
    public <T> List<T> executeQuery(String query) {
        return new ArrayList<>();
    }
}
