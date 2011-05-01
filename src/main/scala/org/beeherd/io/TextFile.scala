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
  , linePositionsToCache: Int = 10000
) {

  require(file != null)
  require(file.exists)
  require(file.isFile)
  require(charset != null)

  /**
  * Return the number of lines in the text file.
  lazy val numLines: Long = lineByteIndexes.size
  */

  // oh nos...
  private var cachedLineInfo: CachedLineInfo = null;
  private var cachedLine: CachedLine = null;

  def line(lineNum: Int): Option[String] = {
    
    require(lineNum >= 1);
    if (cachedLineInfo == null || !cachedLineInfo.contains(lineNum))
      cachedLineInfo = redoCache(lineNum);

    val LineInfo(lineStart, lineLength) = cachedLineInfo.lineInfo(lineNum);
    val randomAccess = new RandomAccessFile(file, "r");
    randomAccess.seek(lineStart);
    val bytes = Array.ofDim[Byte](lineLength);
    val res = randomAccess.read(bytes, 0, lineLength);
    assert (res != -1)
    Some(new String(bytes, charset))
  }

  def line2(lineNum: Int): Option[String] = {
    require(lineNum >= 1);
    if (cachedLine == null || !cachedLine.contains(lineNum))
      // also I can use CTRL-Left Click
      cachedLine = redoCache2(lineNum);

    Some(cachedLine.line(lineNum))
  }


  private def redoCache2(lineNum: Int): CachedLine = {
    val reader = new BufferedReader(new InputStreamReader(
        new FileInputStream(file), charset));
    val surrounding = linePositionsToCache / 2;
    val start = {
      val tmp = lineNum - surrounding - 1;
      if (tmp <= 0) 1
      else tmp
    }
    val end = lineNum + surrounding;

    var currentLineIdx = 1;
    try {
      while (currentLineIdx < start && reader.readLine() != null)
        currentLineIdx += 1;

      val buffer = new scala.collection.mutable.ArrayBuffer[String]();
      var currentLine = reader.readLine();
      while (currentLine != null && currentLineIdx < end) {
        buffer += currentLine;
        currentLine = reader.readLine();
        currentLineIdx += 1;
      }
      CachedLine(start, currentLineIdx, buffer.toArray)

    } finally {
      try { reader.close() } catch { case e:Exception => {} }
    }
  }

  private def redoCache(lineNum: Int): CachedLineInfo = {
    val sepBytes = lineSeparator.getBytes
    val sepBytesLength = sepBytes.length
    val in = new BufferedInputStream(new FileInputStream(file));
    val surrounding = linePositionsToCache / 2;
    val start = {
      val tmp = lineNum - surrounding - 1;
      if (tmp < 0) 0
      else tmp
    }
    val end = lineNum + surrounding;

    println("start: " + start)
    println("end: " + end)

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

      // Scan down to the first line of the window we want to capture
      var currentLineIdx = 1;
      var lineStart = 0; //sepBytesLength;  // byte index that a line starts
      while (result != -1 && currentLineIdx < start) {
        if (atNewLine)
          currentLineIdx += 1;
        result = in.read(bytes, 0, sepBytesLength);
        lineStart += sepBytesLength;
      }

      // Now, we'll capture our window.
      if (result == -1) 
        return CachedLineInfo(0, 0, Array());

      val buffer = new scala.collection.mutable.ArrayBuffer[LineInfo]();
      var lineLength = 0;

      while (result != -1 && currentLineIdx < end) {
        if (atNewLine) {
          buffer += LineInfo(lineStart, lineLength)
          currentLineIdx += 1;
          lineStart += (lineLength + 1)
          lineLength = 0;
        } else {
          lineLength += sepBytesLength
        }
        result = in.read(bytes, 0, sepBytesLength);
      }

      println(currentLineIdx);
      CachedLineInfo(start, currentLineIdx, buffer.toArray)

    } finally {
      try { in.close() } catch { case e:Exception => {} }
    }
  }


  private case class CachedLineInfo(start: Int, end: Int, lineInfos: Array[LineInfo]) {
    def contains(lineNum: Int) = lineNum >= start && lineNum <= end

    def lineInfo(lineNum: Int) = lineInfos(lineNum - start)
  }

  private case class CachedLine(start: Int, end: Int, lines: Array[String]) {
    def contains(lineNum: Int) = lineNum >= start && lineNum <= end

    def line(lineNum: Int) = lines(lineNum - start)
  }


  // If you have a sequence of these, you don't need the line length (numBytes).
  private case class LineInfo(index: Int, numBytes: Int)
}
