/*
 * #%L
 * OpenShiftUtil.scala - Jenkem - Tok - 2012
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
package jenkem.util

import java.util.NoSuchElementException

object OpenShiftUtil {
  def isOnOpenshift: Boolean =
    try { !Option(System.getenv("OPENSHIFT_APP_DNS")).get.isEmpty }
    catch { case nsee: NoSuchElementException => false }
}
