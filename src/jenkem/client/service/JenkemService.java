package jenkem.client.service;

import java.util.List;

import jenkem.shared.data.JenkemImage;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("jenkem")
public interface JenkemService extends RemoteService {
	public String saveJenkemImage(final JenkemImage image);
	public List<String> getImageList();
}
