package botdictator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.input.Keyboard;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.MessageListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.widgets.message.Message;

import tools.Misc;

/*
 * @author A q p https://dreambot.org/forums/index.php/user/106843-a-q-p/
 * 
 */

@ScriptManifest(name = "Bot Dictator", author = "A q p", description = "[DEV] Bot Dictator", version = 1, category = Category.MISC)
public class BotDictator extends AbstractScript implements MessageListener {

	private Timer t = new Timer();
	private ArrayList<String> dictators = new ArrayList<String>();

	@Override
	public void onStart() {
		Misc.printDev("Script started on "+Misc.getTimeStamp());
	}

	@Override
	public int onLoop() {

		if (!getClient().isLoggedIn()) {
			return 600;
		}
		
		return Calculations.random(300, 600);
	}

	@Override
	public void onExit() {

	}
	
	public void handleMessage(Player dictator, String message, Keyboard kb) {
		if (dictator == null) {
			Misc.printDev("dictator has returned null in handleMessage");
			return;
		}
		if (!dictators.contains(dictator.getName().toLowerCase())) {
			Misc.printDev(dictator.getName()+" is not a whitelisted dictator.");
			return;
		}
		message = message.toLowerCase();
		
		for (ChatCommand cc : ChatCommand.values()) {
			if (message.startsWith("!") && message.contains(cc.toString())) {
				chatCommands(dictator, cc, message, kb);
			}
		}
		 
		
	}
	
	private static void chatCommands(Player dictator, ChatCommand chatCommand, String message, Keyboard kb) {
		switch(chatCommand) {
		case REPEAT:
			try {
				String msgStripped = message.substring(chatCommand.toString().length()+2); //+2 accounts for 1 exclamation mark, and 1 space.
				Misc.printDev("Msg stripped: "+msgStripped);
				kb.type(msgStripped);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case STATUS:
			kb.type("Ready.");
			break;
		case TIME:
			kb.type("It is currently: "+Misc.getTimeStamp());
			break;
		case FOLLOW:
			if (dictator.hasAction("Follow")) {
				dictator.interactForceRight("Follow");
				Misc.printDev("Now following "+dictator.getName());
			}
		}
	}

	public void onPaint(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", 1, 11));
		g.drawString("Time Running: " + t.formatTime(), 25, 50);
	}

	@Override
	public void onPlayerMessage(Message arg0) {
		Misc.printDev("Handling playerMsg \""+arg0.getMessage()+"\"");
		handleMessage(Misc.getPlayerByName(arg0.getUsername(), this.getPlayers().all()), arg0.getMessage(), this.getKeyboard());
	}
	
	@Override
	public void onGameMessage(Message arg0) {
	}
	
	@Override
	public void onPrivateInMessage(Message arg0) {
		Misc.printDev("Handling privateMessage \""+arg0.getMessage()+"\"");
		handleMessage(Misc.getPlayerByName(arg0.getUsername(), this.getPlayers().all()), arg0.getMessage(), this.getKeyboard());
	}

	@Override
	public void onPrivateOutMessage(Message arg0) {
	}

	@Override
	public void onTradeMessage(Message arg0) {
	}
}