package open_source;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;

import tools.Misc;

import java.awt.*;

/*
 * @author Vlad https://dreambot.org/forums/index.php/user/20-vlad/
 */

@ScriptManifest(name = "Skeleton", author = "A q p", description = "[DEV] Skeleton", version = 1, category = Category.MISC)
public class Skeleton extends AbstractScript {

	private Timer t = new Timer();

	@Override
	public void onStart() {
		Misc.printDev("Hello. How is everyone doing?");
	}

	@Override
	public int onLoop() {

		if (!getClient().isLoggedIn()) {
			return 600;
		}
		
		Misc.printDev("Current time: "+System.currentTimeMillis());

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