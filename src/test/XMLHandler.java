package test;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLHandler implements Runnable {

	protected String feedUrl;
	protected boolean stopped = true;
	protected Thread thread;
	
	protected String parentElement;
	protected List<String> childElements;
	
	public XMLHandler() {

	}
	
	
	@Override
	public void run() {
		getData();
	}
	
	public void start(String xurl) {
	
		stopped = true;
		feedUrl = xurl;
		if ( stopped ) {
			thread = new Thread(this);
			//thread.setName("XMLHandler");
			thread.start();
			stopped = false;
		}
	}
	
	public void stopMe() {
		stopped = true;
	}	

	public void getData() {
		try {

			URL url = new URL(feedUrl);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection)connection;
			int responseCode = httpConnection.getResponseCode();
			
			
			if (responseCode == HttpURLConnection.HTTP_OK) {
				readData(connection.getInputStream());
			} else {
				//sendErrorMessage();
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public void readData(InputStream in) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(in);
		doc.getDocumentElement().normalize();

		NodeList nodes = doc.getElementsByTagName(parentElement);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				for ( String childElement : childElements ) {
					System.out.println(childElement + " = " + getValue(childElement, element));
				}
			}
		}
	}
	
	public static String getValue(String tag, Element element) {
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		if ( node != null ) {
			return node.getNodeValue() != null ? node.getNodeValue() : "";
		} else
			return "";
	}


	public String getParentElement() {
		return parentElement;
	}


	public void setParentElement(String parentElement) {
		this.parentElement = parentElement;
	}


	public List<String> getChildElements() {
		if ( childElements == null ) childElements = new ArrayList<String>();
		return childElements;
	}


	public void setChildElements(List<String> childElements) {
		this.childElements = childElements;
	}	
	
	


}
