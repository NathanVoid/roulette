// src/main/scala/Revolver.scala

import scala.util.Random

class Revolver(private var bullets: List[Bullet]) {

  def spin(): Unit = {
    bullets = Random.shuffle(bullets)
  }

  def pullTrigger(): Option[Bullet] = bullets match {
    case head :: tail =>
      bullets = tail
      Some(head)
    case Nil => None
  }

  def isEmpty: Boolean = bullets.isEmpty

  def remainingBullets: String = {
    val (liveCount, blankCount) = bullets.foldLeft((0, 0)) {
      case ((live, blank), Live)  => (live + 1, blank)
      case ((live, blank), Blank) => (live, blank + 1)
    }
    s"$liveCount live, $blankCount blank"
  }
}

object Revolver {
  def generateBullets(): List[Bullet] = {
    val totalBullets = Random.between(2, 6)
    val liveCount = Random.between(1, totalBullets)
    val blankCount = totalBullets - liveCount

    Random.shuffle(List.fill(liveCount)(Live) ++ List.fill(blankCount)(Blank))
  }
}
