package ir.piana.dev.springvue.core.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ir.piana.dev.springvue.core.group.GroupProvider;
import ir.piana.dev.springvue.core.reflection.ClassGenerator;
import org.apache.commons.io.IOUtils;
import org.reflections.Reflections;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public class ActionInstaller {
    private static DocumentBuilder dBuilder;
    private static ActionInstaller actionInstaller = null;
    private GroupProvider groupProvider;
    private List<String> components = new ArrayList<>();
    private String routePath;
    private Map<String, Map.Entry<String, Object>> routeMap = new LinkedHashMap<>();
    private String beanBaseName;
    private String beanPackage;
    private List<String> loadFrom;
    StringBuffer buffer = new StringBuffer();
    Map<String, Map.Entry<String, String>> beanMap = new LinkedHashMap<>();

    private static String appComponent;
    private static String notFoundComponent;
    private static String vLinkComponent;

    static {
        try {
            dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            InputStream inputStream = ActionInstaller.class.getResourceAsStream("/app.vue.jsp");
            String theString = IOUtils.toString(inputStream, "UTF-8");
            appComponent = theString.substring(theString.indexOf("<script>") + 8, theString.indexOf("</script>"));

            inputStream = ActionInstaller.class.getResourceAsStream("/not-found.vue.jsp");
            theString = IOUtils.toString(inputStream, "UTF-8");
            notFoundComponent = theString.substring(theString.indexOf("<script>") + 8, theString.indexOf("</script>"));

            inputStream = ActionInstaller.class.getResourceAsStream("/v-link.vue.jsp");
            theString = IOUtils.toString(inputStream, "UTF-8");
            vLinkComponent = theString.substring(theString.indexOf("<script>") + 8, theString.indexOf("</script>"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
    private static final SecureRandom random = new SecureRandom();
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private static long counter = 1;
    private static final String baseBeanName = "springVue";

//    private ActionInstaller(DocumentBuilder dBuilder, Map<String, Object> map) {
//        Map<String, String> springMap = (Map<String, String>) map.get("spring");
//        Map<String, String> vueMap = (Map<String, String>) map.get("vue");
//        beanBaseName = springMap.get("bean-base-name");
//        beanPackage = springMap.get("bean-package");
//        String loadFrom = vueMap.get("load-from");
//        if(loadFrom.equals("resource")) {
//
//        }
//        this.dBuilder = dBuilder;
//        this.prop = prop;
//        this.buffer.append(vLinkComponent).append("\n").append(notFoundComponent).append("\n");
//    }

    private static String generateRandomString() {
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 8; i++) {
            // 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);
            sb.append(rndChar);
        }
        return sb.toString();
    }

    private List<File> getResourceFiles(String folderName) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = this.getClass().getResource(folderName);
        if(url != null) {
            String path = url.getPath();
            File[] files = new File(path).listFiles();
            return Arrays.asList(files);
        } else {
            return new ArrayList<>();
        }
    }
//    public static synchronized ActionInstaller getInstance(InputStream input) {
//        if (actionInstaller != null)
//            return actionInstaller;
//        try {
//            Properties prop = new Properties();
//            // load a properties file
//            prop.load(input);
//            actionInstaller = new ActionInstaller(DocumentBuilderFactory.newInstance()
//                    .newDocumentBuilder(), prop);
//        } catch (Exception ex) {
//            throw new RuntimeException(ex);
//        }
//
//        return actionInstaller;
//    }

    public static synchronized SpringVueResource getSpringVueResource(GroupProvider groupProvider) {
        InputStream inputStream = ActionListener.class.getResourceAsStream("/piana/cfg/spring-vue.yaml");
        if (inputStream == null)
            throw new RuntimeException("config file required!");
        String error = null;
        try {
            Map<String, Object> map = mapper.readValue(inputStream, Map.class);
            map = (Map<String, Object>) map.get("app");
            ActionInstaller actionInstaller = new ActionInstaller();
            actionInstaller.setGroupProvider(groupProvider);
            actionInstaller.setSpringProperties((Map<String, Object>) map.get("spring"));
            actionInstaller.setVueProperties((Map<String, Object>) map.get("vue"));
            actionInstaller.loadComponents();
            actionInstaller.loadRoutes();
            return actionInstaller.install();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    static synchronized SpringVueResource refreshSpringVueResource(SpringVueResource springVueResource, GroupProvider groupProvider) {
        InputStream inputStream = ActionListener.class.getResourceAsStream("/piana/cfg/spring-vue.yaml");
        if (inputStream == null)
            throw new RuntimeException("config file required!");
        String error = null;
        try {
            Map<String, Object> map = mapper.readValue(inputStream, Map.class);
            map = (Map<String, Object>) map.get("app");
            ActionInstaller actionInstaller = new ActionInstaller();
            actionInstaller.setGroupProvider(groupProvider);
            actionInstaller.setSpringProperties((Map<String, Object>) map.get("spring"));
            actionInstaller.setVueProperties((Map<String, Object>) map.get("vue"));
            actionInstaller.loadComponents();
            actionInstaller.loadRoutes();
            return actionInstaller.refresh(springVueResource, groupProvider);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    static synchronized ActionInstaller getInstance() {
        if(actionInstaller == null)
            throw new RuntimeException("actionInstaller not initialized!");
        return actionInstaller;
    }

    private void setGroupProvider(GroupProvider groupProvider) {
        this.groupProvider = groupProvider;
    }

    private void setSpringProperties(Map<String, Object> springMap) {
        beanBaseName = (String) springMap.get("bean-base-name");
        beanPackage = (String) springMap.get("bean-package");
    }

    private void setVueProperties(Map<String, Object> vueMap) {
        loadFrom = Arrays.asList(((String) vueMap.get("load-from")).split(","))
                .stream().map(s -> s.trim()).collect(Collectors.toList());
//        routeMap.putAll(route((Map)vueMap.get("route"), ""));
    }

    private void loadComponents() {
        if (loadFrom.contains("resource")) {
            loadComponentsFromResource();
        }
    }

    private void loadComponentsFromResource() {
        List<File> componentFiles = getResourceFiles("/piana/component");
        List<File> pageFiles = getResourceFiles("/piana/page");
        for(File f : componentFiles)
            component("/piana/component/".concat(f.getName()));
        for(File f : pageFiles)
            component("/piana/page/".concat(f.getName()));
    }

    private void loadRoutes() {
        if (loadFrom.contains("resource")) {
            loadRoutesFromResource();
        }
    }

    private void loadRoutesFromResource() {
        route(this.getClass().getResourceAsStream("/piana/cfg/route.yaml"));
    }

    public ActionInstaller component(String resourcePath) {
        components.add(resourcePath);
        return this;
    }

    private ActionInstaller installComponent(InputStream inputStream) {
        String error = null;
//        String jsApp = null;
        try {
            String theString = IOUtils.toString(inputStream, "UTF-8");
            this.installComponent(theString);
        } catch (IOException e) {
            error = e.getMessage();
        }
        if(error != null)
            throw new RuntimeException(error);
//        buffer.append(jsApp).append("\n");
        return this;
    }

    private ActionInstaller installComponent(String theString) {
        String error = null;
        String jsApp = null;
        try {
            String appString = theString.substring(theString.indexOf("<app "), theString.indexOf("</app>") + 6);
            Document doc = dBuilder.parse(new InputSource(new StringReader(appString)));
            NodeList nList = doc.getElementsByTagName("app");
            Node item = nList.item(0);
            String appName = null;
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) item;
                appName = eElement.getAttribute("name");
            }

            String templateString = theString.substring(theString.indexOf("<html-template>") + 15, theString.indexOf("</html-template>"));
            String scriptString = theString.substring(theString.indexOf("<script>") + 8, theString.indexOf("</script>"));

            String beanName = beanBaseName.concat(generateRandomString()).concat(String.valueOf(counter++));
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

                String aPackage = beanPackage;
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
                beanMap.put(aPackage.concat(".").concat(aClassName), new AbstractMap.SimpleEntry<>(beanName, appName));
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

    private ActionInstaller refreshComponent(InputStream inputStream, SpringVueResource springVueResource) {
        String error = null;
//        String jsApp = null;
        try {
            String theString = IOUtils.toString(inputStream, "UTF-8");
            refreshComponent(theString, springVueResource);
        } catch (IOException e) {
            error = e.getMessage();
        }
        if(error != null)
            throw new RuntimeException(error);
//        buffer.append(jsApp).append("\n");
        return this;
    }

    private ActionInstaller refreshComponent(String theString, SpringVueResource springVueResource) {
        String error = null;
        String jsApp = null;
        try {
            String appString = theString.substring(theString.indexOf("<app "), theString.indexOf("</app>") + 6);
            Document doc = dBuilder.parse(new InputSource(new StringReader(appString)));
            NodeList nList = doc.getElementsByTagName("app");
            Node item = nList.item(0);
            String appName = null;
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) item;
                appName = eElement.getAttribute("name");
            }

            String templateString = theString.substring(theString.indexOf("<html-template>") + 15, theString.indexOf("</html-template>"));
            String scriptString = theString.substring(theString.indexOf("<script>") + 8, theString.indexOf("</script>"));

            final String appName2 = appName;
            List<Map.Entry<String, String>> collect = springVueResource.getBeanMap().keySet().stream()
                    .filter(key -> springVueResource.getBeanMap().get(key).getValue().equals(appName2))
                    .map(key -> springVueResource.getBeanMap().get(key))
                    .collect(Collectors.toList());
            String beanName = "";
            if(collect != null && !collect.isEmpty())
                beanName = collect.get(0).getKey();
            StringBuffer jsAppBuffer = new StringBuffer();
            jsApp = jsAppBuffer.append(scriptString).toString().replaceAll("\\$app\\$", appName)
                    .replaceAll("\\$bean\\$", beanName)
                    .replace("$template$",
                            Arrays.stream(templateString.split(System.getProperty("line.separator")))
                                    .map(line -> line.trim()).collect(Collectors.joining("")));
        } catch (IOException e) {
            error = e.getMessage();
        } catch (SAXException e) {
            error = e.getMessage();
        }
        if(error != null)
            throw new RuntimeException(error);
        buffer.append(jsApp).append("\n");
        return this;
    }

    public Map<String, Map.Entry<String, Map<String, String>>> route(Map<String, Object> map, String parentPath) {
        Map<String, Map.Entry<String, Map<String, String>>> routeMap = new LinkedHashMap<>();
        parentPath = parentPath.equals("//") ? "" : parentPath;
        for (String key : map.keySet()) {
            if (map.get(key) instanceof String) {
                String sign = "";
                String val = (String) map.get(key);
                if(val.startsWith("$")) {
                    if(val.substring(1, val.indexOf("(")).equalsIgnoreCase("redirect-to")) {
                        val = val.substring(val.indexOf("(") + 1, val.indexOf(")"));
                        sign = "@";
                    }
                }
                routeMap.put(sign.concat(parentPath).concat(key), new AbstractMap.SimpleEntry(val, null));
            } else {
                Map<String, Object> childMap = (Map<String, Object>) map.get(key);
                if(childMap.containsKey("component")) {
                    String component = (String) childMap.get("component");
                    routeMap.put(parentPath.concat(key), new AbstractMap.SimpleEntry(component, route((Map<String, Object>)childMap.get("children"), "")));
                } else
                    routeMap.putAll(route((Map<String, Object>)map.get(key), parentPath.concat(key).concat("/")));
            }
        }
        return routeMap;
    }

    private ActionInstaller route(InputStream inputStream) {
        String error = null;
        try {
            Map<String, Object> map = mapper.readValue(inputStream, Map.class);
            routeMap.putAll(route((Map)map.get("route"), ""));
        } catch (IOException e) {
            error = e.getMessage();
        }
        if(error != null)
            throw new RuntimeException(error);
        return this;
    }

    public ActionInstaller route(String resourcePath) {
        routePath = resourcePath;
        return this;
    }

    StringBuffer installRouter(Map<String, Map.Entry<String, Object>> map, String key) {
        StringBuffer routerBuffer = new StringBuffer();
        if(key.startsWith("@"))
            routerBuffer.append("{path:'" + key.substring(1) + "', redirect:'").append(map.get(key).getKey()).append("'},");
        else {
            if(map.get(key).getValue() == null)
                routerBuffer.append("{path:'" + key + "', component:").append(map.get(key).getKey()).append("},");
            else {
                routerBuffer.append("{path:'" + key + "', component:").append(map.get(key).getKey()).append(",")
                        .append("children:[");
                for (String k : ((Map<String, Map.Entry<String, Object>>)map.get(key).getValue()).keySet()) {
                    routerBuffer.append(installRouter((Map<String, Map.Entry<String, Object>>)map.get(key).getValue(), k));
                }
                routerBuffer.deleteCharAt(routerBuffer.length() - 1);
                routerBuffer.append("]},");
            }
        }
        return routerBuffer;
    }

    public SpringVueResource install() throws RuntimeException {
        for(String resourcePath : components) {
            installComponent(this.getClass().getResourceAsStream(resourcePath));
        }
        if(loadFrom.contains("interface")) {
            Reflections reflections = new Reflections("ir.piana");
            Set<Class<? extends VueComponentLoadable>> classes = reflections.getSubTypesOf(VueComponentLoadable.class);
            for (Class loadable : classes) {
                try {
                    installComponent(((VueComponentLoadable)loadable.newInstance()).getComponentString());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        StringBuffer routerBuffer = new StringBuffer();
//        route(this.getClass().getResourceAsStream(routePath));
        routerBuffer.append("const routes = [");
        for (String key : routeMap.keySet()) {
            routerBuffer.append(installRouter(routeMap, key));
        }
        if(routeMap.size() > 0)
            routerBuffer.deleteCharAt(routerBuffer.length() - 1);
        routerBuffer.append("];");
        buffer.append(routerBuffer).append("\n");
        buffer.append("const router = new VueRouter({routes});");
        buffer.append(notFoundComponent).append("\n");
        buffer.append("const groups = ").append(groupProvider.getGroupsJsonString()).append(";");
        buffer.append(notFoundComponent).append("\n");
//        buffer.append(vLinkComponent).append("\n");
        buffer.append(appComponent).append("\n");
        return new DefaultSpringVueResource(buffer.toString(), beanMap, groupProvider);
    }

    public SpringVueResource refresh(SpringVueResource springVueResource, GroupProvider groupProvider) {
        for(String resourcePath : components) {
//            try {
//                URL resURL = this.getClass().getResource(resourcePath);
//                URLConnection resConn = resURL.openConnection();
//                resConn.setUseCaches(false);
//                refreshComponent(resConn.getInputStream(), springVueResource);
            refreshComponent(this.getClass().getResourceAsStream(resourcePath), springVueResource);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        if(loadFrom.contains("interface")) {
            Reflections reflections = new Reflections("ir.piana");
            Set<Class<? extends VueComponentLoadable>> classes = reflections.getSubTypesOf(VueComponentLoadable.class);
            for (Class loadable : classes) {
                try {
                    refreshComponent(((VueComponentLoadable)loadable.newInstance()).getComponentString(), springVueResource);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        StringBuffer routerBuffer = new StringBuffer();
//        route(this.getClass().getResourceAsStream(routePath));
        routerBuffer.append("const routes = [");
        for (String key : routeMap.keySet()) {
            routerBuffer.append(installRouter(routeMap, key));
//            if(key.startsWith("@"))
//                routerBuffer.append("{path:'" + key.substring(1) + "', redirect:'").append(routeMap.get(key)).append("'},");
//            else
//                routerBuffer.append("{path:'" + key + "', component:").append(routeMap.get(key)).append("},");
        }
        if(routeMap.size() > 0)
            routerBuffer.deleteCharAt(routerBuffer.length() - 1);
        routerBuffer.append("];");
        buffer.append(routerBuffer).append("\n");
        buffer.append("const router = new VueRouter({routes});");

        buffer.append("Vue.mixin({data: function() { return {get groups() {return ")
                .append(groupProvider.getGroupsJsonString()).append("; }, get activeParent() { return {\"code\": \"\" }; }}}});").append("\n");

//        buffer.append("const groups = ").append(groupProvider.getGroupsJsonString()).append(";");
        buffer.append(notFoundComponent).append("\n");
//        buffer.append(vLinkComponent).append("\n");
        buffer.append(appComponent).append("\n");
        return new DefaultSpringVueResource(buffer.toString(), beanMap, groupProvider);
    }

    private static class DefaultSpringVueResource implements SpringVueResource {
        private String vueApp;
        private Map<String, Map.Entry<String, String>> beanMap;
        private GroupProvider groupProvider;

        public DefaultSpringVueResource(String vueApp, Map<String, Map.Entry<String, String>> beanMap, GroupProvider groupProvider) {
            this.vueApp = vueApp;
            this.beanMap = beanMap;
            this.groupProvider = groupProvider;
        }

        @Override
        public String getVueApp() {
            return vueApp;
        }

        @Override
        public Map<String, Map.Entry<String, String>> getBeanMap() {
            return beanMap;
        }

        @Override
        public void refresh() {
            SpringVueResource springVueResource = ActionInstaller.refreshSpringVueResource(this, groupProvider);
            this.vueApp = springVueResource.getVueApp();
        }
    }
}
