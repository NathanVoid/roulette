sealed trait Item {
  def name: String
}

case object Lemonade extends Item {
  val name = "Lemonade"
}

case object Sherbet extends Item {
  val name = "Sherbet"
}

case object Blowtorch extends Item {
  val name = "Blowtorch"
}

case object GoldCoin extends Item {
  val name = "GoldCoin"
}