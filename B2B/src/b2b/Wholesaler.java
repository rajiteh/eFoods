package b2b;

import java.net.URL;

import javax.xml.soap.*;

import org.w3c.dom.NodeList;

public class Wholesaler
{
	private String name;
	private URL url;
	
	public Wholesaler(String name, URL url)
   {
		this.name = name;
		this.url = url;
   }

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public URL getUrl()
	{
		return url;
	}

	public void setUrl(URL url)
	{
		this.url = url;
	}

	public double quote(String itemNum) throws Exception
	{
		String[] params = {"itemNumber"};
		String[] values = {itemNum};
		
		return Double.parseDouble(this.operation("quote", params, values).item(0).getTextContent());
	}
	
	public String order(String itemNum, int quantity, String key) throws Exception
	{
		String[] params = {"itemNumber", "quantity", "key"};
		String[] values = {itemNum, String.valueOf(quantity), key};
		
		return this.operation("order", params, values).item(0).getTextContent();
	}
	
	private NodeList operation(String operation, String[] reqParams, String[] values) throws Exception
	{
		// create SOAP message
		SOAPMessage msg;
		try
		{
			msg = MessageFactory.newInstance().createMessage();
		
			MimeHeaders header = msg.getMimeHeaders();
			header.addHeader("SOAPAction", "");
		
			SOAPBody body = msg.getSOAPPart().getEnvelope().getBody();
			SOAPElement elm = body.addChildElement(operation);
			for (int i=0; i < reqParams.length; i++)
			{
				elm.addChildElement(reqParams[i]).addTextNode(values[i]);
			}
		}
		catch (Exception e)
		{
			throw new Exception("Error occurs when creating SOAP message.");
		}
		
		// connect to the wholesaler web service
		SOAPMessage resp;
		try
		{
			SOAPConnection sc = SOAPConnectionFactory.newInstance().createConnection();
			resp = sc.call(msg, this.url);
			sc.close();
		}
		catch (Exception e)
		{
			throw new Exception("Error occurs when setting up SOAP connection.");
		}
		
		// parse the response
		NodeList result;
		try
		{
			result = resp.getSOAPPart().getEnvelope().getBody().getElementsByTagName(operation + "Return");
			// resp.writeTo(System.out);
		}
		catch (Exception e)
		{
			throw new Exception("Error occurs when parsing SOAP return.");
		}
		
		return result;
	}
}
