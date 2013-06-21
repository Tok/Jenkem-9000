package jenkem.js

import com.vaadin.shared.ui.JavaScriptComponentState

class CropperState extends JavaScriptComponentState {
  var imageSrc: String = ""
  def getImageSrc = imageSrc
  def setImageSrc(imageSrc: String) = this.imageSrc = imageSrc
}
