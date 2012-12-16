package jenkem.client.service;

import java.util.List;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IrcServiceAsync {
    void connect(final String network, final int port, final String channel, final String nick, final AsyncCallback<String> ac);
    void disconnect(final AsyncCallback<String> ac);
    void isBotConnected(final AsyncCallback<Boolean> ac);
    void getLog(final AsyncCallback<String> ac);
    void sendMessage(final List<String> message, final AsyncCallback<String> ac);
}
