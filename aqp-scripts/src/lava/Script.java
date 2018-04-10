package lava;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import org.dreambot.api.methods.Calculations;
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
	
	private Timer t = new Timer();

	@Override
	public void onStart() {
		Misc.printDev("Started Lava Runner at "+Misc.getTimeStamp());
	}

	@Override
	public int onLoop() {

		if (!getClient().isLoggedIn()) {
			return 600;
		}
		
		Misc.printDev("Current time: "+Misc.getTimeStamp());

		return Calculations.random(300, 600);
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