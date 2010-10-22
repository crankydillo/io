package org.beeherd.io

import java.io.File

object TempFile {
  /** 
   * Create a temporary file for some use.  The file is deleted when done
   * using it.
   */
  def use[T](f: File =>  T): T = {
    val tmp = File.createTempFile("beeherd", "tmp");
    tmp.deleteOnExit;
    try {
      f(tmp);
    } finally {
      try {
        tmp.delete;
      } catch {
        case e:Exception => { /*TODO Log */  }
      }
    }
  }
}
