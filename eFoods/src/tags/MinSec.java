package tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class MinSec extends SimpleTagSupport
{
	private static int THOUSAND = 1000;
	private static int BASE = 60;
	private long milliseconds;

	public MinSec()
   {
	   super();
   }

	@Override
   public void doTag() throws JspException, IOException
   {
	   super.doTag();
	   
		long seconds = milliseconds / THOUSAND;
		long m = seconds / BASE;
		long s = seconds % BASE;
		
		JspWriter out = this.getJspContext().getOut();
		if (m != 0)
			out.write(m + "m");
		out.write(s + "s");
   }

	public void setMilliseconds(long milliseconds)
	{
		this.milliseconds = milliseconds;
	}
}
