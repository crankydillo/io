package org.beeherd.io

import java.io.{File, FileOutputStream}

object FileUtil {
  /** 
   * Delete the file whether it is a directory or a file.
   * <p>
   * This uses recursion.  Deep directories might overflow the stack.
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

  /**
   * Use some file for writing.  Clean is handled.
   */
   def write[T](f: File)(fun: FileOutputStream => T): T = {
    // I saw some Odersky speech where he had this concept applied to anything
    // that provided a close() method, which probably relies on reflection...
    val out = new FileOutputStream(f);
    try {
      fun(out);
    } finally {
      if (out != null) {
        try {
          out.close()
        } catch {
          case e:Exception => {/* TODO Log?? */}
        }
      }
    }
  }
}
