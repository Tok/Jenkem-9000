package jenkem.shared;

import java.io.Serializable;

public class BotStatus implements Serializable {
    private static final long serialVersionUID = 1701948349685891470L;
    public enum ConnectionStatus { Connected, Disconnected }
    public enum SendStatus { Sending, NotSending }

    private ConnectionStatus connectionStatus;
    private SendStatus sendStatus;
    private String network;
    private String channel;
    private String name;

    public BotStatus() { }
    public BotStatus(final ConnectionStatus connectionStatus, final SendStatus sendStatus,
            final String network, final String channel, final String name) {
        this.connectionStatus = connectionStatus;
        this.sendStatus = sendStatus;
        this.network = network;
        this.channel = channel;
        this.name = name;
    }

    public final ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public final boolean isConnected() {
        return connectionStatus == ConnectionStatus.Connected;
    }

    public final SendStatus getSendStatus() {
        return sendStatus;
    }

    public final boolean isSending() {
        return sendStatus == SendStatus.Sending;
    }

    public final String getNetwork() {
        return network;
    }

    public final String getChannel() {
        return channel;
    }

    public final String getName() {
        return name;
    }
}
