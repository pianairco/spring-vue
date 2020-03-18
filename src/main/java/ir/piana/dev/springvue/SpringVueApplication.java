package ir.piana.dev.springvue;

import ir.piana.dev.springvue.core.reflection.VUModuleInstaller;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.xml.parsers.ParserConfigurationException;

@SpringBootApplication
public class SpringVueApplication {

	public static void main(String[] args) throws ParserConfigurationException {

//		InputStream resourceAsStream = SpringVueApplication.class.getResourceAsStream("/action-one.java");
//		try {
//			String theString = IOUtils.toString(resourceAsStream, "UTF-8");
//			Class aClass = ClassGenerator.registerClass("ir/piana/dev/springvue/action/ActionOne", theString);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (NoSuchFieldException e) {
//			e.printStackTrace();
//		}

		VUModuleInstaller.getInstance()
				.compile(SpringVueApplication.class.getResourceAsStream("/piana/forms/one.vue.jsp"))
				.compile(SpringVueApplication.class.getResourceAsStream("/piana/forms/two.vue.jsp"));

		SpringApplication.run(SpringVueApplication.class, args);
	}

}
