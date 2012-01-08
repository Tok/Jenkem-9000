package jenkem.client.service;

import java.util.List;

import jenkem.shared.data.JenkemImage;

import com.google.gwt.user.client.rpc.AsyncCallback;



public interface JenkemServiceAsync {
	void getImageList(AsyncCallback<List<String>> callback);
	void saveJenkemImage(JenkemImage image, AsyncCallback<String> callback);
}
