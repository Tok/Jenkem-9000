package jenkem.servlet

import org.junit.runner.RunWith
import jenkem.AbstractTester
import org.scalatest.junit.JUnitRunner
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.easymock.EasyMock
import javax.servlet.ServletConfig
import javax.servlet.ServletContext
import java.io.PrintWriter
import java.io.OutputStream
import java.io.ByteArrayOutputStream
import java.io.StringWriter
import javax.servlet.http.HttpServlet

@RunWith(classOf[JUnitRunner])
class ServletSuite extends AbstractTester {
  val nameKey = "name"
  val encoding = "UTF-8"

  test("HTML Output Servlet") {
    val mockRequest = mock[HttpServletRequest]
    EasyMock.expect(mockRequest.getParameter(nameKey)).andReturn("testName.html")
    val sw = new StringWriter
    val mockResponse = createMockResponse(sw, "text/html")
    val (mockConfig, mockContext) = createMockConfigAndContext

    val hos = new HtmlOutputServlet
    hos.init(mockConfig)
    setToReplay(mockRequest, mockResponse, mockConfig, mockContext)
    hos.doGet(mockRequest, mockResponse)
    setToVerify(mockRequest, mockResponse, mockConfig, mockContext)

    assert(sw.toString === "Fail: HTML couldn't be obtained.")
  }

  test("CSS Output Servlet") {
    val mockRequest = mock[HttpServletRequest]
    EasyMock.expect(mockRequest.getParameter(nameKey)).andReturn("testName.css")
    val sw = new StringWriter
    val mockResponse = createMockResponse(sw, "text/css")
    val (mockConfig, mockContext) = createMockConfigAndContext

    val cos = new CssOutputServlet
    cos.init(mockConfig)
    setToReplay(mockRequest, mockResponse, mockConfig, mockContext)
    cos.doGet(mockRequest, mockResponse)
    setToVerify(mockRequest, mockResponse, mockConfig, mockContext)

    assert(sw.toString === "Fail: CSS couldn't be obtained.")
  }

  test("IRC Output Servlet") {
    val mockRequest = mock[HttpServletRequest]
    EasyMock.expect(mockRequest.getParameter(nameKey)).andReturn("testName.irc")
    val sw = new StringWriter
    val mockResponse = createMockResponse(sw, "text/plain")
    val (mockConfig, mockContext) = createMockConfigAndContext

    val ios = new IrcOutputServlet
    ios.init(mockConfig)
    setToReplay(mockRequest, mockResponse, mockConfig, mockContext)
    ios.doGet(mockRequest, mockResponse)
    setToVerify(mockRequest, mockResponse, mockConfig, mockContext)

    assert(sw.toString === "Fail: IRC text couldn't be obtained.")
  }

  private def createMockConfigAndContext(): (ServletConfig, ServletContext) = {
    val mockConfig = mock[ServletConfig]
    val mockContext = mock[ServletContext]
    EasyMock.expect(mockConfig.getServletContext).andReturn(mockContext).anyTimes
    (mockConfig, mockContext)
  }

  private def createMockResponse(sw: StringWriter, content: String): HttpServletResponse = {
    val mockResponse = mock[HttpServletResponse]
    EasyMock.expect(mockResponse.setCharacterEncoding(encoding))
    EasyMock.expect(mockResponse.setContentType(content))
    EasyMock.expect(mockResponse.getWriter).andReturn(new PrintWriter(sw))
    mockResponse
  }

  private def setToReplay(request: HttpServletRequest,
    response: HttpServletResponse, config: ServletConfig, context: ServletContext): Unit = {
    EasyMock.replay(request)
    EasyMock.replay(response)
    EasyMock.replay(config)
    EasyMock.replay(context)
  }

  private def setToVerify(request: HttpServletRequest,
    response: HttpServletResponse, config: ServletConfig, context: ServletContext): Unit = {
    EasyMock.verify(request)
    EasyMock.verify(response)
    EasyMock.verify(config)
    EasyMock.verify(context)
  }
}
