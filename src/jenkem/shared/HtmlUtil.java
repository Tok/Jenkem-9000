package jenkem.shared;

import jenkem.shared.color.ColorUtil;

public class HtmlUtil extends AbstractWebUtil {
	private final String DOCTYPE = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"" 
		+ sep + "    \"http://www.w3.org/TR/html4/strict.dtd\">";
	private final String META = "<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
	
	public static final String MODE_PLAIN = "plain";
	public static final String MODE_FULL_HD = "full-hd";
	public static final String MODE_HYBRID = "hybrid";
	public static final String MODE_PWNTARI = "pwntari";
	public static final String MODE_SUPER_HYBRID = "super-hybrid";
	
	public String generateEmpty() {
		StringBuilder html = new StringBuilder();
		html.append(DOCTYPE);
		html.append(sep);
		html.append("<html>");
		html.append(sep);
		html.append("<head>");
		html.append(sep);
		html.append(META);
		html.append(sep);
		html.append("<title>");
		html.append("</title>");
		html.append(sep);
		html.append("</head>");
		html.append(sep);
		html.append("<body>");
		html.append("</body>");
		html.append(sep);
		html.append("</html>");
		return html.toString();
	}

	public String[] generateHtml(String[] ircOutput, String name, String mode) {
		StringBuilder html = new StringBuilder();
		StringBuilder css = new StringBuilder();
		html.append(DOCTYPE);
		html.append(sep);
		html.append("<html>");
		html.append(sep);
		html.append("<head>");
		html.append(sep);
		html.append(META);
		html.append(sep);
		html.append("<title>");
		html.append(name);
		html.append("</title>");
		html.append(sep);

		html.append("<link href=\"/jenkem/output?name=");
		html.append(name);
		html.append("&amp;type=css");
		html.append("\" rel=\"stylesheet\" type=\"text/css; charset=utf-8\">");
		
		
		html.append(sep);

		html.append("</head>");
		html.append(sep);
		html.append("<body>");
		html.append(sep);
		html.append("<div>");
		html.append(sep);

		css.append("form { margin: 0 auto; padding: 0; }");
		css.append(sep);
		css.append("html { background-color: #ffffff }");
		css.append(sep);
		css.append("body { font-family: monospace; font-size: 1em; font-weight: bold; margin: 0; background-color: black; }");
		css.append(sep);
		css.append("div { float: left; width: auto; clear: both; }");
		css.append(sep);
		css.append("span { float: left; width: auto; }");
		css.append(sep);

		int line = 0;

		if (mode.equals(MODE_PLAIN)) {
			while (ircOutput != null && line < ircOutput.length
					&& ircOutput[line] != null && ircOutput[line].length() > 0) {
				html.append("<div>");
				html.append("<span id=\"id_");
				html.append(line);
				html.append("\">");
				html.append(escape(ircOutput[line]));
				html.append("</span>");
				html.append("</div>");
				html.append(sep);
				css.append("#id_");
				css.append(line);
				css.append(" { color: #000000; background-color: #ffffff; }");
				css.append(sep);
				line++;
			}
		} else {
		while (ircOutput != null && line < ircOutput.length
				&& ircOutput[line] != null && ircOutput[line].length() > 0) {
			html.append("<div>");
			
			String[] splitSections = ircOutput[line].split(ColorUtil.CC);
			String[] sections = new String[splitSections.length];
			int i = 0;
			for (String s : splitSections) {
				if (!s.equals("")) {
					sections[i] = s;
					i++;
				}
			}
			
			int section = 0;
			for (String token : sections) {
				if (token == null || token.equals("")) {
					break;
				}

				if (token.equals(ColorUtil.BC)) {
					break; //throw bold code away
				}
				
				String[] splitCut = token.split(",");
				String[] cut = new String[splitCut.length];
				int ii = 0;
				for (String s : splitCut) {
					if (!s.equals("")) {
						cut[ii] = s;
						ii++;
					}
				}

				
//				String fg = cut.nextToken();
//				String bgAndChars = cut.nextToken();


				String fg = cut[0];
				String bgAndChars = cut[1];

				String bg = "";
				String chars = "";

				try { // FIXME ugly trial and error approach
					Integer.parseInt(bgAndChars.substring(0, 2));
					// if parseInt works we know that the bg color takes two
					// characters
					bg = bgAndChars.substring(0, 2);
					chars = bgAndChars.substring(2, bgAndChars.length());
				} catch (Exception e) {
					bg = bgAndChars.substring(0, 1);
					chars = bgAndChars.substring(1, bgAndChars.length());
				}

				html.append("<span id=\"id_");
				html.append(line);
				html.append("_");
				html.append(section);
				html.append("\">");

				chars = escape(chars);

				html.append(chars);
				html.append("</span>");
				css.append("#id_");
				css.append(line);
				css.append("_");
				css.append(section);
				css.append(" { color: ");
				try {
					ColorUtil colorUtil = new ColorUtil();
					css.append(colorUtil.ircToCss(fg));
					css.append("; background-color: ");
					css.append(colorUtil.ircToCss(bg));
				} catch (ColorUnknownException cue) {
					//ignore. this must never happen.
				}
				css.append("; }");
				css.append(sep);
				section++;
			}
			html.append("</div>");
			html.append(sep);
			line++;
		}
		}
		html.append("</div>");
		html.append(sep);
//		if (type.equals("history")) {
			html.append("<div>");
			html.append("<a href=\"http://validator.w3.org/check?uri=referer\">");
			html.append("<img src=\"http://www.w3.org/Icons/valid-html401\" alt=\"Valid HTML 4.01 Strict\" style=\"border: 0; width: 88px; height: 31px\">");
			html.append("</a>");
			if (!mode.equals(MODE_PLAIN)) {
				html.append("<a href=\"http://jigsaw.w3.org/css-validator/check/referer\">");
				html.append("<img src=\"http://jigsaw.w3.org/css-validator/images/vcss\" alt=\"CSS is valid!\" style=\"border: 0; width: 88px; height: 31px\">");
				html.append("</a>");
			}
			html.append("</div>");
			html.append(sep);
//		}
		html.append("</body>");
		html.append(sep);
		html.append("</html>");

		String[] ret = new String[2];
		ret[0] = html.toString();
		ret[1] = css.toString();
		return ret;
	}

}