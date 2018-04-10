package lavaRunner;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;

import tools.Misc;

/*
 * @author Vlad https://dreambot.org/forums/index.php/user/20-vlad/
 */

@ScriptManifest(name = "Lava Runner", author = "A q p", description = "[DEV] Lava Runner", version = 1, category = Category.RUNECRAFTING)
public class Script extends AbstractScript {
	
	//Variables
	private Area bankArea = new Area(3380, 3267, 3384, 3273);
	private Area altarArea = new Area();
	private Timer t = new Timer();

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
			//then walk out of runecrafting area
			
			//if we are not in the bank,
			if (!containsLocalPlayer(bankArea)) {
				//walk to the bank.
				getWalking().walk(bankArea.getCenter());
			//we are in the bank
			} else {
				//so, bank
				handleBanking();
			}
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