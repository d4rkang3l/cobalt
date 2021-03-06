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

package compiler.structure.parsers.structures.kinds

import compiler.data.parameters.Parameters
import compiler.structure.blocks.Block
import compiler.structure.blocks.structures.kinds.ObjectBlock
import compiler.structure.parsers.Parser
import compiler.tokenizer.Tokenizer

class ObjectParser extends Parser[ObjectBlock] {

  /**
    * A list of all regular expressions
    *
    * @return
    */
  override def getRegexs: List[String] = List(
    "object[ ]+[a-zA-Z][a-zA-Z0-9]*:[ ]*"
  )

  /**
    * Takes a line and checks to see ifs it is for this parsers by using regex.
    */
  override def shouldParse(line: String): Boolean = (getRegexs.filter(line.matches(_)).size > 0)

  def parse(superBlock: Block, tokenizer: Tokenizer): ObjectBlock = {
    tokenizer.nextToken
    val objectName: String = tokenizer.nextToken.token

    val parameters = new Parameters().getParameters("")

    val parentClass = "java/lang/Object"


    val implementedClasses = ""



    return new ObjectBlock(superBlock, objectName, parameters.toArray, parentClass, implementedClasses)
  }
}
