package lavaRunner;

/**
 * This class will contain all configuration for the lava runner script.
 * Ideally these would be prompted & saved somewhere.
 */
public class Config {
	
	/**
	 * The amount of essence each mule should withdraw. Must be <= 28
	 */
	public static final int ESSENCE_TO_WITHDRAW = 27; 
	
	/**
	 * The minimum amount of essence required in inventory for a trade to occur.
	 */
	public static final int NEED_BANK_THRESHOLD = 10;

}
