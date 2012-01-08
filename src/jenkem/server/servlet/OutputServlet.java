package jenkem.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jenkem.server.JenkemServiceImpl;
import jenkem.shared.HtmlUtil;
import jenkem.shared.data.JenkemImage;

public class OutputServlet extends HttpServlet {
	private static final long serialVersionUID = -6626624514779473378L;

	private HtmlUtil htmlUtil = new HtmlUtil();

	private JenkemServiceImpl jenkemService = new JenkemServiceImpl();
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String name = request.getParameter("name");
		String type = request.getParameter("type");
		
		response.setCharacterEncoding("utf-8");

		JenkemImage image = jenkemService.getImageByName(name);
		
		if (image != null && type != null && type.equals("html")) {
			response.setContentType("text/html");
			if (image != null && image.getHtml() != null) {
				response.getWriter().println(image.getHtml());
			} else {
				response.getWriter().println(htmlUtil.generateEmpty());
			}
		} else if (image != null && type != null && type.equals("css")) { 
			response.setContentType("text/css");
			if (image != null && image.getCss() != null) {
				response.getWriter().println(image.getCss());
			}
		} else {
			response.setContentType("text/html");
			response.getWriter().println(htmlUtil.generateEmpty());
		}
	}

}