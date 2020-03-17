package ir.piana.dev.springvue.core.sql;

import java.util.List;

public interface SQLExecuter {
    <T> List<T> executeQuery(String query);
}
