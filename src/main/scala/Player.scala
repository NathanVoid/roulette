// src/main/scala/Player.scala

class Player(val name: String) {
  private var _health: Int = 6
  private var _items: List[Item] = List()
  var blowtorchReady: Boolean = false

  def health: Int = _health
  def isAlive: Boolean = _health > 0
  def items: List[Item] = _items

  def takeDamage(amount: Int): Unit = {
    _health = (_health - amount).max(0)
  }

  def restoreHealth(amount: Int): Unit = {
    _health = (_health + amount).min(6)
  }

  def addItems(newItems: List[Item]): Unit = {
    _items = (_items ++ newItems).take(8) // Max 8 items
  }

  def useItem(item: Item): Boolean = {
    removeFirst(item).isDefined
  }

  def removeItem(index: Int): Option[Item] = {
    if (index >= 0 && index < _items.length) {
      val item = _items(index)
      _items = _items.patch(index, Nil, 1)
      Some(item)
    } else None
  }

  def hasItem(item: Item): Boolean = _items.contains(item)

  def showInventory(): Unit = {
    println(s"[$name Inventory]")
    _items.groupBy(identity).view.mapValues(_.size).toList
      .sortBy(_._1.name)
      .zipWithIndex
      .foreach { case ((item, count), idx) =>
        println(s"${idx + 1}. ${item.name}: $count")
      }
  }

  def showFullInventory(): Unit = {
    println(s"[$name Inventory]")
    _items.zipWithIndex.foreach { case (item, index) =>
      println(s"${index + 1}. ${item.name}")
    }
  }

  private def removeFirst(item: Item): Option[Item] = {
    val index = _items.indexOf(item)
    if (index >= 0) {
      val found = _items(index)
      _items = _items.patch(index, Nil, 1)
      Some(found)
    } else None
  }
}
