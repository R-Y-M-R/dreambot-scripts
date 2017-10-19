package tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.dreambot.api.Client;
import org.dreambot.api.wrappers.interactive.Player;

/**
 * This is a essentially a compilation of functions I use across my scripts.
 * @author R-Y-M-R
 *
 */
public abstract class Misc {
	
	/**
	 * Converts a list into a combined string
	 * @param list	a list of Strings
	 * @return	the strings added to a single String
	 */
	public static String listToString(List<String> list) {
		String out = "";
		for (int i = 0; i < list.size(); i++) {
			out += toCapitalizedLowercase(list.get(i));
			if (i != list.size()-1) {
				out += ", ";
			}
		}
		return out;
	}
	
	/**
	 * First letter will be UPPERCASE, others lowercase
	 * @param unformatted	the original string
	 * @return 	the string with the first letter capitalized, and other letters in lower case
	 */
	public static String toCapitalizedLowercase(String unformatted) {
		return unformatted.substring(0, 1).toUpperCase() + unformatted.substring(1).toLowerCase();
	}
	
	/**
	 * Uses a for loop to search for a matching player. Can return null.
	 * @param 	playerName the player who's name we're searching for
	 * @param 	list a current list of all players
	 * @return	a Player from list who matches playerName
	 */
	public static Player getPlayerByName(String playerName, List<Player> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getName().equalsIgnoreCase(playerName)) {
				return list.get(i);
			}
		}
		printDev("getPlayerByName has returned null!");
		return null;
	}
	
	/**
	 * Uses regionMatches to determine if strings match regardless of case
	 * @param str	 the CharSequence to check, may be null
	 * @param searchStr	the CharSequence to find, may be null
	 * @return
	 */
	public static boolean containsIgnoreCase(String str, String searchStr)     {
	    if(str == null || searchStr == null) { 
	    	return false;
	    }
	    final int length = searchStr.length();
	    if (length == 0) {
	        return true;
	    }
	    for (int i = str.length() - length; i >= 0; i--) {
	        if (str.regionMatches(true, i, searchStr, 0, length)) {
	            return true;
	        }
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
	 * Determines if you should see printDev messages
	 * @return	true if forum name is "A q p"
	 */
	public static final boolean shouldPrintDev() {
		if (!getForumUser().equalsIgnoreCase("A q p")) {
			return false;
		}
		return true;
	}
	
	/**
	 * This method will print things hidden by default for end users.
	 * @param	message	a String to be used as output
	 */
	public static void printDev(String message) {	
		if (!shouldPrintDev()) {
			return;
		}
		System.out.println(message);
	}
	
	/**
	 * This method will determine date & time
	 * @return	the date & time in format "MMM-dd-yyyy '@' HH:mm:ss z"
	 * 
	 */
	public static String getTimeStamp() { 
		return new SimpleDateFormat("MMM-dd-yyyy '@' HH:mm").format(new Date(System.currentTimeMillis()));
	}

}
