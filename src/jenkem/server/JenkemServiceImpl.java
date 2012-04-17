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
import jenkem.shared.data.JenkemImageCss;
import jenkem.shared.data.JenkemImageHtml;
import jenkem.shared.data.JenkemImageInfo;
import jenkem.shared.data.JenkemImageIrc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class JenkemServiceImpl extends RemoteServiceServlet implements
        JenkemService {
    private static final long QUERY_RANGE = 200;
    private static final Logger LOG = Logger.getLogger(JenkemServiceImpl.class.getName());
    private static final PersistenceManagerFactory PMF
        = JDOHelper.getPersistenceManagerFactory("transactions-optional");

    @Override
    public final String saveJenkemImage(final JenkemImageInfo jenkemImageInfo,
            final JenkemImageHtml jenkemImageHtml,
            final JenkemImageCss jenkemImageCss,
            final JenkemImageIrc jenkemImageIrc) {
        final PersistenceManager pm = PMF.getPersistenceManager();
        try {
            pm.makePersistent(jenkemImageInfo);
            pm.makePersistent(jenkemImageHtml);
            pm.makePersistent(jenkemImageCss);
            pm.makePersistent(jenkemImageIrc);
            LOG.log(Level.INFO, "Image stored!");
        } finally {
            pm.close();
        }
        return String.valueOf(jenkemImageInfo.getCreateDate().getTime());
    }

    public final JenkemImageHtml getImageHtmlByName(final String name) {
        JenkemImageHtml result = null;
        if (name != null) {
            final PersistenceManager pm = PMF.getPersistenceManager();
            try {
                final Query query = pm.newQuery(JenkemImageHtml.class);
                query.setFilter("name == n");
                query.setUnique(true);
                query.declareParameters("String n");
                result = (JenkemImageHtml) query.execute(name);
            } finally {
                pm.close();
            }
        }
        return result;
    }

    public final JenkemImageCss getImageCssByName(final String name) {
        JenkemImageCss result = null;
        if (name != null) {
            final PersistenceManager pm = PMF.getPersistenceManager();
            try {
                final Query query = pm.newQuery(JenkemImageCss.class);
                query.setFilter("name == n");
                query.setUnique(true);
                query.declareParameters("String n");
                result = (JenkemImageCss) query.execute(name);
            } finally {
                pm.close();
            }
        }
        return result;
    }

    public final JenkemImageIrc getImageIrcByName(final String name) {
        JenkemImageIrc result = null;
        if (name != null) {
            final PersistenceManager pm = PMF.getPersistenceManager();
            try {
                final Query query = pm.newQuery(JenkemImageIrc.class);
                query.setFilter("name == n");
                query.setUnique(true);
                query.declareParameters("String n");
                result = (JenkemImageIrc) query.execute(name);
            } finally {
                pm.close();
            }
        }
        return result;
    }

    @Override
    public final ArrayList<JenkemImageInfo> getAllImageInfo() {
        final PersistenceManager pm = PMF.getPersistenceManager();
        final ArrayList<JenkemImageInfo> result = new ArrayList<JenkemImageInfo>();
        try {
            final Query query = pm.newQuery(JenkemImageInfo.class);
            query.setRange(0, QUERY_RANGE);
            query.setOrdering("createDate desc");
            @SuppressWarnings("unchecked")
            final List<JenkemImageInfo> tmp = (List<JenkemImageInfo>) query
                    .execute();
            result.addAll(tmp);
        } finally {
            pm.close();
        }
        return result;
    }

    // @Override
    // public void startBot() throws NickAlreadyInUseException, IOException,
    // IrcException {
    // JenkemBot bot = new JenkemBot();
    // bot.setAutoNickChange(true);
    // bot.setVerbose(true);
    // bot.connect("irc.servercentral.net");
    // bot.joinChannel("#ASCII_test");
    // }

}
