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

package compiler.structure.blocks

import java.util.ArrayList

import compiler.utilities.ASMGeneratorTest

/**
  * Stores the total amount of blocks to use as a unique identifier
  */
object Block {
  var TOTAL_BLOCKS: Int = 50
}

/**
  * Represents a blocks of code.
  *
  * @param superBlockInitTest The superBlock of this blocks
  * @param containerInit      Whether the blocks contains other blocks
  * @param variableInit       Whether the blocks is a variable
  */
abstract class Block(var superBlockInitTest: Block, val containerInit: Boolean, val variableInit: Boolean, immutableInit: Boolean = false) {

  Block.TOTAL_BLOCKS += 1

  private val _subBlocks: java.util.ArrayList[Block] = new ArrayList[Block]

  private val _asm: ASMGeneratorTest = new ASMGeneratorTest;
  private var _id: Integer = Block.TOTAL_BLOCKS
  private var _superBlock: Block = superBlockInitTest
  private var _container: Boolean = containerInit
  private var _variable: Boolean = variableInit
  private var _immutable: Boolean = immutableInit

  /* id GET and SET */
  def id = _id

  def id_=(value: Integer) = _id = value

  /* superBlock GET and SET */
  def superBlock = _superBlock

  def superBlock_=(value: Block) = _superBlock = value

  /* subBlocks GET */
  def subBlocks = _subBlocks.toArray(new Array[Block](_subBlocks.size()))

  def addBlock_=(value: Block) = _subBlocks.add(value)

  def removeBlock_=(value: Block) = _subBlocks.remove(value)

  // Called after file is parsed
  def init()

  /* Symbol table information */
  def getName: String

  def getValue: String

  def getType: String

  /* Bytecode for the opening and closing of the blocks */
  def getOpeningCode: String

  def getClosingCode: String

  /* Whether the blocks contains other blocks */
  def isContainer: Boolean = _container

  def container_=(value: Boolean) = _container = value

  /* Whether the blocks is  a variable */
  def isVariable: Boolean = _variable

  def variable_=(value: Boolean) = _variable = value

  /* Whether the blocks is immutable */
  def isImmutable: Boolean = _immutable

  def immutable_=(value: Boolean) = _immutable = value

  // Error check on the line of code
  def error(): Boolean = false

  // Used to generate ASM code
  def asm: ASMGeneratorTest = _asm
}