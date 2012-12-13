package jenkem.client.service;

import java.util.List;
import jenkem.shared.data.JenkemImage;
import jenkem.shared.data.ImageInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous interface for the service to store and read converted images.
 */
public interface JenkemServiceAsync {
    void saveJenkemImage(
            final JenkemImage jenkemImage,
            final AsyncCallback<Void> callback);
    void getAllImageInfo(AsyncCallback<List<ImageInfo>> callback);
}
