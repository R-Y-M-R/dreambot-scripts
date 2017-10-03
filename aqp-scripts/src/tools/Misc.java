package tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.dreambot.api.Client;

/**
 * This is a essentially a compilation of functions I use across my scripts.
 * @author R-Y-M-R
 *
 */
public class Misc {
	
	/**
	 * Uses regionMatches to determine if strings match regardless of case
	 * @param str	 the CharSequence to check, may be null
	 * @param searchStr	the CharSequence to find, may be null
	 * @return
	 */
	public static boolean containsIgnoreCase(String str, String searchStr)     {
	    if(str == null || searchStr == null) return false;

	    final int length = searchStr.length();
	    if (length == 0)
	        return true;

	    for (int i = str.length() - length; i >= 0; i--) {
	        if (str.regionMatches(true, i, searchStr, 0, length))
	            return true;
	    }
	    return false;
	}
	
	/**
	 * Grabs user's forum name
	 * @return	the user's forum profile name
	 */
	public static String getForumUser() {
		return Client.getForumUser().getUsername();
	}
	
	/**
	 * This method will print things hidden by default for end users.
	 * @param	message	a String to be used as output
	 */
	public static void printDev(String message) {	
		if (!Client.getForumUser().getUsername().equalsIgnoreCase("A q p")) {
			return;
		}
		System.out.println(message);
	}
	
	/**
	 * This method will determine date & time
	 * @return	the date & time in format "MMM-dd-yyyy '@' HH:mm"
	 * 
	 */
	public static String getTimeStamp() { 
		//new SimpleDateFormat("MMM dd,yyyy HH:mm")
		return new SimpleDateFormat("MMM-dd-yyyy '@' HH:mm").format(new Date(System.currentTimeMillis()));
	}

}
