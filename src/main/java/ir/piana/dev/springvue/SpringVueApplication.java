package ir.piana.dev.springvue;

import ir.piana.dev.springvue.core.action.ActionInstaller;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import javax.xml.parsers.ParserConfigurationException;

@SpringBootApplication
public class SpringVueApplication {

	public static void main(String[] args) throws ParserConfigurationException {
		ActionInstaller.getInstance()
				.component(SpringVueApplication.class.getResourceAsStream("/piana/component/one.vue.jsp"))
				.component(SpringVueApplication.class.getResourceAsStream("/piana/component/two.vue.jsp"))
				.component(SpringVueApplication.class.getResourceAsStream("/piana/component/three.vue.jsp"))
				.page(SpringVueApplication.class.getResourceAsStream("/piana/page/root.vue.html"))
				.page(SpringVueApplication.class.getResourceAsStream("/piana/page/login.vue.html"))
				.install();

		SpringApplication.run(SpringVueApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		System.out.println("hello world, I have just started up");
	}
}
