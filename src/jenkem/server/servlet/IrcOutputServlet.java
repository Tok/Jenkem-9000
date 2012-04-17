package jenkem.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jenkem.server.JenkemServiceImpl;
import jenkem.shared.data.JenkemImageIrc;

import com.google.appengine.api.datastore.Text;

/**
 * Servlet to retrieve and return stored IRC output.
 */
public class IrcOutputServlet extends HttpServlet {
    private static final long serialVersionUID = -7032670557877867620L;
    private final JenkemServiceImpl jenkemService = new JenkemServiceImpl();

    @Override
    public final void doGet(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException,
            IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/plain");
        final String name = request.getParameter("name");
        final JenkemImageIrc imageIrc = jenkemService.getImageIrcByName(name);
        if (imageIrc != null && imageIrc.getIrc() != null) {
            for (final Text text : imageIrc.getIrc()) {
                final String line = text.getValue();
                response.getWriter().println(line);
            }
        }
    }

}
