/*
 * Cobalt Programming Language Compiler
 * Copyright (C) 2017  Cobalt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package compiler.structure.blocks.structures

import java.io.File

import compiler.structure.blocks.Block
import compiler.structure.blocks.packages.PackageBlock
import compiler.utilities.Utils

/**
  * Represents the whole file.
  */
class FileBlock(name: String, buildDir: File) extends Block(null, true, false) {

  def init() {}

  def getName: String = name

  def getValue: String = ""

  def getType: String = "file"

  def getOpeningCode: String = {

    asm.getPackage(Utils.packageBlock(this).directory.replace("/", ".")) +
    asm.getImport("java.io.DataOutputStream") +
    asm.getImport("java.io.FileNotFoundException") +
    asm.getImport("java.io.FileOutputStream") +
    asm.getImport("java.io.IOException") +
    asm.getImport("java.io.*") +
    asm.getStaticImport("org.objectweb.asm.Opcodes.*") +
    asm.getImport("org.objectweb.asm.*")
  }

  def getClosingCode: String = {
    val packageBlock: PackageBlock = {

        var result = new PackageBlock("")
        for (sub <- subBlocks)
          if (sub.isInstanceOf[PackageBlock])
             result = sub.asInstanceOf[PackageBlock]
        result
    }
    "    public static void main(String [] args){\n   " +
      "new File(new File(\"" + buildDir.getPath.replace("\\", "/") + "/" + packageBlock.directory + "/" + name.split("\\.")(0) + ".class\").getParent()).mkdirs();" +
      "  DataOutputStream dout = null;\n" +
      "        try {\n" +
      "" + "            dout = new DataOutputStream(new FileOutputStream(\"" + buildDir.getPath.replace("\\", "/") + "/" + packageBlock.directory + "/" + name.split("\\.")(0) + ".class\"));\n" + "\n" + "        dout.write(execute());\n" + "        dout.flush();\n" + "        dout.close();\n" + "        } catch (FileNotFoundException e) {\n" + "        e.printStackTrace();\n" + "    } catch (IOException e) {\n" + "            e.printStackTrace();\n" + "        } catch (Exception e) {\n" + "            e.printStackTrace();\n" + "        " + "   } }\n" + "}"
  }

  override def toString: String = "file: " + name
}