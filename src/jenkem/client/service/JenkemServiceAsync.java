package jenkem.client.service;

import java.util.List;

import jenkem.shared.data.JenkemImageCss;
import jenkem.shared.data.JenkemImageHtml;
import jenkem.shared.data.JenkemImageInfo;
import jenkem.shared.data.JenkemImageIrc;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface JenkemServiceAsync {
    void saveJenkemImage(
            final JenkemImageInfo jenkemImageInfo,
            final JenkemImageHtml jenkemImageHtml, 
            final JenkemImageCss image,
            final JenkemImageIrc jenkemImageIrc,
            final AsyncCallback<String> callback);

    void getAllImageInfo(AsyncCallback<List<JenkemImageInfo>> callback);
}
