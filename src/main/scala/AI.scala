// src/main/scala/AI.scala

class AI extends Player("Smart AI") {

  def decideAction(currentBullet: Bullet, opponent: Player, revolver: Revolver): String = {
    // Use GoldCoin to reveal the bullet if available
    if (useItemIfAvailable(GoldCoin)) {
      println(s"$name uses GoldCoin to reveal the bullet.")
      println(s"Revealed bullet is: ${currentBullet.getClass.getSimpleName.replace("$", "")}")
    }

    currentBullet match {
      case Blank =>
        if (health < 3 && useItemIfAvailable(Sherbet)) {
          println(s"$name uses Sherbet to heal.")
          restoreHealth(1)
          "heal"
        } else {
          println(s"$name decides to shoot itself (blank bullet).")
          "shoot_self"
        }

      case Live =>
        if (useItemIfAvailable(Lemonade)) {
          println(s"$name uses Lemonade to skip the live bullet.")
          revolver.pullTrigger()
          "skip"
        } else if (opponent.health < health) {
          if (useItemIfAvailable(Blowtorch)) {
            println(s"$name uses Blowtorch for extra damage!")
            "shoot_opponent_blowtorch"
          } else {
            println(s"$name decides to shoot the player (you).")
            "shoot_opponent"
          }
        } else if (health <= 2 && useItemIfAvailable(Sherbet)) {
          println(s"$name heals using Sherbet.")
          restoreHealth(1)
          "heal"
        } else {
          println(s"$name shoots the player (default strategy).")
          "shoot_opponent"
        }
    }
  }

  private def useItemIfAvailable(item: Item): Boolean = {
    if (hasItem(item)) {
      useItem(item)
    } else {
      false
    }
  }
}
