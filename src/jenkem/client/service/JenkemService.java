package jenkem.client.service;

import java.util.List;

import jenkem.shared.data.JenkemImageCss;
import jenkem.shared.data.JenkemImageHtml;
import jenkem.shared.data.JenkemImageInfo;
import jenkem.shared.data.JenkemImageIrc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Interface for the service to store and read converted images.
 */
@RemoteServiceRelativePath("jenkem")
public interface JenkemService extends RemoteService {
    List<JenkemImageInfo> getAllImageInfo();
    String saveJenkemImage(JenkemImageInfo jenkemImageInfo,
            JenkemImageHtml jenkemImageHtml, JenkemImageCss image,
            JenkemImageIrc jenkemImageIrc);
}
