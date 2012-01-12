package jenkem.client.service;

import java.util.List;

import jenkem.shared.data.JenkemImage;
import jenkem.shared.data.JenkemImageInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("jenkem")
public interface JenkemService extends RemoteService {
	String saveJenkemImage(JenkemImageInfo jenkemImageInfo, JenkemImage image);
	public List<JenkemImageInfo> getAllImageInfo();
}
