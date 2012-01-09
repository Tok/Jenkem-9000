package jenkem.server.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jenkem.server.JenkemServiceImpl;

import com.google.appengine.api.utils.SystemProperty;


/**
 * Deletes all non-persistent images with an age older than 30 minutes.
 */
public class CleanupServlet extends HttpServlet {
	private static final long serialVersionUID = -7323337352742211255L;
	private static final boolean PRODUCTION_MODE = SystemProperty.environment
			.value() == SystemProperty.Environment.Value.Production;
	private static final Logger LOG = Logger.getLogger(CleanupServlet.class.getName());	
	
	private JenkemServiceImpl jenkemService = new JenkemServiceImpl();
	
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Content-Type", "text/html");

		final StringBuffer report = new StringBuffer();
		
		// check if cleanup was triggered by cron job
		final String cron = request.getHeader("X-AppEngine-Cron");
		if (cron != null && cron.equals("true")) {
			doTheCleanup(report);
			report.append("cleanup done: ");
			report.append(new Date());
			report.append(System.getProperty("line.separator"));
			LOG.info(report.toString());
		} else {
			report.append("cleanup not called from cron job: ");
			report.append(new Date());
			if (PRODUCTION_MODE) {
				LOG.severe(report.toString());
				//just send a redirect instead of an error message
				response.sendRedirect("/");
				return;
			} else {
				doTheCleanup(report);
				LOG.warning(report.toString());
			}
		}
		response.getWriter().println(report.toString());
	}

	private void doTheCleanup(StringBuffer report) {
		final int counter = jenkemService.doCleanUp();
		report.append("Deleted ");
		report.append(String.valueOf(counter));
		report.append(" images.");
	}
}