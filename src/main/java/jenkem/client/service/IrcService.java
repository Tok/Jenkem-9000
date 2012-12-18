package jenkem.client.service;

import java.util.List;
import jenkem.shared.BotStatus;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Interface for the service to store and read converted images.
 */
@RemoteServiceRelativePath("ircService")
public interface IrcService extends RemoteService {
    String connect(final String network, final int port, final String channel, final String nick);
    String disconnect();
    BotStatus getBotStatus();
    String sendMessage(final List<String> message);
}
