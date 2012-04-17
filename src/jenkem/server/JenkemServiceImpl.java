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
 * The server side implementation of the RPC service to store and read converted iamges.
 */
@SuppressWarnings("serial")
public class JenkemServiceImpl extends RemoteServiceServlet implements JenkemService {
    private static final long QUERY_RANGE = 200;
    private static final Logger LOG = Logger.getLogger(JenkemServiceImpl.class.getName());
    private static final PersistenceManagerFactory PMF = JDOHelper.getPersistenceManagerFactory("transactions-optional");

    /**
     * Saves a converted JenkemImage.
     */
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

    /**
     * Returns the HTML of the stored image corresponding to the provided name.
     * @param name
     * @return jenkemImageHtml
     */
    public final JenkemImageHtml getImageHtmlByName(final String name) {
        JenkemImageHtml jenkemImageHtml = null;
        if (name != null) {
            final PersistenceManager pm = PMF.getPersistenceManager();
            try {
                final Query query = pm.newQuery(JenkemImageHtml.class);
                query.setFilter("name == n");
                query.setUnique(true);
                query.declareParameters("String n");
                jenkemImageHtml = (JenkemImageHtml) query.execute(name);
            } finally {
                pm.close();
            }
        }
        return jenkemImageHtml;
    }

    /**
     * Returns the CSS of the stored image corresponding to the provided name.
     * @param name
     * @return jenkemImageCss
     */
    public final JenkemImageCss getImageCssByName(final String name) {
        JenkemImageCss jenkemImageCss = null;
        if (name != null) {
            final PersistenceManager pm = PMF.getPersistenceManager();
            try {
                final Query query = pm.newQuery(JenkemImageCss.class);
                query.setFilter("name == n");
                query.setUnique(true);
                query.declareParameters("String n");
                jenkemImageCss = (JenkemImageCss) query.execute(name);
            } finally {
                pm.close();
            }
        }
        return jenkemImageCss;
    }

    /**
     * Returns the IRC representation of the stored image corresponding to the provided name.
     * @param name
     * @return jenkemImageIrc
     */
    public final JenkemImageIrc getImageIrcByName(final String name) {
        JenkemImageIrc jenkemImageIrc = null;
        if (name != null) {
            final PersistenceManager pm = PMF.getPersistenceManager();
            try {
                final Query query = pm.newQuery(JenkemImageIrc.class);
                query.setFilter("name == n");
                query.setUnique(true);
                query.declareParameters("String n");
                jenkemImageIrc = (JenkemImageIrc) query.execute(name);
            } finally {
                pm.close();
            }
        }
        return jenkemImageIrc;
    }

    /**
     * Returns an ArrayList with the information of all images in range.
     * @return infoList
     */
    @Override
    public final ArrayList<JenkemImageInfo> getAllImageInfo() {
        final PersistenceManager pm = PMF.getPersistenceManager();
        final ArrayList<JenkemImageInfo> infoList = new ArrayList<JenkemImageInfo>();
        try {
            final Query query = pm.newQuery(JenkemImageInfo.class);
            query.setRange(0, QUERY_RANGE);
            query.setOrdering("createDate desc");
            @SuppressWarnings("unchecked")
            final List<JenkemImageInfo> tmp = (List<JenkemImageInfo>) query.execute();
            infoList.addAll(tmp);
        } finally {
            pm.close();
        }
        return infoList;
    }

}
