/*
 * #%L
 * CropperState.scala - Jenkem - Tok - 2012
 * %%
 * Copyright (C) 2012 - 2013 Lukas Steiger <lsteiger4@gmail.com>
 * %%
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar.
 * See http://www.wtfpl.net/ for more details.
 * #L%
 */
package jenkem.js

import com.vaadin.shared.ui.JavaScriptComponentState

class CropperState extends JavaScriptComponentState {
  var imageSrc: String = ""
  def getImageSrc: String = imageSrc
  def setImageSrc(imageSrc: String): Unit = { this.imageSrc = imageSrc }
}
