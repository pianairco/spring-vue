package ir.piana.dev.springvue.core.reflection;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.stream.Collectors;

public class VUModuleInstaller {
    private DocumentBuilder dBuilder;
    private static VUModuleInstaller vuModuleInstaller = null;
    private StringBuffer buffer = new StringBuffer();

    public VUModuleInstaller(DocumentBuilder dBuilder) {
        this.dBuilder = dBuilder;
    }

    public static synchronized VUModuleInstaller getInstance()
            throws ParserConfigurationException {
        if (vuModuleInstaller != null)
            return vuModuleInstaller;
        vuModuleInstaller = new VUModuleInstaller(DocumentBuilderFactory.newInstance()
                .newDocumentBuilder());
        return vuModuleInstaller;
    }

    public String compile(InputStream inputStream) {
        String error = null;
        String jsApp = null;
        try {
            String theString = IOUtils.toString(inputStream, "UTF-8");
            String templateString = theString.substring(theString.indexOf("<template>") + 10, theString.indexOf("</template>"));
            String scriptString = theString.substring(theString.indexOf("<script>") + 8, theString.indexOf("</script>"));

            StringBuffer jsAppBuffer = new StringBuffer();
            jsApp = jsAppBuffer.append(scriptString).toString().replace("$template$",
                    Arrays.stream(templateString.split(System.getProperty("line.separator")))
                            .map(line -> line.trim()).collect(Collectors.joining("")));

            String javaString = theString.substring(theString.indexOf("<bean"));
            javaString = javaString.replaceAll("<%@ page import=\"", "import ");
            javaString = javaString.replaceAll("\" %>", ";");
            javaString = javaString.replaceAll("<%", "");
            javaString = javaString.replaceAll("%>", "");
            String beanString = javaString.substring(0, javaString.indexOf("<import>")).concat("</bean>");
            String importString = javaString.substring(javaString.indexOf("<import>") + 8, javaString.indexOf("</import>"));
            String classString = javaString.substring(javaString.indexOf("<action>") + 8, javaString.indexOf("</action>"));

            StringBuffer classSourceBuffer = new StringBuffer();

            Document doc = dBuilder.parse(new InputSource(new StringReader(beanString)));
            NodeList nList = doc.getElementsByTagName("bean");
            Node item = nList.item(0);
            String name = null;
            String aPackage = null;
            String aClassName = null;
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) item;
                name = eElement.getAttribute("name");
                aPackage = eElement.getAttribute("package");
                aClassName = eElement.getAttribute("class");
            }
            jsApp = jsApp.replace("$app$", name);

            classSourceBuffer.append("package ".concat(aPackage).concat(";\n"));
            classSourceBuffer.append(Arrays.stream(importString.split(System.getProperty("line.separator")))
                    .map(line -> line.trim()).collect(Collectors.joining("\n")));
            classSourceBuffer.append("import org.springframework.stereotype.Component;\n");
            classSourceBuffer.append("\n");
            classSourceBuffer.append("@Component(\"").append(name).append("\")\n");
            classSourceBuffer.append(Arrays.stream(classString.split(System.getProperty("line.separator"))).map(line -> line.trim())
                    .filter(line -> !line.isEmpty()).collect(Collectors.joining("\n")));
            String classSource = classSourceBuffer.toString().replace("class $VUE", "public class ".concat(aClassName));
            Class aClass = ClassGenerator.registerClass(aPackage.concat(".").concat(aClassName).replaceAll("\\.", "/"), classSource);
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
        return jsApp;
    }

    public String getAppBuffer() {
        return buffer.toString();
    }
}
