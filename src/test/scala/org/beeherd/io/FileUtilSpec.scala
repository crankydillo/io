package org.beeherd.io

import java.io.File

import org.apache.commons.io.FileUtils

import org.specs._
import org.specs.runner.JUnit4
        
class FileUtilSpecTest extends JUnit4(FileUtilSpec)
object FileUtilSpec extends Specification {
  "The FileUtil object" should {
    "allow a user to delete a file" in {
      // should I use project stuff like this???
      TempFile.use {f =>
        FileUtil.delete(f);
        f.exists must beFalse
      } 
    }

    "allow a user to delete a directory" in {
      TempDir.use {d =>
        val f = new File(d, "hi");
        f.createNewFile();
        FileUtil.delete(d);
        d.exists must beFalse
      } 
    }

  }
}
