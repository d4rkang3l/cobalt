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

package compiler.structure.parsers

import compiler.structure.blocks.Block
import compiler.tokenizer.Tokenizer

abstract class Parser[T <: Block] {


  /**
    * A list of all regular expressions
    *
    * @return
    */
  def getRegexs: List[String]

  /**
    * Takes a line and checks to see ifs it is for this parsers by using regex.
    */
  def shouldParse(line: String): Boolean

  /**
    * Take the superBlock and the tokenizer for the line and return a blocks of this parsers's type.
    */
  def parse(superBlock: Block, tokenizer: Tokenizer): Block
}