package jenkem.client.service;

import java.util.List;
import jenkem.shared.data.JenkemImage;
import jenkem.shared.data.JenkemImageInfo;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Interface for the service to store and read converted images.
 */
@RemoteServiceRelativePath("jenkem")
public interface JenkemService extends RemoteService {
    List<JenkemImageInfo> getAllImageInfo();
    void saveJenkemImage(JenkemImage jenkemImage);
}
