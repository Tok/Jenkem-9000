package jenkem.client.service;

import java.util.List;
import jenkem.shared.data.JenkemImage;
import jenkem.shared.data.ImageInfo;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Interface for the service to store and read converted images.
 */
@RemoteServiceRelativePath("jenkem")
public interface JenkemService extends RemoteService {
    List<ImageInfo> getAllImageInfo();
    void saveJenkemImage(JenkemImage jenkemImage);
}
