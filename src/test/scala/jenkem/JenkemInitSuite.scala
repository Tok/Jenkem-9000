/*
 * #%L
 * JenkemInitSuite.scala - Jenkem - Tok - 2012
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
    val e = intercept[Exception] { ji.init(mockRequest) }
    assert(e.isInstanceOf[com.vaadin.event.ListenerMethod$MethodException] || e.isInstanceOf[NullPointerException])
  }
}
