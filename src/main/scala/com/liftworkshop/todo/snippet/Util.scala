/*
 * Util.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.liftworkshop.todo.snippet

import _root_.scala.xml.{NodeSeq}
import _root_.com.liftworkshop._
import _root_.com.liftworkshop.todo.model._

class Util {
    def in(html : NodeSeq) =
        if (User.loggedIn_?) html else NodeSeq.Empty

    def out(html : NodeSeq) =
        if (!User.loggedIn_?) html else NodeSeq.Empty
        

}
