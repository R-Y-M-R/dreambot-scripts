package lavaRunner;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.path.impl.LocalPath;
import org.dreambot.api.methods.walking.pathfinding.impl.dijkstra.DijkstraPathFinder;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.MessageListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;

import tools.Local;
import tools.Misc;

/*
 * @author Vlad https://dreambot.org/forums/index.php/user/20-vlad/
 */

@ScriptManifest(name = "Lava Runner", author = "A q p", description = "[DEV] Brings lava runes to the master. Now with Ring of Dueling support.", version = 1.4, category = Category.RUNECRAFTING)
public class Script extends AbstractScript implements MessageListener {
	
	//Variables
	private final Tile bankTile = new Tile(3381, 3268, 0);						//The specific tile to use in the bank
	private final Tile altarTile = new Tile(3310, 3252, 0);
	private final Tile exitAltarTile = new Tile(2575, 4849, 0);
	private final Tile awayFromRampTile = new Tile(3324, 3259, 0);
	private final Area bankArea = new Area(3380, 3273, 3384, 3267, 0);			//The area of the bank
	private final Area innerAltarArea = new Area(2560, 4860, 2600, 4820, 0);	//The area of the (inner) altar
	private final Area outerAltarArea = new Area(new Tile(3317, 3259, 0), new Tile(3309, 3251, 0));	//The area of the (outer) altar
	private final Area badCameraArea = new Area(altarTile, awayFromRampTile);
	private final Area castleWarsArea = new Area(new Tile(2446, 3097, 0), new Tile(2438, 3082, 0));
	private final Tile castleWarsBankTile = new Tile(2443, 3083, 0);
	private Timer t = new Timer();		//A timer
	private Timer lastTrade = new Timer();
	private Timer resetCamera = new Timer();
	private boolean skipFirstTradeWait = true;
	
	private void printOthersEquipment(Player target) {
		List<String> list = this.getOthersEquipment(target);
		log("---" + target.getName() + "---");
		for (String s : list) {
			log(s);
		}
		log("---/" + target.getName() + "---");

		if (!innerAltarArea.contains(target)) {
			log("The master is not inside the altar area. Eror #405");
			return;
		}
	}
	
	private boolean wearingItem(Player target, String itemName) {
		List<String> list = this.getOthersEquipment(target);
		for (String s : list) {
			if (s.equals(itemName)) {
				return true;
			}
		}
		return false;
	}
	
	private List<String> getOthersEquipment(Player p) {
        List<String> equipmentList = new LinkedList<String>();
        if(p != null) {
            int[] equipment = p.getComposite().getApperance();
            for (int i = 0; i < equipment.length; i++) {
                if (equipment[i] - 512 > 0) {
                    equipmentList.add(new Item(equipment[i]-512, 0, getClient().getInstance()).getName());
                }
            }
        }
        return equipmentList;
    }
	
	private boolean walkPathToBank() {
		log("Dfjk*path to bank");
		DijkstraPathFinder df = getWalking().getDijPathFinder();
		Tile start = altarTile;
		Tile end = awayFromRampTile;
		LocalPath<Tile> path = df.calculate(start, end);
		return path.walk();
	}
	
	/*private boolean walkPathToAltar() {
		log("a*Path to altar");
		AStarPathFinder pf = getWalking().getAStarPathFinder();
		Tile start = bankTile;
		Tile end = altarTile;
		LocalPath<Tile> path = pf.calculate(start, end);
		return path.walk();
	}*/

	@Override
	public void onStart() {
		this.getWalking().setRunThreshold(50);
		lastTrade.setRunTime(Config.TRADE_COOLDOWN+1);
		Misc.printDev("Started Lava Runner: "+Misc.getTimeStamp());
		resetRunThreshold();
	}
	
	private void resetRunThreshold() {
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
		
		if (this.getLocalPlayer().getAnimation() != -1) {
			if (Config.EXTREME_DEBUGGING) {
				log("We're waiting because anims.");
			}
			return 100;
		}
		
		/*if (tiltCameraArea.contains(getLocalPlayer())) {
			turnCamera();
		}*/
		
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
				if (Config.RING_OF_DUELING && getEquipment().contains(ring -> ring.getName().contains("dueling"))) {
					log("RoD tp to Castle Wars");
					if (getEquipment().interact(EquipmentSlot.RING, "Castle Wars")) {
						longSleep();
					}
				} else if (getWalking().walk(exitAltarTile)) {
					smallSleep();
				}
				smallSleep();
				
				if (Config.EXTREME_DEBUGGING) {
					log("We want to use the portal!");
				}
				GameObject portal = getGameObjects().closest("Portal");
				if (portal != null) {
					if (portal.interact("Use")) {
						medSleep();
					}
					smallSleep();
				}
			} else
			//if we are not in the bank,
			if (!bankArea.contains(getLocalPlayer()) && !castleWarsArea.contains(getLocalPlayer())) {
				if (Config.EXTREME_DEBUGGING) {
					log("We are not in the bank");
					log("So we'll walk to the bank!");
				}
				//walk to the bank.
				if (badCameraArea.contains(getLocalPlayer())) {
					walkPathToBank();
				} else if (getWalking().walk(bankTile)) {
					medSleep();
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
			
			if (this.getInventory().contains("Ring of dueling(8)") && !getEquipment().contains(ring -> ring.getName().contains("dueling"))) {
				log("Equipping Ring of Dueling");
				if (getInventory().interact("Ring of dueling(8)", "Wear")) {
					smallSleep();
				}
			}
			if (Config.RING_OF_DUELING && getEquipment().contains(ring -> ring.getName().contains("dueling")) && this.castleWarsArea.contains(getLocalPlayer())) {
				log("RoD tp to Duel Arena");
				if (getEquipment().interact(EquipmentSlot.RING, "Duel Arena")) {
					longSleep();
				}
			}  else if (!outerAltarArea.contains(getLocalPlayer()) && !innerAltarArea.contains(getLocalPlayer())) { //if we not inside any altaer
				if (Config.EXTREME_DEBUGGING) {
					log("We aren't in ruins and aren't outside ruins, so we want to walk to outer ruins!");
				}

				if (getWalking().walk(altarTile)) {
					smallSleep();
				}
			}
			
			if (outerAltarArea.contains(getLocalPlayer())){											//if we're inside the outside altar area
				if (Config.EXTREME_DEBUGGING) {
					log("We want to use the Ruins!");
				}
				GameObject ruins = getGameObjects().closest("Mysterious ruins");
				if (ruins != null) {
					if (ruins.interact("Enter")) { //enter the ruins
						medSleep();
					}
					smallSleep();
				}
			}
			
			if (innerAltarArea.contains(getLocalPlayer()) && (getInventory().count("Pure essence") > Config.NEED_BANK_THRESHOLD || getTrade().isOpen())) {											//if we're in the inner area
				if (Config.EXTREME_DEBUGGING) {
					log("We would handle trading here.");
				}
				handleTrading();
			} 
			
			
		}
		

		return 200;
	}
	
	/*private void turnCamera() {
		getCamera().rotateToYaw(Calculations.random(1, 10));
		getCamera().rotateToPitch(383);

	}*/
	
	private void resetCamera() {
		if (getCamera().getYaw() > 1500 || getCamera().getYaw() < 500) {
			if (Config.EXTREME_DEBUGGING) {
				log("Our yaw: "+getCamera().getYaw()+", needs to be reset! "+Misc.getTimeStamp());
			}
			getCamera().rotateToYaw(Calculations.random(1, 10));
			if (getCamera().getPitch() < 200) {
				if (Config.EXTREME_DEBUGGING) {
					log("Our pitch: "+getCamera().getPitch()+", needs to be reset! "+Misc.getTimeStamp());
				}
				getCamera().rotateToPitch(383);
			}
		}
	}
	
	private void checkForMods() {
		if (!getPlayers().all(f -> f != null && f.getName().contains("Mod")).isEmpty()) {
			log("We just found a JMod! Logged out, quickly... Time: " + Misc.getTimeStamp());
			getTabs().logout();
			stop();
		}
	}
	
	
	private void handleTrading() {
		Player target = getPlayers().closest(Local.lavaBoss);
		if (target == null) {
			log("Attempting to trade a nulled master! We're lost! Error #404: "+Misc.getTimeStamp());
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
						smallSleep();	//sleep on that thought
					}
				}
			}
		} 
		if (getTrade().isOpen(1)) { //if the trade(1) is open
			if (!getTrade().contains(true, 1, "Pure essence")) {
				if (Config.MULE_BINDINGS) {
					if (!wearingItem(target, "Binding necklace")) {
						log("Master is not wearing a binding necklace. Offering ours.");
						if (this.getInventory().contains("Binding necklace")) {
							this.getTrade().addItem("Binding necklace", 1);
						}
					}
				}
				if (getTrade().addItem("Pure essence", getTrade().contains(true, 1, "Binding necklace") ? Config.ESSENCE_TO_WITHDRAW-1 : Config.ESSENCE_TO_WITHDRAW)) {
					if (Config.EXTREME_DEBUGGING) {
						log("Trading over our Pure Essence");
					}
					smallSleep();
				}
			}
			
			if (getTrade().acceptTrade()) { // accept trade
				if (Config.EXTREME_DEBUGGING) {
					log("Accepting trade (1)");
				}
				smallSleep();
			}
		}
		
		if (getTrade().isOpen(2)) { //if the trade(2) is open
			if (getTrade().acceptTrade()) { // accept trade
				if (Config.EXTREME_DEBUGGING) {
					log("Accepting trade (2)");
				}
				smallSleep();
			}
		}
		
		

		

		
		
		
	}
	
	private boolean needToBank() {
		if (getTrade().isOpen()) {
			return false;
		}
		if (!getInventory().contains("Pure essence") || getInventory().count("Pure essence") < Config.NEED_BANK_THRESHOLD || getInventory().get("Pure essence").isNoted()) {
			return true;
		}
		return false;
	}
	
	private boolean shouldDumpInventory() {
		
		if (this.getInventory().get("Pure essence") != null) {
			if (this.getInventory().get("Pure essence").isNoted()) {
				return true;
			}
		}
		
		int ess = this.getInventory().count("Pure essence");
		int RoD = this.getInventory().count("Ring of dueling(8)");
		int Bind = this.getInventory().count("Binding necklace");
		
		if (ess+RoD+Bind == (28 - this.getInventory().emptySlotCount())) {
			return false;
		}
		
		return true;
	}
	
	private void handleBanking() {
		if (!getBank().isOpen()) {
			if (getBank().openClosest()) {
				smallSleep();
			}
		} else { //bank is open
			if (!getBank().placeHoldersEnabled()) {
				getBank().togglePlaceholders(true);
				smallSleep();
			}
			if (getBank().getWithdrawMode() == BankMode.NOTE) {
				getBank().setWithdrawMode(BankMode.ITEM);
				smallSleep();
			}
			if (shouldDumpInventory()) {
				getBank().depositAllItems();
				smallSleep();
			}
			if (Config.MULE_BINDINGS) {
				if (!this.getInventory().contains("Binding necklace")) {
					getBank().withdraw("Binding necklace", 1);
					smallSleep();
				}
			}
			if (Config.RING_OF_DUELING) {
				if (!getEquipment().contains(ring -> ring.getName().contains("dueling"))) {
					getBank().withdraw("Ring of dueling(8)", 1);
				}
			}
			getBank().withdraw("Pure essence", Config.ESSENCE_TO_WITHDRAW);
			smallSleep();
			getBank().close();
			smallSleep();
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
			smallSleep();	//sleep on that thought
		}
		
	}
}