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

import java.io._
import java.nio.charset.Charset

/**
* A class which can do some nifty things with files treated as text.
*/
class TextFile(
  file: File 
  , charset: Charset = Charset.forName(System.getProperty("file.encoding"))
  , lineSeparator: String = System.getProperty("line.separator")
  //, indexDir: File
) {

  require(file != null)
  require(file.exists)
  require(file.isFile)
  require(charset != null)
  /*
  require(indexDir != null)
  require(indexDir.isDirectory)
  */

  /**
  * Return the number of lines in the text file.
  */
  lazy val numLines: Int = {
    println("lsep: " + lineSeparator)
    val in = new BufferedInputStream(new FileInputStream(file));
    try {
      var byte = in.read();
      while (byte != -1) {
        println(byte);
        byte = in.read()
      }
      0
    } finally {
      in.close();
    }
  }

}
