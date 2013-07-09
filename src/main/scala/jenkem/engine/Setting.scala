/*
 * #%L
 * Setting.scala - Jenkem - Tok - 2012
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
package jenkem.engine

sealed abstract class Setting(val caption: String)

object Setting {
  case object UPDOWN extends Setting("\"_")
  case object LEFTRIGHT extends Setting("[]")
  case object DBQP extends Setting("dbqp")
  case object DIAGONAL extends Setting("\\ /")
  case object VERTICAL extends Setting("|")
  case object HORIZONTAL extends Setting("-")
  case object FULLBLOCK extends Setting("█")
  val values = List(UPDOWN, LEFTRIGHT, DBQP, DIAGONAL, VERTICAL, HORIZONTAL, FULLBLOCK)

  val min = -100
  val max = 100
  val default = 0

  type Pair = (Boolean, Int)
  class Instance(val ud: Pair, val lr: Pair, val dbqp: Boolean, val diag: Boolean, val vert: Boolean, val hor: Boolean, val fb: Boolean) {
    def has(setting: Setting): Boolean = {
      if (setting.equals(DBQP)) { dbqp }
      else if (setting.equals(DIAGONAL)) { diag }
      else if (setting.equals(VERTICAL)) { vert }
      else if (setting.equals(HORIZONTAL)) { hor }
      else if (setting.equals(FULLBLOCK)) { fb }
      else { getPair(setting)._1 }
    }
    def get(setting: Setting): Int = getPair(setting)._2
    private def getPair(setting: Setting): Pair = {
      setting match {
        case UPDOWN => ud
        case LEFTRIGHT => lr
        case _ => (false, default)
      }
    }
  }

  def getInitial(hasAnsi: Boolean, fullBlock: Boolean): Instance = {
    new Instance((true, default), (true, default), (!hasAnsi), (!hasAnsi), (false), (false), (fullBlock))
  }
}
