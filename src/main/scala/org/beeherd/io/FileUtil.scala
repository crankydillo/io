package org.beeherd.io

import java.io.File

object FileUtil {
  /** 
   * Delete the file whether it is a directory or a file.
   */
  def delete(f: File): Unit = {
   // For now, I'm going to do some stuff commons-io will do so that the
   // project doesn't have that dependency.  Don't get out of hand with this..
    if (!f.isDirectory)
      f.delete;
    else {
      f.listFiles.foreach {delete _}
      f.delete // now we have to worry about the stack..
    }
  }
}
