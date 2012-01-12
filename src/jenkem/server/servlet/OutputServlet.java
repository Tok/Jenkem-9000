package jenkem.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jenkem.server.JenkemServiceImpl;
import jenkem.shared.HtmlUtil;
import jenkem.shared.data.JenkemImageCss;
import jenkem.shared.data.JenkemImageHtml;

public class OutputServlet extends HttpServlet {
	private static final long serialVersionUID = -6626624514779473378L;

	private final HtmlUtil htmlUtil = new HtmlUtil();
	private final JenkemServiceImpl jenkemService = new JenkemServiceImpl();
	
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");

		final String name = request.getParameter("name");
		final String type = request.getParameter("type");
		
		if (type != null && type.equals("html")) {
			final JenkemImageHtml imageHtml = jenkemService.getImageHtmlByName(name);
			response.setContentType("text/html");
			if (imageHtml != null && imageHtml.getHtml() != null) {
				response.getWriter().println(imageHtml.getHtml());
			} else {
				response.getWriter().println(htmlUtil.generateEmpty());
			}
		} else if (type != null && type.equals("css")) {
			final JenkemImageCss imageCss = jenkemService.getImageCssByName(name);
			response.setContentType("text/css");
			if (imageCss != null && imageCss.getCss() != null) {
				response.getWriter().println(imageCss.getCss());
			}
		} else {
			response.setContentType("text/html");
			response.getWriter().println(htmlUtil.generateEmpty());
		}
	}

}