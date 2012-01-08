package jenkem.server;


public abstract class AbstractWebUtil {
	String sep = System.getProperty("line.separator");
	String escape(String in) {
		String out = in.replaceAll("&", "&amp;");
		out = out.replaceAll("<", "&lt;");
		out = out.replaceAll(">", "&gt;");
		out = out.replaceAll(" ", "&nbsp;");
		return out;
	}
}