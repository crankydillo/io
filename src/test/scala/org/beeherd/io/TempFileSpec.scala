package org.beeherd.io

import java.io.File

import org.apache.commons.io.FileUtils

import org.specs._
import org.specs.runner.JUnit4
        
class TempFileSpecTest extends JUnit4(TempFileSpec)
object TempFileSpec extends Specification {
  "The TempFile object" should {
    "allow a user to easily use a temp file" in {
      var tmp: File = null;
      TempFile.use {f =>
        tmp = f;
        FileUtils.writeStringToFile(f, "hello");
        FileUtils.readFileToString(f) + " world"
      } must beEqual("hello world");
      tmp.exists must beFalse
    }
  }
}
