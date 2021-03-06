/**
* Copyright 2010 Samuel Cox
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.beeherd.io

import java.io.{BufferedOutputStream, File, FileOutputStream, OutputStream}

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
   def write[T](f: File)(fun: OutputStream => T): T = {
    // I saw some Odersky speech where he had this concept applied to anything
    // that provided a close() method, which probably relies on reflection...
    val out = new BufferedOutputStream(new FileOutputStream(f));
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
