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
 * Takes an URL parameter to an image and proxifies it back, so the client can locally call this servlet 
 * in order to circumvent the same origin policies.
 */
public class ImageServlet extends HttpServlet {
	private static final long serialVersionUID = 7865055524973352098L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("image/jpeg");

		String urlString = request.getParameter("url");
		URL url = new URL(urlString);
        InputStream in = url.openStream();
        OutputStream out = response.getOutputStream();

        byte[] buf = new byte[1024];
        int count = 0;
        while ((count = in.read(buf)) >= 0) {
            out.write(buf, 0, count);
        }
        in.close();
        out.close();
	}

}