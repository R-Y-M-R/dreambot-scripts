package botdictator;

public enum ChatCommand {

	REPEAT("Echo"),
	STATUS("Status"),
	TIME("Time"),
	FOLLOW("Follow"),
	INTERACT_PLAYER("plr"),
	INTERACT_NPC("npc"),
	INTERACT_OBJ("obj"),
	INTERACT_ENTITY("ent"),
	WHITELIST("list"),
	GET_POSITION("pos")
	
	;
	
	ChatCommand(String command) {
		this.setCommand(command);
	}
	
	private String command;
	
	public String getCommand() {
		return command;
	}

	private void setCommand(String command) {
		this.command = command;
	}
	
	@Override
	public String toString() {
		return command.toLowerCase();
	}
	

	
	
	
}
