package compiler.symbol_table

import compiler.block.Block
import java.util.ArrayList
import java.util.List

object SymbolTable {
  private val SYMBOL_TABLE: SymbolTable = new SymbolTable

  def getInstance: SymbolTable = {
    return SYMBOL_TABLE
  }
}

class SymbolTable() {
  var rows: List[Row] = new ArrayList[Row]

  def addRow(row: Row) {
    rows.add(row)
  }

  def exists(name: String, methodName: String, className: String): Boolean = {
    if (name == null) {
      return false
    }
    import scala.collection.JavaConversions._
    for (r <- rows) {
      if (r.getName == null || r.getMethodName == null || r.getClassName == null) {

      } else {
        if (r.getName == name && r.getMethodName == methodName && r.getClassName == className) {
          return true
        }
      }
    }
    // Check ifs variables declared in class scope exist
    import scala.collection.JavaConversions._
    for (r <- rows) {
      if (methodName == null && r.getMethodName == null && name != null && r.getName != null) {
        if (name == r.getName) {
          return true
        }
      }
    }
    return false
  }

  def getType(block: Block): String = {
    return block.getType
  }

  def printSymbols() {
    System.out.printf("%-1s %-15s %-1s %-15s %-1s %-15s %-1s %-15s %-1s %-15s \n", "|", "Name", "|", "Type", "|", "Value", "|", "Method", "|", "Class")
    System.out.printf("%-1s %-15s %-1s %-15s %-1s %-15s %-1s %-15s %-1s %-15s \n", "+", "----", "+", "----", "+", "----", "+", "----", "+", "----")
    import scala.collection.JavaConversions._
    for (row <- rows) {
      System.out.printf("%-1s %-15s %-1s %-15s %-1s %-15s %-1s %-15s %-1s %-15s %-1s \n", " ", row.getName, " ", row.getType, " ", row.getValue, " ", row.getMethodName, " ", row.getClassName, " ")
    }
  }

  def getValue(method: Block, variableName: String): Row = {
    import scala.collection.JavaConversions._
    for (row <- rows) {
      if (row.getMethodName != null && row.getMethodName == method.getName && row.getName != null && row.getName == variableName) {
        return row
      }
    }
    return null
  }
}