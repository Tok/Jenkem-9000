package jenkem.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jenkem.server.JenkemServiceImpl;
import jenkem.shared.data.JenkemImageIrc;

import com.google.appengine.api.datastore.Text;

public class IrcOutputServlet extends HttpServlet {
	private static final long serialVersionUID = -7032670557877867620L;
	private final JenkemServiceImpl jenkemService = new JenkemServiceImpl();
	
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain");
		final String name = request.getParameter("name");
		final JenkemImageIrc imageIrc = jenkemService.getImageIrcByName(name);
		if (imageIrc != null && imageIrc.getIrc() != null) {
			for (Text text : imageIrc.getIrc()) {
				String line = text.getValue();
				response.getWriter().println(line);
			}
		}
	}

}