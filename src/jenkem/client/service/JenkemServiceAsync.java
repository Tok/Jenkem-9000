package jenkem.client.service;

import java.util.List;

import jenkem.shared.data.JenkemImage;

import com.google.gwt.user.client.rpc.AsyncCallback;



public interface JenkemServiceAsync {
	void getImageList(final AsyncCallback<List<String>> callback);
	void saveJenkemImage(final JenkemImage image, final AsyncCallback<String> callback);
}
