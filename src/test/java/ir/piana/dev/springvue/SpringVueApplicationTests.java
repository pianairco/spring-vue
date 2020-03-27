package ir.piana.dev.springvue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootTest()
//@SpringBootConfiguration
@SpringBootApplication(scanBasePackages = "ir.piana.dev.springvue.core")
public class SpringVueApplicationTests {

	public static void main(String[] args) {
		SpringApplication.run(SpringVueApplicationTests.class, args);
	}

}
