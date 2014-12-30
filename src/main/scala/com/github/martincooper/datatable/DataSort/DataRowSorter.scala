/**
 * Copyright 2014 Martin Cooper
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.martincooper.datatable.DataSort

import com.github.martincooper.datatable.DataRow
import com.github.martincooper.datatable.DataSort.SortEnum.{ Descending, Ascending }

import scala.util.Try

/** Handles the multi column sorting on a DataRow. */
object DataRowSorter {

  /** Implements a Ordering[DataRow] for QuickSort. */
  def dataRowOrdering(sortItem: SortItem): Ordering[DataRow] = {
    dataRowOrdering(Seq(sortItem))
  }

  /** Implements a Ordering[DataRow] for QuickSort. */
  def dataRowOrdering(sortItems: Iterable[SortItem]): Ordering[DataRow] = {
    Ordering.fromLessThan { (rowOne: DataRow, rowTwo: DataRow) =>
      compare(rowOne, rowTwo, sortItems) > 0
    }
  }

  /** Method to support 'Ordered[DataRow]' for DataRows. */
  def compare(rowOne: DataRow, rowTwo: DataRow, sortItem: SortItem): Int = {
    compare(rowOne, rowTwo, Seq(sortItem))
  }

  /** Method to support 'Ordered[DataRow]' for DataRows. */
  def compare(rowOne: DataRow, rowTwo: DataRow, sortItems: Iterable[SortItem]): Int = {
    compareBySortItem(rowOne, rowTwo, sortItems)
  }

  /** Recursive sort method, handles multi-sort on columns. */
  private def compareBySortItem(rowOne: DataRow, rowTwo: DataRow, sortItems: Iterable[SortItem]): Int = {
    sortItems match {
      case Nil => 0
      case firstItem :: tail => compareValues(rowTwo, rowOne, firstItem) match {
        case 0 => compareBySortItem(rowOne, rowTwo, tail)
        case result => result
      }
    }
  }

  /**
   * Compares the two values in each specified column.
   * TODO : Currently only supports string comparison.
   */
  private def compareValues(rowOne: DataRow, rowTwo: DataRow, sortItem: SortItem): Int = {
    val valueOne = valueFromIdentity(rowOne, sortItem.columnIdentity)
    val valueTwo = valueFromIdentity(rowTwo, sortItem.columnIdentity)

    sortItem.order match {
      case Ascending => valueOne.toString.compareTo(valueTwo.toString)
      case Descending => valueTwo.toString.compareTo(valueOne.toString)
    }
  }

  /** Gets a value from a DataRow by ItemIdentity. */
  private def valueFromIdentity(dataRow: DataRow, itemIdentity: ItemIdentity): Try[Any] = {
    itemIdentity match {
      case ItemByName(name) => dataRow.get(name)
      case ItemByIndex(index) => dataRow.get(index)
    }
  }
}
