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

package compiler.structure.parsers.ifs

import compiler.structure.blocks.Block
import compiler.structure.blocks.ifs.IfBlock
import compiler.structure.parsers.Parser
import compiler.tokenizer.Tokenizer

class IfParser extends Parser[IfBlock] {

  /**
    * A list of all regular expressions
    *
    * @return
    */
  override def getRegexs: List[String] = List("if \\((.*)*\\):")

  override def shouldParse(line: String): Boolean = (getRegexs.filter(line.matches(_)).size > 0)

  def parse(superBlock: Block, tokenizer: Tokenizer): IfBlock = {
    tokenizer.nextToken //skip if
    tokenizer.nextToken // skip (

    // Loop through getting each parameter and  adding to a String
    var statement: String = ""
    var nextToken: String = tokenizer.nextToken.token
    while (nextToken != ")") {
      {
        if (nextToken == "=") statement += nextToken
        else statement += " " + nextToken + " "
        nextToken = tokenizer.nextToken.token
      }
    }
    return new IfBlock(superBlock, statement.trim.replaceAll(" +", " "))
  }
}
