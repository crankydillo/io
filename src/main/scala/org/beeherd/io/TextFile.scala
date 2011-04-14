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
  lazy val numLines: Long = lineByteIndexes.size

  def line(lineNum: Int): Option[String] = {
    require(lineNum >= 1);
    if (lineNum >= lineByteIndexes.size)
      return None
    val LineInfo(lineStart, lineLength) = lineByteIndexes(lineNum - 1);

    val randomAccess = new RandomAccessFile(file, "r");
    randomAccess.seek(lineStart);
    val bytes = Array.ofDim[Byte](lineLength);
    val res = randomAccess.read(bytes, 0, lineLength);
    assert (res != -1)
    Some(new String(bytes, charset))
  }

  private lazy val lineByteIndexes: Seq[LineInfo] = {
    // I'm going to assume that we need to use imperative style to handle
    // large files...
    val sepBytes = lineSeparator.getBytes
    val sepBytesLength = sepBytes.length
    val in = new BufferedInputStream(new FileInputStream(file));
    try {
      var bytes = Array.ofDim[Byte](sepBytesLength);
      
      var result = in.read(bytes, 0, sepBytesLength);

      def atNewLine: Boolean = {
        if (bytes.size != sepBytesLength)
          return false;
        var ctr = 0;
        while (ctr < sepBytesLength) {
          if (bytes(ctr) != sepBytes(ctr))
            return false
          ctr += 1;
        }
        true
      }
      
      if (result == -1) {
        Seq()
      } else {
        val buffer = new scala.collection.mutable.ArrayBuffer[LineInfo]();
        var lineStart = 0;  // byte index that a line starts
        var lineLength = 0;

        while (result != -1) {
          if (atNewLine) {
            buffer += LineInfo(lineStart, lineLength)
            lineStart += (lineLength + 1)
            lineLength = 0;
          } else {
            lineLength += sepBytesLength
          }
          result = in.read(bytes, 0, sepBytesLength);
        }
        buffer.toSeq
      }

    } finally {
      in.close();
    }
  }

  // If you have a sequence of these, you don't need the line length (numBytes).
  private case class LineInfo(index: Int, numBytes: Int)
}
