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

package compiler.runtime

import java.io._

import compiler.exceptions.{ContainerException, DeclarationException, IndentationException}
import compiler.structure.blocks.Block
import compiler.structure.blocks.ifs.IfBlock
import compiler.structure.blocks.imports.ImportBlock
import compiler.structure.blocks.loops.WhileBlock
import compiler.structure.blocks.operators.{AddBlock, DivideBlock, MultiplyBlock, SubtractBlock}
import compiler.structure.blocks.packages.PackageBlock
import compiler.structure.blocks.prints.PrintBlock
import compiler.structure.blocks.push.PushBlock
import compiler.structure.blocks.structures.kinds.ClassBlock
import compiler.structure.blocks.structures.methods.MethodBlock
import compiler.structure.blocks.structures.{FileBlock, ObjectMethodCallBlock}
import compiler.structure.parsers.Parser
import compiler.structure.parsers.comments.CommentParser
import compiler.structure.parsers.ifs.IfParser
import compiler.structure.parsers.imports.ImportParser
import compiler.structure.parsers.loops.{ForParser, WhileParser}
import compiler.structure.parsers.modifiers.ModifierParser
import compiler.structure.parsers.operators.{AddParser, DivideParser, MultiplyParser, SubtractParser}
import compiler.structure.parsers.packages.PackageParser
import compiler.structure.parsers.primitives._
import compiler.structure.parsers.prints.PrintParser
import compiler.structure.parsers.push.PushParser
import compiler.structure.parsers.structures.kinds.{ClassParser, ObjectParser}
import compiler.structure.parsers.structures.methods.MethodParser
import compiler.structure.parsers.structures.{MethodCallParser, ObjectDefinitionParser, ObjectMethodCallParser}
import compiler.symbol_table.{Row, SymbolTable}
import compiler.tokenizer.Tokenizer
import compiler.utilities.Utils

import scala.collection.JavaConverters._

class Runtime {
  private val parsers: Array[Parser[_]] = Array(

    new ModifierParser,
    // MethodBlock Parser
    new MethodParser,
    // Operator Parsers
    new AddParser,
    new DivideParser,
    new MultiplyParser,
    new SubtractParser,
    // Primitive Parsers
    new BooleanParser,
    new CharacterParser,
    new DoubleParser,
    new FloatParser,
    new IntegerParser,
    new LongParser,
    new StringParser,
    new ShortParser,
    // IfBlock Parser
    new IfParser,
    // PrintBlock Parser
    new PrintParser,
    new ForParser,
    new CommentParser,
    new MethodCallParser,
    new ImportParser,
    new WhileParser,
    new ObjectDefinitionParser,
    new ObjectMethodCallParser,
    new PackageParser,
    // Kinds Parsers
    new ObjectParser,
    new ClassParser,
    new PushParser
  )

  private var block: Block = null

  //start at 1 to ignore class declaration line

  private var methodName: String = null
  private var className: String = null

  def this(sourceFile: File, outputFile: File, buildDir: File) {
    this()
    var packageBlock: PackageBlock = null
    val imports: java.util.List[ImportBlock] = new java.util.ArrayList[ImportBlock]
    var br: BufferedReader = null
    try {
      // create a buffered reader
      br = new BufferedReader(new FileReader(sourceFile))
      var line: String = null
      var readImports = false
      while ((line = br.readLine) != null && !readImports) {

        if (line.trim == "")
          line = br.readLine()

        val fileBlock: Block = new FileBlock(sourceFile.getName, buildDir)

        for (parser <- parsers) {

          if (parser.shouldParse(line.trim)) {
            val tokenizer: Tokenizer = new Tokenizer(line)
            block = parser.parse(null, tokenizer)
            if (block.isInstanceOf[PackageBlock]) {
              packageBlock = block.asInstanceOf[PackageBlock]
            }
            else if (block.isInstanceOf[ImportBlock]) {
              imports.add(block.asInstanceOf[ImportBlock])
            }
            else {
              readImports = true
              block.superBlock_=(fileBlock)
              createBlock(block, br)
            }
          }
        }

        if (packageBlock != null) fileBlock.addBlock_$eq(packageBlock)

        for (importBlock <- imports.asScala) fileBlock.addBlock_$eq(importBlock)

        fileBlock.addBlock_$eq(block)
        block = fileBlock
      }
      if (br != null) br.close()

    } catch {
      case e: FileNotFoundException => e.printStackTrace()
      case e: IOException => e.printStackTrace()
    }

    // Compile
    new Compile(outputFile, block)

    // Output generated blocks structure
    Utils.printBlockInfo(block)

    // Output the symbol table
    SymbolTable.getInstance.printSymbols()
  }

  def createBlock(currentBlockInit: Block, br: BufferedReader, indentation: Int = 0, lineNumber: Int = 0): Block = {
    var currentBlock = currentBlockInit


    var line: String = br.readLine()

    if (line == null) {
      return null
    }

    if (line.trim == "") {
      line = br.readLine()

    }

    if (line == null) {
      return null
    }
    val currentIndentation = Utils.getIndentation(line)

    line = line.trim()
    val splitLine = line.split(";")
    if (splitLine.length > 1) {

      val t1 = new Tokenizer(splitLine(0))
      var nextBlock: Block = null
      for (parser <- parsers) {
        if (parser.shouldParse(splitLine(0).trim)) {
          nextBlock = parser.parse(currentBlock, t1)
          currentBlock.addBlock_$eq(nextBlock)
        }
      }

      val t2 = new Tokenizer(splitLine(1).trim)
      for (parser <- parsers) {
        if (parser.shouldParse(splitLine(1).trim)) {

          val inlineBlock = parser.parse(nextBlock, t2)
          nextBlock.addBlock_$eq(inlineBlock)
        }
      }

      createBlock(currentBlock, br, indentation, lineNumber + 1)

      return null
    }

    val tokenizer = new Tokenizer(line)
    if (currentBlock.isInstanceOf[MethodBlock]) methodName = currentBlock.getName
    if (currentBlock.isInstanceOf[ClassBlock]) className = currentBlock.getName
    // Check if the next symbol exists. If so then throw and error. If not then add to the symbol table.
    if (!currentBlock.isInstanceOf[AddBlock] && !currentBlock.isInstanceOf[SubtractBlock] && !currentBlock.isInstanceOf[MultiplyBlock] && !currentBlock.isInstanceOf[DivideBlock] && !currentBlock.isInstanceOf[IfBlock] && !currentBlock.isInstanceOf[WhileBlock] && !currentBlock.isInstanceOf[PrintBlock] && !currentBlock.isInstanceOf[ObjectMethodCallBlock] && !currentBlock.isInstanceOf[PushBlock]) if (SymbolTable.getInstance.exists(currentBlock.getName, methodName, className)) {
      throw new DeclarationException("Line: " + lineNumber + " " + currentBlock.getName + " has already been defined." + line)
    }
    else {
      SymbolTable.getInstance.addRow(new Row().setId(currentBlock.id).setName(currentBlock.getName).setType(currentBlock.getType).setValue(currentBlock.getValue).setMethodName(methodName).setClassName(className))
    }
    if (currentIndentation - indentation > 1) throw new IndentationException("Line: " + lineNumber + "    Indentation: " + (currentIndentation - indentation) + " " + line)


    // Indented out one
    else if (currentIndentation == (indentation + 1)) {

      if (!currentBlock.isContainer) throw new ContainerException("Line: " + lineNumber + "    Indentation: " + indentation + " " + currentBlock + " Block cannot store other blocks.")
      for (parser <- parsers) {
        if (parser.shouldParse(line)) {
          val nextBlock = parser.parse(currentBlock, tokenizer)
          currentBlock.addBlock_$eq(nextBlock)
          createBlock(nextBlock, br, currentIndentation, lineNumber + 1)
        }
      }
    }
    else if (currentIndentation == indentation) {
      // Indentation the same if (indentation == currentIndentation) { if (currentBlock.isContainer) throw new ContainerException("Line: " + lineNumber + "    Indentation: " + indentation + " " + currentBlock + " Minimum of one blocks stored within container.")
      for (parser <- parsers) {
        if (parser.shouldParse(line)) {
          val nextBlock = parser.parse(currentBlock.superBlock, tokenizer)
          currentBlock.superBlock.addBlock_$eq(nextBlock)
          createBlock(nextBlock, br, currentIndentation, lineNumber + 1)
        }
      }
    }
    else {
      // Indentation decreases by any amount
      for (parser <- parsers) {
        if (parser.shouldParse(line)) {
          currentBlock = currentBlock.superBlock
          var i = 0
          while (i < indentation - currentIndentation) {
            currentBlock = currentBlock.superBlock
            i += 1
          }
          val nextBlock = parser.parse(currentBlock, tokenizer)

          currentBlock.addBlock_$eq(nextBlock)
          createBlock(nextBlock, br, currentIndentation, lineNumber + 1)
        }
      }
    }

    null
  }
}
