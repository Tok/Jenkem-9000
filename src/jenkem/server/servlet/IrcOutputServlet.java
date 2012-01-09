package jenkem.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jenkem.server.JenkemServiceImpl;
import jenkem.shared.data.JenkemImage;

import com.google.appengine.api.datastore.Text;

public class IrcOutputServlet extends HttpServlet {
	private static final long serialVersionUID = -7032670557877867620L;
	private final JenkemServiceImpl jenkemService = new JenkemServiceImpl();
	
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain");
		final Long ts = Long.valueOf(request.getParameter("ts"));
		final JenkemImage image = jenkemService.getImageByTimesStamp(ts);
		if (image != null && image.getIrc() != null) {
			for (Text text : image.getIrc()) {
				String line = text.getValue();
				response.getWriter().println(line);
			}
		}
	}

}