package jenkem.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IrcServiceAsync {
    @Deprecated
    void sendMessage(String network, String channel, String message, AsyncCallback<String> callback);
}
