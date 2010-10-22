package org.beeherd.io

import java.io.File

import org.apache.commons.io.FileUtils

import org.specs._
import org.specs.runner.JUnit4
        
class TempDirSpecTest extends JUnit4(TempDirSpec)
object TempDirSpec extends Specification {
  "The TempDir object" should {
    "allow a user to easily use a temporary directory" in {
      var tmp: File = null;
      TempDir.use {d =>
        d must beDirectory
        tmp = d;
        val f = new File(d, "hi");
        f.createNewFile();
        FileUtils.writeStringToFile(f, "hello");
        FileUtils.readFileToString(f) + " world"
      } must beEqual("hello world");
      tmp.exists must beFalse
    }
  }
}
