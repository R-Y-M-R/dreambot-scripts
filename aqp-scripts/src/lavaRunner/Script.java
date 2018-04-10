package lavaRunner;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;

import tools.Misc;

/*
 * @author Vlad https://dreambot.org/forums/index.php/user/20-vlad/
 */

@ScriptManifest(name = "Lava Runner", author = "A q p", description = "[DEV] Lava Runner", version = 1, category = Category.RUNECRAFTING)
public class Script extends AbstractScript {
	
	//Variables
	private final Area bankArea = new Area(3380, 3267, 3384, 3273, 0);			//The area of the bank
	private final Area innerAltarArea = new Area(2560, 4860, 2600, 4820, 0);	//The area of the (inner) altar
	private final Area outerAltarArea = new Area(3317, 3259, 3309, 3251, 0);	//The area of the (outer) altar
	private final Tile bankTile = new Tile(3381, 3268, 0);						//The specific tile to use in the bank
	private final Tile exitAltarTile = new Tile(2575, 4849, 0);
	private Timer t = new Timer();												//A timer

	@Override
	public void onStart() {
		Misc.printDev("Started Lava Runner at "+Misc.getTimeStamp());
	}

	@Override
	public int onLoop() {

		//must be logged in
		if (!getClient().isLoggedIn()) {
			return Misc.smallSleep();
		}
		
		//if we should bank
		if (needToBank()) {
			//check if we're in runecrafting area
			if (containsLocalPlayer(innerAltarArea)) {
				//then walk out of runecrafting area
				getWalking().walk(exitAltarTile);
				GameObject portal = getGameObjects().closest("Portal");
				if (portal != null) {
					portal.interact("Use");
					Misc.smallSleep();
				}
			} else
			//if we are not in the bank,
			if (!containsLocalPlayer(bankArea)) {
				//walk to the bank.
				getWalking().walk(bankTile);
			//we are in the bank
			} else {
				//so, bank
				handleBanking();
			}
		//we're in the bank area, but don't need to bank
		} else if (containsLocalPlayer(bankArea)) {
			getWalking().walk(outerAltarArea.getCenter());
			
		}
		
		
		Misc.printDev("Current time: "+Misc.getTimeStamp());

		return Misc.smallSleep();
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
			}
			if (getBank().getWithdrawMode() == BankMode.NOTE) {
				getBank().setWithdrawMode(BankMode.ITEM);
			}
			getBank().depositAllItems();
			getBank().withdraw("Pure essence", Config.ESSENCE_TO_WITHDRAW);
			getBank().close();
		}
	}
	
	/**
	 * Determines if a given area contains our local player
	 * @param area	the area to check if local player is in
	 * @return	true if localplayer is in area
	 */
	public boolean containsLocalPlayer(Area area) {
		return getPlayers().all(p -> p != null && area.contains(p) && !p.equals(getLocalPlayer())).size() > 0;
	}

	@Override
	public void onExit() {

	}

	public void onPaint(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", 1, 11));
		g.drawString("Time Running: " + t.formatTime(), 25, 50);
	}
}