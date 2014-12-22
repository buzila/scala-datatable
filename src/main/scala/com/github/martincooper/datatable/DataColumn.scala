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

package com.github.martincooper.datatable

import scala.reflect.runtime.universe._

/**
 * Generic Column Trait.
 * Allows a collection of columns storing data of distinct types to be stored in a generic collection.
 */
trait GenericColumn extends ModifiableByIndex[Any, GenericColumn] {
  def name: String
  def data: Vector[Any]
  def columnType: Type
}

/** Strongly typed data column. */
class DataColumn[T: TypeTag](columnName: String, columnData: Iterable[T]) extends GenericColumn {
  def name = columnName
  def data = columnData.toVector
  def columnType = typeOf[T]

  /** Returns a new DataColumn[T] with the new value appended to the end. */
  override def add(value: Any): DataColumn[T] = {
    val typedValue = value.asInstanceOf[T]
    new DataColumn[T](name, data :+ typedValue)
  }

  /** Returns a new DataColumn[T] with the new value at the specified index. */
  override def replace(index: Int, value: Any): DataColumn[T] = {
    new DataColumn[T](name, VectorExtensions.replaceItem(data, index, value.asInstanceOf[T]))
  }

  /** Returns a new DataColumn[T] with the value inserted at the specified index. */
  override def insert(index: Int, value: Any): DataColumn[T] = {
    val typedValue = value.asInstanceOf[T]
    new DataColumn[T](name, VectorExtensions.insertItem(data, index, typedValue))
  }

  /** Returns a new DataColumn[T] with the value at the specified removed. */
  override def remove(index: Int): DataColumn[T] = {
    new DataColumn[T](name, VectorExtensions.removeItem(data, index))
  }

  override def toString = "Col : " + name
}

object GenericColumn {

  implicit class GenericColExt(val col: GenericColumn) extends AnyVal {

    /** Casts the generic column back to the specified type. */
    def as[T]: DataColumn[T] =
      col.asInstanceOf[DataColumn[T]]
  }
}