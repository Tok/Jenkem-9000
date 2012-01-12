package jenkem.client.service;

import java.util.List;

import jenkem.shared.data.JenkemImageCss;
import jenkem.shared.data.JenkemImageHtml;
import jenkem.shared.data.JenkemImageInfo;
import jenkem.shared.data.JenkemImageIrc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("jenkem")
public interface JenkemService extends RemoteService {
	public List<JenkemImageInfo> getAllImageInfo();
	String saveJenkemImage(JenkemImageInfo jenkemImageInfo,
			JenkemImageHtml jenkemImageHtml, JenkemImageCss image,
			JenkemImageIrc jenkemImageIrc);
}
