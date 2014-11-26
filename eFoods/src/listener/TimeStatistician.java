package listener;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

/**
 * Application Lifecycle Listener implementation class TimeStatistic
 *
 */
@WebListener
public class TimeStatistician implements HttpSessionAttributeListener {

    /**
     * Default constructor. 
     */
    public TimeStatistician() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see HttpSessionAttributeListener#attributeRemoved(HttpSessionBindingEvent)
     */
    public void attributeRemoved(HttpSessionBindingEvent se)  { 
         // DO NOTHING!
    }

	/**
     * @see HttpSessionAttributeListener#attributeAdded(HttpSessionBindingEvent)
     */
    public void attributeAdded(HttpSessionBindingEvent se)  { 
   	 if (se.getName().equals("cartItemsCount"))
   	 {
   		 long frsVstTime = se.getSession().getCreationTime();		// fresh visit time
   		 long curTime = System.currentTimeMillis();	// current time (when first item added)
   		 
   		 updateAvgAddItemTime(se, curTime - frsVstTime);
   		 
   		 se.getSession().setAttribute("cartLastManipTime", curTime);
   	 }
   	 else if (se.getName().equals("checkedOut"))	// when client has checked out
   	 {
   		 long frsVstTime = se.getSession().getCreationTime();		// fresh visit time
   		 long curTime = System.currentTimeMillis();	// current time (when client checked out)
   		 
   		 updateAvgCheckOutTime(se, curTime - frsVstTime);
   	 } 
    }

	/**
     * @see HttpSessionAttributeListener#attributeReplaced(HttpSessionBindingEvent)
     */
    public void attributeReplaced(HttpSessionBindingEvent se)  { 
   	 if (se.getName().equals("cartItemsCount"))
   	 {
   		 long lstMnpTime = (long) se.getSession().getAttribute("cartLastManipTime");	// item last added time
   		 long curTime = System.currentTimeMillis();	// current time
   		 int oldCount = (int) se.getValue();
   		 int newCount = (int) se.getSession().getAttribute("cartItemsCount");
   		 
   		 // when there is item added to the cart
   		 if (newCount > oldCount)
   		 {
   			 updateAvgAddItemTime(se, curTime - lstMnpTime);
   			 se.getSession().setAttribute("cartLastManipTime", curTime);
   		 }
   	 }
    }
    
    // Do not remove static, since we need class-wide synchronization
    private static synchronized void updateAvgAddItemTime(HttpSessionBindingEvent evn, long duration)
    {
   	 if (evn.getSession().getServletContext().getAttribute("avgAddItemTime") == null)
   	 {
   		 // when the very first item added 
   		 evn.getSession().getServletContext().setAttribute("avgAddItemTime", duration);
   		 evn.getSession().getServletContext().setAttribute("NItemsAdded", (long) 1);
   	 }
   	 else
   	 {
   		 long avgTime = (long) evn.getSession().getServletContext().getAttribute("avgAddItemTime");
   		 long N = (long) evn.getSession().getServletContext().getAttribute("NItemsAdded");
   		 
   		 // cast to double for computation
   		 double durAcc = (new Long(duration)).doubleValue();
   		 double avgTimeAcc = (new Long(avgTime)).doubleValue();
   		 double NAcc = (new Long(N)).doubleValue();
   		 // update statistic
   		 double newAvgTime = (NAcc / (NAcc + 1.0)) * avgTimeAcc + (1.0 / (NAcc + 1.0)) * durAcc;
   		 
   		 evn.getSession().getServletContext().setAttribute("avgAddItemTime", (new Double(newAvgTime)).longValue());
   		 evn.getSession().getServletContext().setAttribute("NItemsAdded", N + 1);
   	 }
    }
    
    // Do not remove static, since we need class-wide synchronization
    private static synchronized void updateAvgCheckOutTime(HttpSessionBindingEvent evn, long duration)
    {
   	 if (evn.getSession().getServletContext().getAttribute("avgCheckOutTime") == null)
   	 {
   		 // when the very order placed
   		 evn.getSession().getServletContext().setAttribute("avgCheckOutTime", duration);
   		 evn.getSession().getServletContext().setAttribute("NCheckedOut", (long) 1);
   	 }
   	 else
   	 {
   		 long avgTime = (long) evn.getSession().getServletContext().getAttribute("avgCheckOutTime");
   		 long N = (long) evn.getSession().getServletContext().getAttribute("NCheckedOut");
   		 
   		// cast to double for computation
   		 double durAcc = (new Long(duration)).doubleValue();
   		 double avgTimeAcc = (new Long(avgTime)).doubleValue();
   		 double NAcc = (new Long(N)).doubleValue();
   		 // update statistic
   		 double newAvgTime = (NAcc / (NAcc + 1.0)) * avgTimeAcc + (1.0 / (NAcc + 1.0)) * durAcc;
   		 
   		 evn.getSession().getServletContext().setAttribute("avgCheckOutTime", (new Double(newAvgTime)).longValue());
   		 evn.getSession().getServletContext().setAttribute("NCheckedOut", N + 1);
   	 }
    }
}
