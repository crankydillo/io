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
