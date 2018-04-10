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
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;

import tools.Local;
import tools.Misc;

/*
 * @author Vlad https://dreambot.org/forums/index.php/user/20-vlad/
 */

@ScriptManifest(name = "Lava Runner", author = "A q p", description = "[DEV] Lava Runner", version = 1, category = Category.RUNECRAFTING)
public class Script extends AbstractScript {
	
	//Variables
	private final Area bankArea = new Area(3380, 3273, 3384, 3267, 0);			//The area of the bank
	private final Area innerAltarArea = new Area(2560, 4860, 2600, 4820, 0);	//The area of the (inner) altar
	//private final Area outerAltarArea = new Area(3317, 3259, 3309, 3251, 0);	//The area of the (outer) altar
	private final Area outerAltarArea = new Area(new Tile(3317, 3259), new Tile(3309, 3251));	//The area of the (outer) altar
	private final Tile bankTile = new Tile(3381, 3268, 0);						//The specific tile to use in the bank
	private final Tile exitAltarTile = new Tile(2575, 4849, 0);
	private Timer t = new Timer();		//A timer
	public Timer lastTrade = new Timer();

	@Override
	public void onStart() {
		this.getWalking().setRunThreshold(50);
		Misc.printDev("Started Lava Runner: "+Misc.getTimeStamp());
	}

	@Override
	public int onLoop() {
		checkForMods();
		Calculations.random(800, 1200);

		//must be logged in
		if (!getClient().isLoggedIn()) {
			return Misc.smallSleep();
		}
		
		

		//if we should bank
		if (needToBank()) {
			if (Config.EXTREME_DEBUGGING) {
				Misc.smallSleep();
				log("We need to bank!");
			}
			//check if we're in runecrafting area
			if (innerAltarArea.contains(getLocalPlayer())) {
				if (Config.EXTREME_DEBUGGING) {
					Misc.smallSleep();
					log("We're inside the inner Altar.");
				}
				//then walk out of runecrafting area
				if (Config.EXTREME_DEBUGGING) {
					Misc.smallSleep();
					log("We're trying to exit the inner altar!");
				}
				if (getWalking().walk(exitAltarTile)) {
					Misc.smallSleep();
				}
				Misc.smallSleep();
				
				if (Config.EXTREME_DEBUGGING) {
					Misc.smallSleep();
					log("We want to use the portal!");
				}
				GameObject portal = getGameObjects().closest("Portal");
				if (portal != null) {
					if (portal.interact("Use")) {
						Misc.smallSleep();
					}
					Misc.smallSleep();
				}
			} else
			//if we are not in the bank,
			if (!bankArea.contains(getLocalPlayer())) {
				if (Config.EXTREME_DEBUGGING) {
					Misc.smallSleep();
					log("We are not in the bank");
				}
				//walk to the bank.
				if (Config.EXTREME_DEBUGGING) {
					Misc.smallSleep();
					log("So we'll walk to the bank!");
				}
				if (getWalking().walk(bankTile)) {
					Misc.smallSleep();
				}
			//we are in the bank
			} else {
				if (Config.EXTREME_DEBUGGING) {
					Misc.smallSleep();
					log("We are in the bank!");
				}
				//so, bank
				handleBanking();
			}
		//don't need to bank
		} else {
			if (!outerAltarArea.contains(getLocalPlayer()) && !innerAltarArea.contains(getLocalPlayer())) { //if we not inside any altaer
				if (Config.EXTREME_DEBUGGING) {
					Misc.smallSleep();
					log("We aren't in ruins and aren't inside ruins, so we want to walk into the outer ruins!");
				}
				if (getWalking().walk(outerAltarArea.getRandomTile())) {
					Misc.smallSleep();
				}
			}
			
			if (outerAltarArea.contains(getLocalPlayer())){											//if we're inside the outside altar area
				if (Config.EXTREME_DEBUGGING) {
					Misc.smallSleep();
					log("We want to use the Ruins!");
				}
				GameObject ruins = getGameObjects().closest("Mysterious ruins");
				if (ruins != null) {
					if (ruins.interact("Enter")) { //enter the ruins
						Misc.smallSleep();
					}
					Misc.smallSleep();
				}
			}
			
			if (innerAltarArea.contains(getLocalPlayer())) {											//if we're in the inner area
				if (Config.EXTREME_DEBUGGING) {
					Misc.smallSleep();
					log("We would handle trading here.");
				}
				handleTrading();
			} 
			
			
		}
		

		return 100;
	}
	
	public void checkForMods() {
		Player p = getPlayers().all(f -> f != null && f.getName().contains("Mod")).get(0);
		String mod = "Mod";
		if (p != null) {
			mod = p.getName();
		}
		log("We just found "+mod+"! Script session suspended. Time: "+Misc.getTimeStamp());
		getTabs().logout();
		stop();
	}
	
	
	public void handleTrading() {
		Player target = getPlayers().closest(Local.lavaBoss);
		//(p -> p != null && p.getName().equals(Local.lavaBoss)).get(0);
		if (target == null) {
			log("Attempting to trade a nulled master! We're lost! Error #404: "+Misc.getTimeStamp());
			return;
		}
		
		if (innerAltarArea.contains(target)) {
			log("The master is not inside the altar area. Eror #405");
			return;
		}
		
		if (!getTrade().isOpen()) { //if the trade is not open
			if (target.isStandingStill()) { //and out master isn't busy
				if (lastTrade.elapsed() > Config.TRADE_COOLDOWN) { //and we aren't spamming the master
					if (getTrade().tradeWithPlayer(target.getName())) { //we can try to trade master
						lastTrade.reset();	//reset timer
						Misc.smallSleep();	//sleep on that thought
					}
				}
			}
		} else if (getTrade().isOpen(1)) { //if the trade(1) is open
			if (getTrade().addItem("Pure essence", Config.ESSENCE_TO_WITHDRAW)) {
				Misc.smallSleep();
			}
			if (getTrade().acceptTrade()) { // accept trade
				Misc.medSleep();
			}
		} else if (getTrade().isOpen(2)) { //if the trade(2) is open
			if (getTrade().acceptTrade()) { // accept trade
				Misc.longSleep();
			}
		}
		

		

		
		
		
	}
	
	public boolean needToBank() {
		if (!getInventory().contains("Pure essence") || getInventory().count("Pure essence") < Config.NEED_BANK_THRESHOLD) {
			return true;
		}
		return false;
	}
	
	public void handleBanking() {
		if (!getBank().isOpen()) {
			getBank().openClosest();
			Misc.smallSleep();
		} else { //bank is open
			if (!getBank().placeHoldersEnabled()) {
				getBank().togglePlaceholders(true);
				Misc.smallSleep();
			}
			if (getBank().getWithdrawMode() == BankMode.NOTE) {
				getBank().setWithdrawMode(BankMode.ITEM);
				Misc.smallSleep();
			}
			getBank().depositAllItems();
			Misc.smallSleep();
			getBank().withdraw("Pure essence", Config.ESSENCE_TO_WITHDRAW);
			Misc.smallSleep();
			getBank().close();
			Misc.smallSleep();
		}
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
}