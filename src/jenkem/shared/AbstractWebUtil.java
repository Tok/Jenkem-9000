package jenkem.shared;

public abstract class AbstractWebUtil {
//	String sep = System.getProperty("line.separator");
	String sep = "\n";
	
	String escape(String in) {
		String out = in.replaceAll("&", "&amp;");
		out = out.replaceAll("<", "&lt;");
		out = out.replaceAll(">", "&gt;");
		out = out.replaceAll(" ", "&nbsp;");
		return out;
	}

}