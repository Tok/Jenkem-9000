package jenkem.servlet

import org.junit.runner.RunWith
import jenkem.AbstractTester
import org.scalatest.junit.JUnitRunner
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RunWith(classOf[JUnitRunner])
class ServletSuite extends AbstractTester {
  test("HTML Output Servlet") {
    val mockRequest = mock[HttpServletRequest]
    val mockResponse = mock[HttpServletResponse]
    val hos = new HtmlOutputServlet
    intercept[NullPointerException] { hos.doGet(mockRequest, mockResponse) }
  }

  test("CSS Output Servlet") {
    val mockRequest = mock[HttpServletRequest]
    val mockResponse = mock[HttpServletResponse]
    val cos = new CssOutputServlet
    intercept[NullPointerException] { cos.doGet(mockRequest, mockResponse) }
  }

  test("IRC Output Servlet") {
    val mockRequest = mock[HttpServletRequest]
    val mockResponse = mock[HttpServletResponse]
    val ios = new IrcOutputServlet
    intercept[NullPointerException] { ios.doGet(mockRequest, mockResponse) }
  }
}
