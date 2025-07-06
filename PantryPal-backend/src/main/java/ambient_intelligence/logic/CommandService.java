package ambient_intelligence.logic;

import java.util.List;

import ambient_intelligence.boundary.CommandBoundary;

public interface CommandService {

	public List<Object> invokeCommand(CommandBoundary command);
	public List<CommandBoundary> getAllCommandsHistory(int size, int page, String systemId, String email);
	public void deleteAllCommands(String systemId, String email);
}
