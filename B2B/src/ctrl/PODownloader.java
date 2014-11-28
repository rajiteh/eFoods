package ctrl;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import beans.POFileBean;
import beans.POFilesWrapper;

public class PODownloader
{
	String pofwurl;
	String bseurl;

	public PODownloader(String poFilesWrapperUrl, String baseUrl)
   {
		this.pofwurl = poFilesWrapperUrl;
		this.bseurl = baseUrl;
   }
	
	public String[] downloadPOs(String dir) throws Exception
	{
		// download XML file that specifies urls of PO files
		String pofsname = dir + "POFiles.xml";
		this.download(pofwurl, pofsname);
		
		// get list of PO file urls
		List<String> urls = getPOUrls(pofsname);
		
		// download the PO files
		String[] fnames = new String[urls.size()];
		int i = 0;
		Iterator<String> itr = urls.iterator();
		while (itr.hasNext())
		{
			String cur = itr.next();
			fnames[i] = dir + this.getFileNameOnly(cur);
			this.download(cur, fnames[i]);
			i++;
		}
		
		return fnames;
	}
	
	private void download(String srcurl, String fname) throws Exception
	{
		try
		{
			URL url = new URL(srcurl);
			ReadableByteChannel src = Channels.newChannel(url.openStream());
			FileOutputStream dst = new FileOutputStream(fname);
			dst.getChannel().transferFrom(src, 0, Long.MAX_VALUE);
			dst.close();
		}
		catch (MalformedURLException m)
		{
			throw new Exception("Invalid URL!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			//throw new Exception("Error: fail to download file.");
		}
	}
	
	private List<String> getPOUrls(String fname) throws Exception
	{
		if (fname == null)
			throw new NullPointerException("invalid file names.");
		
		// define unmarshaller
		JAXBContext jc;
		Unmarshaller um;
		try
		{
			jc = JAXBContext.newInstance(POFilesWrapper.class);
			um = jc.createUnmarshaller();
		}
		catch (Exception e)
		{
			throw new Exception("Error occurs when defining unmarshaller.");
		}
		
		// ummarshal order files
		POFilesWrapper pofw;
		try
		{
			pofw = (POFilesWrapper) um.unmarshal(new File(fname));
		}
		catch (Exception e)
		{
			throw new Exception("Error occurs during unmarshalling.");
		}
		
		if (pofw == null)
			throw new Exception("POFilesWrapper does not generated!");
		
		
		List<String> urls = new ArrayList<String>();
		List<POFileBean> pofs = pofw.getPOFileBeans();
		Iterator<POFileBean> itr = pofs.iterator();
		while (itr.hasNext())
		{
			urls.add(this.bseurl + itr.next().getUrl());
		}
		
		return urls;
	}
	
	private String getFileNameOnly(String url)
	{
		return url.substring(url.lastIndexOf('/') + 1);
	}
}
