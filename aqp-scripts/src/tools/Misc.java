package tools;

public class Misc {
	
	/**
	 * This method will print things hidden by default for end users.
	 * 
	 * @param	message	a String to be used as output
	 */
	public static void printDev(String message) {
		if (false) {
			return;
		}
		System.out.println(message);
	}

}
