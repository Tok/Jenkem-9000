package jenkem

import org.junit.runner.RunWith
import com.vaadin.server.VaadinRequest
import com.vaadin.server.VaadinService
import com.vaadin.server.VaadinSession
import com.vaadin.ui.UI
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class JenkemInitSuite extends AbstractTester {
  test("Init") {
    val ji = new JenkemInit
    assert(ji.isInstanceOf[JenkemInit])
    assert(ji.isInstanceOf[UI])
    val vaadinService = VaadinService.getCurrent
    val vaadinSession = new VaadinSession(vaadinService)
    ji.setSession(vaadinSession)
    ji.attach
    val mockRequest = mock[VaadinRequest]
    val e = intercept[NullPointerException] { ji.init(mockRequest) }
    assert(e.isInstanceOf[NullPointerException])
  }
}
