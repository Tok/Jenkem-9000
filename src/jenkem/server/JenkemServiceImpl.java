package jenkem.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import jenkem.client.service.JenkemService;
import jenkem.shared.data.JenkemImage;
import jenkem.shared.data.JenkemImageInfo;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class JenkemServiceImpl extends RemoteServiceServlet implements
		JenkemService {
	private static final Logger LOG = Logger.getLogger(JenkemServiceImpl.class.getName());
	private static final PersistenceManagerFactory PMF = JDOHelper.getPersistenceManagerFactory("transactions-optional");

	@Override
	public String saveJenkemImage(final JenkemImageInfo jenkemImageInfo, final JenkemImage jenkemImage) {
		final PersistenceManager pm = PMF.getPersistenceManager();
		try {
			pm.makePersistent(jenkemImageInfo);
			pm.makePersistent(jenkemImage);
			LOG.log(Level.INFO, "Image stored!");
		} finally {
			pm.close();
		}
		return String.valueOf(jenkemImageInfo.getCreateDate().getTime());
	}
	
	public JenkemImage getImageByName(final String name) {
		JenkemImage result = null;		
		if (name != null) {
			final PersistenceManager pm = PMF.getPersistenceManager();
			try {
				Query query = pm.newQuery(JenkemImage.class);
				query.setFilter("name == n");
				query.setUnique(true);
				query.declareParameters("String n");
				result = (JenkemImage) query.execute(name);
			} finally {
				pm.close();
			}
		}
		return result;
	}
	
	@Override
	public ArrayList<JenkemImageInfo> getAllImageInfo() {
		final PersistenceManager pm = PMF.getPersistenceManager();
		ArrayList<JenkemImageInfo> result = new ArrayList<JenkemImageInfo>();
		try {
			Query query = pm.newQuery(JenkemImageInfo.class);
			query.setRange(0, 200);
			@SuppressWarnings("unchecked")
			List<JenkemImageInfo> tmp = (List<JenkemImageInfo>) query.execute();
			result.addAll(tmp);
		} finally {
			pm.close();
		}
		return result;
	}
	
//	@Override
//	public void startBot() throws NickAlreadyInUseException, IOException, IrcException {
//		JenkemBot bot = new JenkemBot();
//		bot.setAutoNickChange(true);
//		bot.setVerbose(true);
//		bot.connect("irc.servercentral.net");
//		bot.joinChannel("#ASCII_test");
//	}

}
