package botdictator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.MessageListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.widgets.message.Message;

import tools.Local;
import tools.Misc;

/*
 * @author A q p https://dreambot.org/forums/index.php/user/106843-a-q-p/
 * @TODO better way of loading dictators, update onStart before publish
 */

@ScriptManifest(name = "Bot Dictator", author = "A q p", description = "[DEV] Bot Dictator", version = 1, category = Category.MISC)
public class BotDictator extends AbstractScript implements MessageListener {

	private Timer t = new Timer();
	private ArrayList<String> dictators = new ArrayList<String>();

	@Override
	public void onStart() {
		Misc.printDev("Script started on "+Misc.getTimeStamp());
		dictators.add(Local.dictator); //sorry github, no more leaking usernames
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
		Misc.printDev("Script stopped "+Misc.getTimeStamp()+", bye!");
	}
	
	private static void chatCommands(Player dictator, ChatCommand chatCommand, String command, BotDictator bd) {
		try {
			Misc.printDev("Chatcommand: "+chatCommand.toString()+", full: "+command);
			String[] parts = command.toLowerCase().split(" ");
			String stripped = command.replace(parts[0], "").trim();
			String target = null, action = null;

			if(command.contains("\r") || command.contains("\n")) {
				Misc.printDev("Message contained \"\r\" or \"n\". Handling aborted.");
				return;
			}
			if (command.contains("(") && command.contains(")")) {
				target = command.substring(command.indexOf("(")+1, command.indexOf(")")).trim();
				Misc.printDev("Target = "+target);
			}	
			if (command.contains("{") && command.contains("}")) {
				action = command.substring(command.indexOf("{")+1, command.indexOf("}")).trim();
				Misc.printDev("Action = "+action);
			}
			
			switch(chatCommand) {
			case REPEAT:
				bd.getKeyboard().type(stripped);
				break;
			case STATUS:
				bd.getKeyboard().type("Ready.");
				break;
			case TIME:
				bd.getKeyboard().type("It is currently: "+Misc.getTimeStamp());
				break;
			case FOLLOW:
				if (dictator.hasAction("Follow")) {
					dictator.interactForceRight("Follow");
					Misc.printDev("Now following "+dictator.getName());
				}
				break;
			case INTERACT_ENTITY:
				if (parts.length < 3) {
					Misc.printDev("parts < 3, need more args to interact with entity");
					break;
				}
				if (target == null) {
					Misc.printDev("Target was not declared. Use \"{\" and \"}\" brackets.");
					break;
				}
				if (action == null) {
					Misc.printDev("Action was not declared. Use \"(\" and \")\" brackets.");
					break;
				}
				Misc.printDev("We're all good, prepare to interact with entity");
				
				break;
			default:
				Misc.printDev("Default case has caught: "+command);
				break;
			}
		} catch (Exception e) {
			Misc.printDev("Error occured while handling command!\n "+e.getMessage());
		}
	}
	
	public void handleMessage(Player dictator, String message, BotDictator bd) {
		if (dictator == null) {
			Misc.printDev("dictator == null, via handleMessage");
			return;
		}
		if (!dictators.contains(dictator.getName().toLowerCase())) {
			Misc.printDev(dictator.getName()+" is not a whitelisted dictator.");
			return;
		}
		message = message.toLowerCase();
		
		for (ChatCommand cc : ChatCommand.values()) {
			if (message.startsWith("!") && message.substring(1).contains(cc.toString())) {
				Misc.printDev("Message matched a command, passing to handler.");
				chatCommands(dictator, cc, message.substring(1), bd); 
				break;
			}
		}
	}

	public void onPaint(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", 1, 11));
		g.drawString("Time Running: " + t.formatTime(), 25, 50);
	}

	@Override
	public void onPlayerMessage(Message message) {
		Misc.printDev("Handling playerMsg \""+message.getMessage()+"\"");
		handleMessage(Misc.getPlayerByName(message.getUsername(), this.getPlayers().all()), message.getMessage(), this);
	}
	
	@Override
	public void onGameMessage(Message message) {
	}
	
	@Override
	public void onPrivateInMessage(Message message) {
		Misc.printDev("Handling privateMessage \""+message.getMessage()+"\"");
		handleMessage(Misc.getPlayerByName(message.getUsername(), this.getPlayers().all()), message.getMessage(), this);
	}

	@Override
	public void onPrivateOutMessage(Message message) {
	}

	@Override
	public void onTradeMessage(Message message) {
	}
}