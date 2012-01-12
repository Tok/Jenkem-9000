package jenkem.client.service;

import java.util.List;

import jenkem.shared.data.JenkemImage;
import jenkem.shared.data.JenkemImageInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface JenkemServiceAsync {
	void saveJenkemImage(final JenkemImageInfo jenkemImageInfo, final JenkemImage image, final AsyncCallback<String> callback);
	void getAllImageInfo(AsyncCallback<List<JenkemImageInfo>> callback);
}
