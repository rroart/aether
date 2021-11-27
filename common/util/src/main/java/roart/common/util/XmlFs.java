package roart.common.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import roart.common.constants.FileSystemConstants.FileSystemType;
import roart.common.model.FileObject;
import roart.common.model.Location;

public class XmlFs {
    public String[] getDirList(String file) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        return getDirList(new File(file));
    }
    
    public String[] getDirList(File file) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        System.out.println("file " + file);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document doc = builder.parse(file);
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expr = "/config/fs/dirlist";
        NodeList nodeList = (NodeList) xPath.compile(expr).evaluate(doc, XPathConstants.NODESET);
        Node node = nodeList.item(0);
        String content = node.getTextContent();
        return content.split(",");
    }
    
    public Map<Location, List<FileObject>> getDirListMap(File file) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        String[] dirList = getDirList(file);
        return getDirListMap(dirList);
    }

    public Map<Location, List<FileObject>> getDirListMap(String dirList) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        return getDirListMap(dirList.split(","));
    }
    
    public Map<Location, List<FileObject>> getDirListMap(String[] dirList) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        Map<Location, List<FileObject>> retMap = new HashMap<>();
        for (String dir : dirList) {
            FileObject fok = FsUtil.getFileObject(dir);
            FileObject fo = FsUtil.getFileObject(dir);
            fok.location.extra = null;
            if (true || fo != null) {
                List<FileObject> list = retMap.computeIfAbsent(fok.location, f -> new ArrayList<>());
                list.add(fo);
            } else {
                System.out.println("MyType null");
            }
        }
        return retMap;
    }

    public void decide(String file) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        String[] dirList = getDirList(file);
        for (String dir : dirList) {
            
        }
    }
}
