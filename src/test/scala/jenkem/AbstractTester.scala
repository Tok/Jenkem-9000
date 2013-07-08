/*
 * #%L
 * AbstractTester.scala - Jenkem - Tok - 2012
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

import java.io.ByteArrayOutputStream
import java.io.PrintStream

import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite
import org.scalatest.mock.EasyMockSugar

abstract class AbstractTester extends FunSuite with EasyMockSugar with BeforeAndAfter {
  val printStream = System.err

  before {
    System.setErr(new PrintStream(new ByteArrayOutputStream))
  }

  after {
    System.setErr(printStream)
  }

  def testAny(a: Any, executeProductTests: Boolean): Unit = {
    val p = a.asInstanceOf[Product]
    assert(p.productIterator.isInstanceOf[Iterator[Any]])
    if (executeProductTests) {
      val e = intercept[IndexOutOfBoundsException] { p.productElement(0) }
      assert(e.isInstanceOf[IndexOutOfBoundsException])
      assert(p.productPrefix === a.toString)
      assert(p.productArity === 0)
    }
    val eq = a.asInstanceOf[Equals]
    assert(!eq.canEqual(new Object))
  }
}
