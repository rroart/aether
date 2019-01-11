package roart.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static org.junit.Assert.*;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import roart.common.constants.FileSystemConstants.FileSystemType;
import roart.common.util.XmlFs;

public class XmlFsTest {
    @Test
    public void test() throws ParserConfigurationException, SAXException, IOException, URISyntaxException, XPathExpressionException {
        /*
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document doc = builder.parse(new File(getPath("test.xml").toUri()));
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expr = "/config/fs/dirlist";
        NodeList nodeList = (NodeList) xPath.compile(expr).evaluate(doc, XPathConstants.NODESET);
        assertEquals(1, nodeList.getLength());
        System.out.println(nodeList.item(0));
        Node node = nodeList.item(0);
        String content = node.getTextContent();
        System.out.println(content);
        */
        System.out.println(getPath("test.xml").toUri());
        String[] f = new XmlFs().getDirList(new File(getPath("test.xml").toUri()));
        System.out.println(f[0] + " " + f[1]);
        Map<FileSystemType, List<String>> m = new XmlFs().getDirListMap(new File(getPath("test.xml").toUri()));
        System.out.println(m);
    }
    
    public Path getPath(String file) throws URISyntaxException {
        return Paths.get(Thread.currentThread().getContextClassLoader().getResource(file).toURI());
    }
    
}
