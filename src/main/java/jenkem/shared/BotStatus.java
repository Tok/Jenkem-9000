package jenkem.shared;

import java.io.Serializable;

public class BotStatus implements Serializable {
    private static final long serialVersionUID = 1701948349685891470L;
    private boolean isConnected;
    private boolean isSending;
    private String network;
    private String channel;
    private String name;

    public BotStatus() { }
    public BotStatus(final boolean isConnected, final boolean isSending, final String network,
            final String channel, final String name) {
        this.isConnected = isConnected;
        this.isSending = isSending;
        this.network = network;
        this.channel = channel;
        this.name = name;
    }

    public final boolean isConnected() {
        return isConnected;
    }

    public final boolean isSending() {
        return isSending;
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
