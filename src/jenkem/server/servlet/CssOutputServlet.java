package jenkem.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jenkem.server.JenkemServiceImpl;
import jenkem.shared.data.JenkemImageCss;

public class CssOutputServlet extends HttpServlet {
	private static final long serialVersionUID = 7683169629001671486L;
	
	private final JenkemServiceImpl jenkemService = new JenkemServiceImpl();
	
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		final String name = request.getParameter("name");
		final JenkemImageCss imageCss = jenkemService.getImageCssByName(name);
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/css");
		response.getWriter().println(imageCss.getCss());
	}
}