package jenkem.client.widget;

import com.google.gwt.i18n.client.Messages;

public interface IrcSettings extends Messages {
    @DefaultMessage("irc.freenode.net")
    String network();

    @DefaultMessage("8001")
    String port();

    @DefaultMessage("#DSFARGEG")
    String channel();

    @DefaultMessage("J_")
    String nick();
}
