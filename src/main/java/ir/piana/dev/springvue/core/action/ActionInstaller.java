package ir.piana.dev.springvue.core.action;

import ir.piana.dev.springvue.core.reflection.ClassGenerator;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class ActionInstaller {
    private DocumentBuilder dBuilder;
    private static ActionInstaller actionInstaller = null;
    private static String appComponent = "var app = new Vue({\n" +
            "        el: '#app',\n" +
            "        data: {\n" +
            "            currentRoute: window.location.hash\n" +
            "        },\n" +
            "        created: function () {console.log(\"create\");console.log(window.location);console.log(window.location.hash);console.log(window.location.pathname);console.log(this.currentRoute);},\n" +
            "        computed: {ViewComponent() {if(this.currentRoute === '')return routes['/'] || NotFound;else if(this.currentRoute.startsWith('#'))return routes[this.currentRoute.substring(1)] || NotFound;else return routes[this.currentRoute] || NotFound;}},\n" +
            "        render(h) {return h(this.ViewComponent)}\n" +
            "    });";
    private static String notFoundComponent = "const NotFound = { template: '<p>Page not found</p>' }";
    private static String vLinkComponent = "Vue.component('v-link', {\n" +
            "        template: '<a v-bind:href=\"href\" v-bind:class=\"{ active: isActive }\" v-on:click=\"go\"><slot></slot></a>',\n" +
            "        props: {href: {type:String,required: true}},\n" +
            "        computed: {isActive () {return this.href === this.$root.currentRoute}},\n" +
            "        methods: {go (event) {\n" +
            "            event.preventDefault();this.$root.currentRoute = this.href;console.log(this.$root.currentRoute);\n" +
            "            if(this.$root.currentRoute.startsWith('/#'))\n" +
            "                this.$root.currentRoute = this.$root.currentRoute.substring(2);\n" +
            "            window.history.pushState(null,routes[this.$root.currentRoute],this.href)\n" +
            "            }\n" +
            "        }\n" +
            "    });";
    private Properties prop = null;
    private StringBuffer buffer = new StringBuffer();
    private Map<String, String> beanMap = new LinkedHashMap<>();
    private Map<String, String> routeMap = new LinkedHashMap<>();

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
    private static final SecureRandom random = new SecureRandom();
    private static long counter = 1;
    private static final String baseBeanName = "springVue";

    public ActionInstaller(DocumentBuilder dBuilder, Properties prop) {
        this.dBuilder = dBuilder;
        this.prop = prop;
        this.buffer.append(vLinkComponent).append("\n").append(notFoundComponent).append("\n");
    }

    public static String generateRandomString() {
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 8; i++) {
            // 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);
            sb.append(rndChar);
        }
        return sb.toString();
    }

    public static synchronized ActionInstaller getInstance() {
        if (actionInstaller != null)
            return actionInstaller;
        try (InputStream input = ActionInstaller.class.getResourceAsStream("/spring-vue.properties")) {
            Properties prop = new Properties();
            // load a properties file
            prop.load(input);
            actionInstaller = new ActionInstaller(DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder(), prop);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return actionInstaller;
    }

    public ActionInstaller component(InputStream inputStream) {
        String error = null;
        String jsApp = null;
        try {
            String theString = IOUtils.toString(inputStream, "UTF-8");
            String appString = theString.substring(theString.indexOf("<app "), theString.indexOf("</app>") + 6);
            Document doc = dBuilder.parse(new InputSource(new StringReader(appString)));
            NodeList nList = doc.getElementsByTagName("app");
            Node item = nList.item(0);
            String appName = null;
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) item;
                appName = eElement.getAttribute("name");
            }

            String templateString = theString.substring(theString.indexOf("<template>") + 10, theString.indexOf("</template>"));
            String scriptString = theString.substring(theString.indexOf("<script>") + 8, theString.indexOf("</script>"));

            String beanName = prop.getProperty("base-bean-name").concat(generateRandomString()).concat(String.valueOf(counter++));
            StringBuffer jsAppBuffer = new StringBuffer();
            jsApp = jsAppBuffer.append(scriptString).toString().replaceAll("\\$app\\$", appName)
                    .replaceAll("\\$bean\\$", beanName)
                    .replace("$template$",
                            Arrays.stream(templateString.split(System.getProperty("line.separator")))
                                    .map(line -> line.trim()).collect(Collectors.joining("")));

            int startOfBeanIfExist = theString.indexOf("</script>") + 10;
            if (theString.length() > startOfBeanIfExist) {
                String javaString = theString.substring(startOfBeanIfExist);
                if(javaString == null || javaString.isEmpty() || !javaString.contains("<bean>")) {
                    buffer.append(jsApp).append("\n");
                    return this;
                }
                javaString = javaString.replaceAll("<%@ page import=\"", "import ");
                javaString = javaString.replaceAll("\" %>", ";");
                javaString = javaString.replaceAll("<%", "");
                javaString = javaString.replaceAll("%>", "");

                String beanString = javaString.substring(0, javaString.indexOf("<import>")).concat("</bean>");
                String importString = javaString.substring(javaString.indexOf("<import>") + 8, javaString.indexOf("</import>"));
                String classString = javaString.substring(javaString.indexOf("<action>") + 8, javaString.indexOf("</action>"));

                StringBuffer classSourceBuffer = new StringBuffer();

                String aPackage = prop.getProperty("package");
                String aClassName = beanName.substring(0, 1).toUpperCase() + beanName.substring(1);

                classSourceBuffer.append("package ".concat(aPackage).concat(";\n"));
                classSourceBuffer.append(Arrays.stream(importString.split(System.getProperty("line.separator")))
                        .map(line -> line.trim()).collect(Collectors.joining("\n")));
                classSourceBuffer.append("import org.springframework.stereotype.Component;\n");
                classSourceBuffer.append("\n");

                classSourceBuffer.append("@Component(\"").append(beanName).append("\")\n");
                classSourceBuffer.append(Arrays.stream(classString.split(System.getProperty("line.separator"))).map(line -> line.trim())
                        .filter(line -> !line.isEmpty()).collect(Collectors.joining("\n")));
                String classSource = classSourceBuffer.toString().replace("class $VUE$", "public class ".concat(aClassName));
                Class aClass = ClassGenerator.registerClass(aPackage.concat(".").concat(aClassName).replaceAll("\\.", "/"), classSource);
                beanMap.put(aPackage.concat(".").concat(aClassName), beanName);
            }
        } catch (IOException e) {
            error = e.getMessage();
        } catch (IllegalAccessException e) {
            error = e.getMessage();
        } catch (SAXException e) {
            error = e.getMessage();
        } catch (NoSuchFieldException e) {
            error = e.getMessage();
        }
        if(error != null)
            throw new RuntimeException(error);
        buffer.append(jsApp).append("\n");
        return this;
    }

    public ActionInstaller page(InputStream inputStream) {
        String error = null;
        try {
            String theString = IOUtils.toString(inputStream, "UTF-8");
            String appString = theString.substring(theString.indexOf("<page "), theString.indexOf("</page>") + 8);
            Document doc = dBuilder.parse(new InputSource(new StringReader(appString)));
            NodeList nList = doc.getElementsByTagName("page");
            Node item = nList.item(0);
            String pageName = null;
            String pagePath = null;
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) item;
                pageName = eElement.getAttribute("name");
                pagePath = eElement.getAttribute("path");
            }

            String templateString = theString.substring(theString.indexOf("<template>") + 10, theString.indexOf("</template>"));
            if(theString.contains("<script>")) {
                String scriptString = theString.substring(theString.indexOf("<script>") + 8, theString.indexOf("</script>"));
                StringBuffer jsAppBuffer = new StringBuffer();
                String jsApp = jsAppBuffer.append(scriptString).toString().replaceAll("\\$app\\$", pageName)
                        .replace("$template$",
                                Arrays.stream(templateString.split(System.getProperty("line.separator")))
                                        .map(line -> line.trim()).collect(Collectors.joining("")));
                buffer.append(jsApp).append("\n");
            } else {
                String pageComponent = "var $app$ = Vue.component('$app$', { template: '$template$' });";
                pageComponent = pageComponent.replaceAll("\\$app\\$", pageName);
                pageComponent = pageComponent.replaceAll("\\$template\\$",
                        Arrays.stream(templateString.split(System.getProperty("line.separator")))
                                .map(line -> line.trim()).collect(Collectors.joining("")));
                buffer.append(pageComponent).append("\n");
            }
            routeMap.put(pagePath, pageName);
        } catch (IOException e) {
            error = e.getMessage();
        } catch (SAXException e) {
            error = e.getMessage();
        }
        if(error != null)
            throw new RuntimeException(error);
        return this;
    }

    public void install() {
        StringBuffer routerBuffer = new StringBuffer();
        routerBuffer.append("const routes = {");
        for (String key : routeMap.keySet()) {
            routerBuffer.append("'/" + key + "':").append(routeMap.get(key)).append(",");
        }
        if(routeMap.size() > 0)
            routerBuffer.deleteCharAt(routerBuffer.length() - 1);
        routerBuffer.append("};");
        buffer.append(routerBuffer).append("\n");
        buffer.append(appComponent).append("\n");
    }

    public String getAppBuffer() {
        return buffer.toString();
    }

    public Map<String, String> getBeanMap() {
        return beanMap;
    }
}
