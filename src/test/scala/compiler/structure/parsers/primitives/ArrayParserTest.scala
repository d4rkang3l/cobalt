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

package compiler.structure.parsers.primitives

import compiler.structure.blocks.Block
import compiler.structure.blocks.primitives.ArrayBlockTest
import compiler.structure.parsers.ParserTest
import compiler.tokenizer.TokenizerTest

class ArrayParserTest extends ParserTest[ArrayBlockTest] {
  /**
    * Takes a line and checks to see ifs it is for this parsers by using regex.
    */
  override def shouldParse(line: String): Boolean = false

  /**
    * Take the superBlock and the tokenizer for the line and return a blocks of this parsers's type.
    */
  override def parse(superBlock: Block, tokenizer: TokenizerTest): Block = null
}
