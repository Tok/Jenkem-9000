package jenkem.util

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class UrlOptionizerSuite extends FunSuite {
  test("Extract valid") {
    val urlString = "http://host.com:80/path"
    val url = UrlOptionizer.extract(urlString)
    assert(url.get.isInstanceOf[java.net.URL])
    assert(url.get.toString.equals(urlString))
  }

  test("Extract valid without port") {
    val urlString = "http://host.com/path"
    val url = UrlOptionizer.extract(urlString)
    assert(url.get.isInstanceOf[java.net.URL])
    assert(url.get.toString.equals(urlString))
  }

  test("Try to extract invalid") {
    assert(UrlOptionizer.extract("ht--tp://host.com:1111/path") === None)
    assert(UrlOptionizer.extract("http://host.com:----/path") === None)
  }
}
