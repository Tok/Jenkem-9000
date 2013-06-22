package jenkem.js

import com.vaadin.shared.ui.JavaScriptComponentState

class CropperState extends JavaScriptComponentState {
  var imageSrc: String = ""
  def getImageSrc: String = imageSrc
  def setImageSrc(imageSrc: String) { this.imageSrc = imageSrc }
}
