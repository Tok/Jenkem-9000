package jenkem.server.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Takes an URL parameter to an image and proxifies it back, so the client can locally call this servlet.
 * in order to circumvent the same origin policies.
 */
public class ImageServlet extends HttpServlet {
	private static final long serialVersionUID = 7865055524973352098L;
    private static final int MAX_BYTES = 1024;

	public void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("image/jpeg");

		final String urlString = request.getParameter("url");
		final URL url = new URL(urlString);
		final InputStream in = url.openStream();
		final OutputStream out = response.getOutputStream();

		final byte[] buf = new byte[MAX_BYTES];
        int count = 0;
        while ((count = in.read(buf)) >= 0) {
            out.write(buf, 0, count);
        }
        in.close();
        out.close();
	}

}