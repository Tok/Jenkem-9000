package jenkem.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.Extent;
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
	public String saveJenkemImage(final JenkemImage jenkemImage) {
		final PersistenceManager pm = PMF.getPersistenceManager();
		try {
			pm.makePersistent(jenkemImage);
			LOG.log(Level.INFO, "Image stored!");
		} finally {
			pm.close();
		}
		return String.valueOf(jenkemImage.getCreateDate().getTime());
	}
		
	@Override
	public List<String> getImageList() {
		// TODO Auto-generated method stub
		return null;
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

	public JenkemImage getImageByTimesStamp(final Long ts) {
		JenkemImage result = null;		
		if (ts != null) {
			final PersistenceManager pm = PMF.getPersistenceManager();
			try {
				Query query = pm.newQuery(JenkemImage.class);
				query.setFilter("createStamp == ts");
				query.setUnique(true);
				query.declareParameters("Long ts");
				result = (JenkemImage) query.execute(ts.longValue());
			} finally {
				pm.close();
			}
		}
		return result;
	}

	/**
	 * Deletes all images that are older than 2000000ms and not flagged as persitent.
	 * @return number of deleted images
	 */
	public int doCleanUp() {
		final Date now = new Date();
		final PersistenceManager pm = PMF.getPersistenceManager();
		final Extent<JenkemImage> extent = pm.getExtent(JenkemImage.class, false);
		int count = 0;
		for (JenkemImage image : extent) {
			if (!image.getIsPersistent()) {
				final long age = now.getTime() - image.getCreateStamp();
				if (age > 200000) { //2000000ms = 33,3 minutes
					pm.deletePersistent(image);
				}
			}
			count++;
		}
		return count;
	}

	public List<JenkemImage> getAllPersitentImages() {
		final PersistenceManager pm = PMF.getPersistenceManager();
		final ArrayList<JenkemImage> result = new ArrayList<JenkemImage>();
		final Extent<JenkemImage> extent = pm.getExtent(JenkemImage.class, false);
		for (JenkemImage image : extent) {
			if (image.getIsPersistent()) {
				result.add(image);
			}
		}
		extent.closeAll();
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
