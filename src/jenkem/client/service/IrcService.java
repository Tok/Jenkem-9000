package jenkem.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Interface for the service to store and read converted images.
 */
@RemoteServiceRelativePath("irc")
public interface IrcService extends RemoteService {
    @Deprecated
    String sendMessage(final String network, final String channel, final String message);
}
