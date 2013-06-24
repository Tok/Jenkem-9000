package jenkem.js

import scala.Array.canBuildFrom
import org.json.JSONArray
import org.json.JSONException
import com.vaadin.annotations.JavaScript
import com.vaadin.ui.AbstractJavaScriptComponent
import com.vaadin.ui.JavaScriptFunction
import com.vaadin.ui.Notification
import scala.collection.mutable.ArrayBuffer

/**
 * https://vaadin.com/book/vaadin7/-/page/gwt.javascript.html
 */
@JavaScript(Array("croplib.js", "cropper-connector.js"))
class Cropper extends AbstractJavaScriptComponent {
  var isReady = false;

  addFunction(
    "onSelect",
    new JavaScriptFunction() {
      @throws(classOf[JSONException])
      override def call(arguments: JSONArray) {
        if (isReady) { //ignore first resize
          val coords = arguments.get(0).asInstanceOf[JSONArray];
          val crops = new Crops(
            coords.getInt(0),
            coords.getInt(1),
            coords.getInt(2),
            coords.getInt(3),
            coords.getInt(4),
            coords.getInt(5))
          listeners.foreach(_.valueChange(crops))
        }
        isReady = true
      }
    })

  val listeners: ArrayBuffer[CropperChangeListener] = ArrayBuffer[CropperChangeListener]()
  def addListener(listener: CropperChangeListener): Unit = { listeners += listener }

  def setImageSrc(imageSrc: String): Unit = getState.setImageSrc(imageSrc)
  def getImageSrc: String = getState.getImageSrc

  override def getState: CropperState = super.getState.asInstanceOf[CropperState]
}
