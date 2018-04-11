package lavaRunner;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.MessageListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.widgets.message.Message;

import tools.Local;
import tools.Misc;

/*
 * @author Vlad https://dreambot.org/forums/index.php/user/20-vlad/
 */

@ScriptManifest(name = "Lava Runner", author = "A q p", description = "[DEV] Lava Runner", version = 1, category = Category.RUNECRAFTING)
public class Script extends AbstractScript implements MessageListener {
	
	//Variables
	private final Area bankArea = new Area(3380, 3273, 3384, 3267, 0);			//The area of the bank
	private final Area innerAltarArea = new Area(2560, 4860, 2600, 4820, 0);	//The area of the (inner) altar
	//private final Area outerAltarArea = new Area(3317, 3259, 3309, 3251, 0);	//The area of the (outer) altar
	private final Area outerAltarArea = new Area(new Tile(3317, 3259), new Tile(3309, 3251));	//The area of the (outer) altar
	private final Tile bankTile = new Tile(3381, 3268, 0);						//The specific tile to use in the bank
	private final Tile exitAltarTile = new Tile(2575, 4849, 0);
	private Timer t = new Timer();		//A timer
	private Timer lastTrade = new Timer();
	private Timer resetCamera = new Timer();
	
	private boolean acc1 = true;
	private boolean acc2 = true;
	private boolean skipFirstTradeWait = true;

	@Override
	public void onStart() {
		this.getWalking().setRunThreshold(50);
		lastTrade.setRunTime(Config.TRADE_COOLDOWN+1);
		Misc.printDev("Started Lava Runner: "+Misc.getTimeStamp());
		resetRunThreshold();
	}
	
	public void resetRunThreshold() {
		getWalking().setRunThreshold(Calculations.random(40, 50));
	}

	@Override
	public int onLoop() {
		checkForMods();

		//must be logged in
		if (!getClient().isLoggedIn()) {
			smallSleep();
			return 100;
		}
		
		if (resetCamera.elapsed() > Config.CAMERA_RESET) {
			resetCamera();
			resetCamera.reset();
		}
		
		

		//if we should bank
		if (needToBank()) {
			resetRunThreshold();
			
			if (Config.EXTREME_DEBUGGING) {
				log("We need to bank!");
			}
			//check if we're in runecrafting area
			if (innerAltarArea.contains(getLocalPlayer())) {
				if (Config.EXTREME_DEBUGGING) {
					log("We're inside the inner Altar.");
				}
				//then walk out of runecrafting area
				if (Config.EXTREME_DEBUGGING) {
					log("We're trying to exit the inner altar!");
				}
				if (getWalking().walk(exitAltarTile)) {
					smallAfkSleep();
				}
				smallSleep();
				
				if (Config.EXTREME_DEBUGGING) {
					log("We want to use the portal!");
				}
				GameObject portal = getGameObjects().closest("Portal");
				if (portal != null) {
					if (portal.interact("Use")) {
						longSleep();
					}
					smallSleep();
				}
			} else
			//if we are not in the bank,
			if (!bankArea.contains(getLocalPlayer())) {
				if (Config.EXTREME_DEBUGGING) {
					log("We are not in the bank");
				}
				//walk to the bank.
				if (Config.EXTREME_DEBUGGING) {
					log("So we'll walk to the bank!");
				}
				if (getWalking().walk(bankTile)) {
					smallAfkSleep();
				}
			//we are in the bank
			} else {
				if (Config.EXTREME_DEBUGGING) {
					log("We are in the bank!");
				}
				//so, bank
				handleBanking();
			}
		//don't need to bank
		} else {
			if (!outerAltarArea.contains(getLocalPlayer()) && !innerAltarArea.contains(getLocalPlayer())) { //if we not inside any altaer
				if (Config.EXTREME_DEBUGGING) {
					log("We aren't in ruins and aren't inside ruins, so we want to walk into the outer ruins!");
				}

				if (getWalking().walk(outerAltarArea.getNearestTile(getLocalPlayer()))) {
					smallAfkSleep();
				}
			}
			
			if (outerAltarArea.contains(getLocalPlayer())){											//if we're inside the outside altar area
				if (Config.EXTREME_DEBUGGING) {
					log("We want to use the Ruins!");
				}
				GameObject ruins = getGameObjects().closest("Mysterious ruins");
				if (ruins != null) {
					if (ruins.interact("Enter")) { //enter the ruins
						longSleep();
					}
					smallSleep();
				}
			}
			
			if (innerAltarArea.contains(getLocalPlayer())) {											//if we're in the inner area
				if (Config.EXTREME_DEBUGGING) {
					log("We would handle trading here.");
				}
				handleTrading();
			} 
			
			
		}
		

		return 100;
	}
	
	public void resetCamera() {
		if (getCamera().getYaw() > 1500 || getCamera().getYaw() < 500) {
			if (Config.EXTREME_DEBUGGING) {
				log("Our yaw: "+getCamera().getYaw()+", needs to be reset! "+Misc.getTimeStamp());
			}
			getCamera().rotateToPitch(383);
			getCamera().rotateToYaw(0);
		}
	}
	
	public void checkForMods() {
		if (!getPlayers().all(f -> f != null && f.getName().contains("Mod")).isEmpty()) {
			log("We just found a JMod! Logged out, quickly... Time: " + Misc.getTimeStamp());
			getTabs().logout();
			stop();
		}
	}
	
	
	public void handleTrading() {
		Player target = getPlayers().closest(Local.lavaBoss);
		//(p -> p != null && p.getName().equals(Local.lavaBoss)).get(0);
		if (target == null) {
			log("Attempting to trade a nulled master! We're lost! Error #404: "+Misc.getTimeStamp());
			return;
		}
		
		if (!innerAltarArea.contains(target)) {
			log("The master is not inside the altar area. Eror #405");
			return;
		}
		
		if (!getTrade().isOpen()) { //if the trade is not open
			if (Config.EXTREME_DEBUGGING) {
				log("trade is not open");
			}
			if (target.getAnimation() == -1) { //and out master isn't busy
				if (Config.EXTREME_DEBUGGING) {
					log("master is not busy");
				}
				if (lastTrade.elapsed() > Config.TRADE_COOLDOWN || skipFirstTradeWait) { //and we aren't spamming the master
					if (skipFirstTradeWait) {
						skipFirstTradeWait = false;
					}
					if (Config.EXTREME_DEBUGGING) {
						log("we're passed countdown");
					}
					if (getTrade().tradeWithPlayer(target.getName())) { //we can try to trade master
						if (Config.EXTREME_DEBUGGING) {
							log("we try to trade master");
						}
						lastTrade.reset();	//reset timer
						acc1 = false;
						acc2 = false;
						smallSleep();	//sleep on that thought
					}
				}
			}
		} else if (getTrade().isOpen(1)) { //if the trade(1) is open
			if (!getTrade().contains(true, 1, "Pure essence")) {
				if (getTrade().addItem("Pure essence", Config.ESSENCE_TO_WITHDRAW)) {
					if (Config.EXTREME_DEBUGGING) {
						log("Attempting to trade "+Config.ESSENCE_TO_WITHDRAW+" x Pure Essence");
					}
					smallSleep();
				}
			}
			
			if (getTrade().acceptTrade() && !acc1) { // accept trade
				if (Config.EXTREME_DEBUGGING) {
					log("Accepting trade (1)");
				}
				acc1 = true;
				medSleep();
			}
		} else if (getTrade().isOpen(2) && !acc2) { //if the trade(2) is open
			if (getTrade().acceptTrade()) { // accept trade
				if (Config.EXTREME_DEBUGGING) {
					log("Accepting trade (2)");
				}
				acc2 = true;
				medSleep();
			}
		}
		

		

		
		
		
	}
	
	public boolean needToBank() {
		if (getTrade().isOpen()) {
			return false;
		}
		if (!getInventory().contains("Pure essence") || getInventory().count("Pure essence") < Config.NEED_BANK_THRESHOLD) {
			return true;
		}
		return false;
	}
	
	public void handleBanking() {
		if (!getBank().isOpen()) {
			getBank().openClosest();
			medSleep();
		} else { //bank is open
			if (!getBank().placeHoldersEnabled()) {
				getBank().togglePlaceholders(true);
				smallSleep();
			}
			if (getBank().getWithdrawMode() == BankMode.NOTE) {
				getBank().setWithdrawMode(BankMode.ITEM);
				smallSleep();
			}
			if (!getInventory().isEmpty()) {
				getBank().depositAllItems();
				smallSleep();
			}
			smallSleep();
			getBank().withdraw("Pure essence", Config.ESSENCE_TO_WITHDRAW);
			//smallSleep();
			//getBank().close();
			medSleep();
		}
	}
	
	/**
	 * Useful for small sleeps
	 * @return	an int between 200 and 600
	 */
	public static void smallSleep() {
		sleep(Calculations.random(200, 600));
	}
	
	/**
	 * Useful for medium sleeps
	 * @return	an int between 600 and 1400
	 */
	public static void medSleep() {
		sleep(Calculations.random(600, 1400));
	}
	
	/**
	 * Useful for long sleeps
	 * @return	an int between 1400 and 4500
	 */
	public static void longSleep() {
		sleep(Calculations.random(1400, 4500)); 
	}
	
	/**
	 * Useful for a small afk
	 * @return an int between 3500 and 6000
	 */
	public static void smallAfkSleep() {
		sleep(Calculations.random(3500, 6000));
	}
	

	@Override
	public void onExit() {
		Misc.printDev("Ended: "+Misc.getTimeStamp());
	}

	public void onPaint(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", 1, 11));
		g.drawString("Time Running: " + t.formatTime(), 25, 50);
	}

	@Override
	public void onGameMessage(Message msg) {
		if (Config.EXTREME_DEBUGGING) {
			log("onGameMessage trading offer");
		}
	}

	@Override
	public void onPlayerMessage(Message msg) {
		if (Config.EXTREME_DEBUGGING) {
			log("onPlayerMessage trading offer");
		}
	}

	@Override
	public void onPrivateInMessage(Message msg) {
		if (Config.EXTREME_DEBUGGING) {
			log("onPrivateInMessage trading offer");
		}
	}

	@Override
	public void onPrivateOutMessage(Message msg) {
		if (Config.EXTREME_DEBUGGING) {
			log("onPrivateOutMessage trading offer");
		}
	}

	@Override
	public void onTradeMessage(Message msg) {
		//trade master
		log("onTradeMessage trading offer");
		if (!msg.getMessage().contains(Local.lavaBoss)) {
			log("Got a trade from... somebody else?");
			return;
		}
		Player target = getPlayers().closest(Local.lavaBoss);
		if (target == null) {
			log("Attempting to trade a nulled master! We're lost! Error #404: "+Misc.getTimeStamp());
			return;
		}
		if (getTrade().tradeWithPlayer(target.getName())) { //we can try to trade master
			if (Config.EXTREME_DEBUGGING) {
				log("we try to trade master");
			}
			lastTrade.reset();	//reset timer
			acc1 = false;
			acc2 = false;
			smallSleep();	//sleep on that thought
		}
		
	}
}