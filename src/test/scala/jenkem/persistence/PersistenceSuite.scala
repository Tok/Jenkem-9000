/*
 * #%L
 * PersistenceSuite.scala - Jenkem - Tok - 2012
 * %%
 * Copyright (C) 2012 - 2013 Lukas Steiger
 *                 <lsteiger4@gmail.com>
 * %%
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar.
 * See http://www.wtfpl.net/ for more details.
 * #L%
 */
package jenkem.persistence

import org.datanucleus.api.jdo.JDOPersistenceManager
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory
import org.junit.runner.RunWith
import javax.jdo.spi.PersistenceCapable
import jenkem.AbstractTester
import jenkem.DbTest
import jenkem.persistence.data.ImageCss
import jenkem.persistence.data.ImageHtml
import jenkem.persistence.data.ImageInfo
import jenkem.persistence.data.ImageIrc
import jenkem.persistence.data.JenkemImage
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PersistenceSuite extends AbstractTester {
  val change = "#"

  test("PMF Instance") {
    val pmf = PMF.get
    assert(pmf.isInstanceOf[JDOPersistenceManagerFactory])
    val pm = pmf.getPersistenceManager
    assert(pm.isInstanceOf[JDOPersistenceManager])
  }

  test("PMF Properties") {
    val connUrl = PMF.properties.getProperty("javax.jdo.option.ConnectionURL")
    assert(connUrl === "mongodb:/jenkem")
    val pmfClass = PMF.properties.getProperty("javax.jdo.PersistenceManagerFactoryClass")
    assert(pmfClass === "org.datanucleus.api.jdo.JDOPersistenceManagerFactory")
    val mapping = PMF.properties.getProperty("javax.jdo.option.Mapping")
    assert(mapping === "mongodb")
  }

  test("Environment Variables") {
    assert(PMF.dbHost === None.orNull)
    assert(PMF.dbPort === None.orNull)
    assert(PMF.dbName === None.orNull)
    assert(PMF.dbUser === None.orNull)
    assert(PMF.dbPass === None.orNull)
  }

  test("PersistenceService Constants") {
    assert(PersistenceService.QUERY_RANGE === 200L)
  }

  val nameString = "name"
  val ircString = "irc"
  val htmlString = "html"
  val cssString = "css"
  val iconString = "icon"
  val methodString = "method"
  val charactersString = "characters"
  val contrast: Short = 1
  val brightness: Short = 0
  val lines: Short = 10
  val lineWidth: Short = 50
  val dateString = "date"
  val iinfo = new ImageInfo(nameString, iconString, methodString, charactersString, contrast, brightness, lines, lineWidth, dateString)
  val ihtml = new ImageHtml(nameString, htmlString)
  val icss = new ImageCss(nameString, cssString)
  val iirc = new ImageIrc(nameString, ircString)
  val ji = new JenkemImage(iinfo, ihtml, icss, iirc)

  test("Get HTML By Name") {
    assert(PersistenceService.getImageHtmlByName(nameString + change) === None)
  }

  test("Get CSS By Name") {
    assert(PersistenceService.getImageCssByName(nameString + change) === None)
  }

  test("Get IRC By Name") {
    assert(PersistenceService.getImageIrcByName(nameString + change) === None)
  }

  test("Save And Read", DbTest) {
    val info = new ImageInfo(nameString, iconString, methodString, charactersString, contrast, brightness, lines, lineWidth, dateString)
    val html = new ImageHtml(nameString, htmlString)
    val css = new ImageCss(nameString, cssString)
    val irc = new ImageIrc(nameString, ircString)
    val img = new JenkemImage(info, html, css, irc)
    assert(PersistenceService.saveJenkemImage(img))
    val rhtml = PersistenceService.getImageHtmlByName(nameString).get
    assert(rhtml._id === nameString)
    assert(rhtml.toString === nameString)
    assert(rhtml.name === nameString)
    assert(rhtml.html === htmlString)
    testPc(ihtml.asInstanceOf[PersistenceCapable])
    val rcss = PersistenceService.getImageCssByName(nameString).get
    assert(rcss._id === nameString)
    assert(rcss.toString === nameString)
    assert(rcss.name === nameString)
    assert(rcss.css === cssString)
    testPc(rcss.asInstanceOf[PersistenceCapable])
    val rirc = PersistenceService.getImageIrcByName(nameString).get
    assert(rirc.name === nameString)
    assert(rirc._id === nameString)
    assert(rirc.toString === nameString)
    assert(rirc.name === nameString)
    assert(rirc.irc === ircString)
    testPc(rirc.asInstanceOf[PersistenceCapable])
    assert(!PersistenceService.getAllImageInfo.isEmpty)
    val nInfo = new ImageInfo(nameString, iconString + change, methodString + change,
        charactersString + change, contrast, brightness, lines, lineWidth, dateString)
    val nHtml = new ImageHtml(nameString, htmlString + change)
    val nCss = new ImageCss(nameString, cssString + change)
    val nIrc = new ImageIrc(nameString, ircString + change)
    val nImg = new JenkemImage(nInfo, nHtml, nCss, nIrc)
    assert(PersistenceService.saveJenkemImage(nImg))
    assert(PersistenceService.getImageHtmlByName(nameString).get.name === nameString)
    assert(PersistenceService.getImageCssByName(nameString).get.name === nameString)
    assert(PersistenceService.getImageIrcByName(nameString).get.name === nameString)
  }

  test("Image IRC") {
    assert(iirc._id === nameString)
    assert(iirc.toString === nameString)
    assert(iirc.name === nameString)
    assert(iirc.irc === ircString)
    val pc = iirc.asInstanceOf[PersistenceCapable]
    testPc(pc)
  }

  test("Image HTML") {
    assert(ihtml._id === nameString)
    assert(ihtml.toString === nameString)
    assert(ihtml.name === nameString)
    assert(ihtml.html === htmlString)
    val pc = ihtml.asInstanceOf[PersistenceCapable]
    testPc(pc)
  }

  test("Image CSS") {
    assert(icss._id === nameString)
    assert(icss.toString === nameString)
    assert(icss.name === nameString)
    assert(icss.css === cssString)
    val pc = icss.asInstanceOf[PersistenceCapable]
    testPc(pc)
  }

  test("Image Info") {
    assert(iinfo._id === nameString)
    assert(iinfo.toString === nameString)
    assert(iinfo.name === nameString)
    assert(iinfo.icon === iconString)
    assert(iinfo.method === methodString)
    assert(iinfo.contrast === contrast)
    assert(iinfo.brightness === brightness)
    assert(iinfo.lines === lines)
    assert(iinfo.lineWidth === lineWidth)
    assert(iinfo.creation === dateString)
    val pc = iinfo.asInstanceOf[PersistenceCapable]
    testPc(pc)
  }

  test("Jenkem Image") {
    assert(ji.html.toString === nameString)
    assert(ji.css.toString === nameString)
    assert(ji.irc.toString === nameString)
    assert(ji.info.toString === nameString)
    assert(ji.values.contains(ji.HTML))
    assert(ji.values.contains(ji.CSS))
    assert(ji.values.contains(ji.IRC))
    assert(ji.values.contains(ji.INFO))
    testPart(ji.HTML, "html")
    testPart(ji.CSS, "css")
    testPart(ji.IRC, "irc")
    testPart(ji.INFO, "info")
  }

  private def testPart(part: ji.Part, name: String): Unit = {
    assert(part !== None.orNull)
    assert(part.toString.equalsIgnoreCase(name))
  }

  class Oifc extends PersistenceCapable.ObjectIdFieldConsumer {
    def storeBooleanField(fn: Int, v: Boolean): Unit = {}
    def storeCharField(fn: Int, v: Char): Unit = {}
    def storeByteField(fn: Int, v: Byte): Unit = {}
    def storeShortField(fn: Int, v: Short): Unit = {}
    def storeIntField(fn: Int, v: Int): Unit = {}
    def storeLongField(fn: Int, v: Long): Unit = {}
    def storeFloatField(fn: Int, v: Float): Unit = {}
    def storeDoubleField(fn: Int, v: Double): Unit = {}
    def storeStringField(fn: Int, v: String): Unit = {}
    def storeObjectField(fn: Int, v: Object): Unit = {}
  }

  private def testPc(pc: PersistenceCapable): Unit = {
    assert(!pc.jdoIsDeleted)
    assert(!pc.jdoIsDetached)
    assert(!pc.jdoIsDirty)
    assert(!pc.jdoIsNew)
    assert(!pc.jdoIsPersistent)
    assert(!pc.jdoIsTransactional)
    assert(pc.jdoGetObjectId === None.orNull)
    assert(pc.jdoGetPersistenceManager === None.orNull)
    assert(pc.jdoGetTransactionalObjectId === None.orNull)
    assert(pc.jdoGetVersion === None.orNull)
    pc.jdoMakeDirty(nameString)
    assert(!pc.jdoIsDirty) //still not dirty
    assert(pc.jdoNewObjectIdInstance.toString === nameString)
    pc.jdoReplaceFlags

    pc.jdoNewObjectIdInstance("")
    intercept[Exception] { pc.jdoCopyKeyFieldsFromObjectId(new Oifc, "") }
  }
}
