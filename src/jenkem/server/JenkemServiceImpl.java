package jenkem.server;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import jenkem.client.service.JenkemService;
import jenkem.shared.data.JenkemImage;

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
	public String saveJenkemImage(JenkemImage jenkemImage) {
		PersistenceManager pm = PMF.getPersistenceManager();
		try {
			pm.makePersistent(jenkemImage);
			LOG.log(Level.INFO, "Image stored!");
		} finally {
			pm.close();
		}
		return jenkemImage.getName();
	}
		
	@Override
	public List<String> getImageList() {
		// TODO Auto-generated method stub
		return null;
	}

	public JenkemImage getImageByName(String name) {
		JenkemImage result = null;		
		if (name != null) {
			PersistenceManager pm = PMF.getPersistenceManager();
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


//	@Override
//	public void startBot() throws NickAlreadyInUseException, IOException, IrcException {
//		JenkemBot bot = new JenkemBot();
//		bot.setAutoNickChange(true);
//		bot.setVerbose(true);
//		bot.connect("irc.servercentral.net");
//		bot.joinChannel("#ASCII_test");
//	}


	
}
