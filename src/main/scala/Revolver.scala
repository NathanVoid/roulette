// src/main/scala/Revolver.scala

import scala.util.Random

class Revolver(var bullets: List[Bullet]) {
  def spin(): Unit = {
    bullets = Random.shuffle(bullets)
  }

  def pullTrigger(): Option[Bullet] = {
    bullets match {
      case Nil => None
      case head :: tail =>
        bullets = tail
        Some(head)
    }
  }

  def isEmpty: Boolean = bullets.isEmpty

  def remainingBullets: String = {
    val liveCount = bullets.count(_ == Live)
    val blankCount = bullets.count(_ == Blank)
    s"$liveCount live, $blankCount blank"
  }
}

object Revolver {
  def generateBullets(): List[Bullet] = {
    val totalBullets = Random.between(2, 6)
    val liveCount = Random.between(1, totalBullets)
    val blankCount = totalBullets - liveCount

    val bullets = List.fill(liveCount)(Live) ++ List.fill(blankCount)(Blank)
    Random.shuffle(bullets)
  }
}
