/*
 * #%L
 * InfoTab.scala - Jenkem - Tok - 2012
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
package jenkem.ui.tab

import com.vaadin.server.ExternalResource
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.Label
import com.vaadin.ui.Link
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.HorizontalLayout

class InfoTab extends VerticalLayout {
  val newLine = "\n"
  setCaption("Info")
  setSizeFull
  setMargin(true)

  addLabel("Welcome to the Jenkem 9000 ASCII converter.")
  addLabel(newLine)
  addLabel("To convert an image, go to the main tab, enter a link to the image and click the convert button.")
  addLabel("If you make a good conversion, you can add it to the gallery, by clicking the submit button.")
  addLabel("Other images with the same name will be repalced. You can save the HTML output from")
  addLabel("the gallery after submitting your conversion.")
  addLabel("If you do so, don't forget to also save the provided CSS and fix the stylesheet link in your")
  addLabel("HTML source if you want to upload it somewhere else.")
  addLabel(newLine)
  addLabel("Problems:")
  addLabel("IRC Clients:")
  addLabel("\tSome IRC clients limit the input that can be put on one line.")
  addLabel("\tDepending on your client, long lines with many color changes and therefore too much")
  addLabel("\tdata will be cut off or skipped to a new line and thereby break the image.")
  addLabel("\tIf this happens, you might try to use a lower setting for the line-width of your conversion.")
  addLabel("\tIt's often a good idea to test replaying your converted image into an empty channel")
  addLabel("\tbefore flooding it directly into a channel full with people idling in it.")
  addLabel("\tThe recommended font is Lucida Console.")
  addLabel(newLine)
  addLabel("Browsers:")
  addLabel("\tCropping and functions that rely on cropping do not work in Internet Explorer.")
  addLabel("\tThe recommended browser for Jenkem is Chrome because it has a fast JavaScript engine.")
  addLabel("\tFirefox also works.")
  addLabel(newLine)
  addLabel("Conversion Modes:")
  addLabel("Vortacular:")
  addLabel("\tThis is the default method for colored images.")
  addLabel("\tWill push four pixels into one character.")
  addLabel("Pwntari:")
  addLabel("\tMore pixels, but less colors.")
  addLabel("Plain:")
  addLabel("\tDoesn't output any colors.")
  addLabel("Stencil:")
  addLabel("\tThis method is best used with black & white images like stencils.")
  addLabel(newLine)
  addLabel("Color Schemes:")
  addLabel("Default:")
  addLabel("\tThe default color schema. Works best for fotos and colorful images.")
  addLabel("Old:")
  addLabel("\tAnother color schema, that was in place before the default was optimized.")
  addLabel("\tThe output looks similar to conversions with the default scheme.")
  addLabel("Vivid:")
  addLabel("\tA color schema with more vivid colors, by using a reduced amount of black and white pixels.")
  addLabel("Mono:")
  addLabel("\tDoes only use black & white and six colors. Output looks more old-school.")
  addLabel("LSD:")
  addLabel("\tBlack and white are highly reduced in order to make the output far more colorful.")
  addLabel("Skin:")
  addLabel("\tThis scheme was optimized for images with skin.")
  addLabel("\tHowever, default mode conversion often look better.")
  addLabel("Bwg:")
  addLabel("\tThis scheme is only using black, white and two shades of gray.")
  addLabel("\tWorks best for images without any color.")
  addLabel("Bw:")
  addLabel("\tOnly uses black and white. Works best for black & white images like stencils.")
  addLabel(newLine)
  addLabel("Contrast:")
  addLabel("\tThe contrast is multiplied with the color of each pixel.")
  addLabel("\tTry to lower the contrast settings to improve the output for bright images.")
  addLabel("Brightness:")
  addLabel("\tThe brightness is added to the color of each pixel.")
  addLabel("\tTry to adjust this value to fix the output after setting the contrast.")
  addLabel(newLine)
  addLabel("Kick:")
  addLabel("\tSets an offset for methods that use anti aliasing.")
  addLabel("\tSetting another option than \"0\" may result in a better output.")
  addLabel("\tChanging the kick is particulary useful for images that have important regions,")
  addLabel("\tlike for example the eyes in an image of a face.")
  addLabel(newLine)
  addLabel("Binary Output for IRC:")
  addLabel("\tOutputs the conversion as text with colors for IRC.")
  addLabel("\tYou can copy and paste this into your IRC-client to forward your conversion to IRC.")
  addLabel("\tMost clients will show it with the exact same color as in the HTML-preview.")
  addLabel("\tThe palette is obeying the defacto standard as used by mIRC.")
  addLabel("\tBut beware, many IRC-servers don't allow flooding and may get you kicked for doing this.")
  addLabel("\tDepending on your IRC client you may consider use a script that pastes the output")
  addLabel("\tlinewise with a delay of about 1500 ms in order to prevent this.")
  addLabel(newLine)
  addLabel("You can link to this application converting an image,")
  addLabel("by appending the image-url to the #main link like this:")
  addLink("http://jenkem-9000.rhcloud.com/#main/http://upload.wikimedia.org/wikipedia/commons/0/03/RGB_Colorcube_Corner_White.png")
  addLabel(newLine)
  addLabel("The source code of this application can be found at:")
  addLink("http://github.com/Tok/Jenkem-9000/")
  addLabel(newLine)
  addLabel("Jenkem 9000 project page:")
  addLink("http://tok.github.com/Jenkem-9000/")
  addLabel(newLine)
  addLabel(newLine)
  addLabel(newLine)
  val cacaPan = new HorizontalLayout
  val label = new Label("\tSalutations à nos amis du ")
  label.setContentMode(ContentMode.PREFORMATTED)
  cacaPan.addComponent(label)
  cacaPan.addComponent(new Link("laboratoires de caca", new ExternalResource("http://caca.zoy.org")))
  cacaPan.addComponent(new Label("."))
  addComponent(cacaPan)

  def addLink(text: String): Unit = addComponent(new Link(text, new ExternalResource(text)))
  def addLabel(text: String): Unit = {
    val label = new Label(text)
    label.setContentMode(ContentMode.PREFORMATTED)
    addComponent(label)
  }
}
