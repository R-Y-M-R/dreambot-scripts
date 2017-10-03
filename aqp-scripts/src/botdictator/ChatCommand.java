package botdictator;

public enum ChatCommand {

	REPEAT("Echo"),
	STATUS("Status"),
	TIME("Time"),
	FOLLOW("Follow"),
	
	;
	
	ChatCommand(String command) {
		this.setCommand(command);
	}
	
	private String command;
	
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
	@Override
	public String toString() {
		return command.toLowerCase();
	}
	

	
	
	
}
