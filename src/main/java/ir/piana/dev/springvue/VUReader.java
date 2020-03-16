package ir.piana.dev.springvue;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Service
public class VUReader {
    @PostConstruct
    void init() {
        InputStream oneIs = VUReader.class.getResourceAsStream("/piana/forms/one.jsp");
        InputStream twoIs = VUReader.class.getResourceAsStream("/piana/forms/two.jsp");
    }
}
