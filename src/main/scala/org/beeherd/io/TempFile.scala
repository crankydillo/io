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

import java.io.File

object TempFile {
  /** 
   * Create a temporary file for some use.  The file is deleted when done
   * using it.
   */
  def use[T](f: File =>  T): T = {
    val tmp = File.createTempFile("bh-file", "tmp");
    tmp.deleteOnExit;
    try {
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
