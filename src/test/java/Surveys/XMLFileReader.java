package Surveys;
import java.io.File;
import java.util.Iterator;
import java.util.ListIterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
public class XMLFileReader {
	public static String getXMLNodeValueByTag(String xmlfpath,String nodename) {
		String tagvalue=null;
		try{
		
		File file = new File(xmlfpath);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbf.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		System.out.println(doc.getDocumentElement().getNodeName());
		NodeList nodeList = doc.getElementsByTagName("questions");
		int tLength = nodeList.getLength();
		for(int i=0; i<tLength; i++){
		Node node = nodeList.item(i);
		if(node.getNodeType()==Node.ELEMENT_NODE){
		Element element = (Element)node;
		tagvalue=element.getElementsByTagName(nodename).item(0).getTextContent();
		System.out.println("Question id: "+element.getAttribute("id"));
		System.out.println("tagvalue: "+element.getElementsByTagName("text").item(0).getTextContent());
		/*System.out.println("Last Name: "+element.getElementsByTagName("lastname").item(0).getTextContent());
		System.out.println("Balance: "+element.getElementsByTagName("balance").item(0).getTextContent());*/
		
		}
		
		}
		}catch (Exception e){
		e.printStackTrace();
		}
		return tagvalue;
		
		}
		}
