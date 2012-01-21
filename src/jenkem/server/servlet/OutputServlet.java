package jenkem.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jenkem.server.JenkemServiceImpl;
import jenkem.shared.HtmlUtil;
import jenkem.shared.data.JenkemImageHtml;

/**
 * Servlet to retrieve and return stored HTML
 */
public class OutputServlet extends HttpServlet {
    private static final long serialVersionUID = -6626624514779473378L;
    private final HtmlUtil htmlUtil = new HtmlUtil();
    private final JenkemServiceImpl jenkemService = new JenkemServiceImpl();

    @Override
    public final void doGet(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException,
            IOException {
        final String name = request.getParameter("name");
        final JenkemImageHtml imageHtml = jenkemService
                .getImageHtmlByName(name);
        if (imageHtml != null && imageHtml.getHtml() != null) {
            response.getWriter().println(imageHtml.getHtml());
        } else {
            response.getWriter().println(htmlUtil.generateEmpty());
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html");
    }

}
