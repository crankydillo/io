package org.beeherd.io

import java.io.File

object TempDir {
  /** 
   * Create a temporary directory for some use.  The directory is deleted when done
   * using it.
   */
  def use[T](f: File =>  T): T = {
    val tmp = File.createTempFile("bh-dir", "tmp");
    tmp.deleteOnExit;
    try {
      tmp.delete();
      tmp.mkdirs();
      f(tmp);
    } finally {
      try {
        FileUtil.delete(tmp);
      } catch {
        case e:Exception => { /*TODO Log */  }
      }
    }
  }
}
