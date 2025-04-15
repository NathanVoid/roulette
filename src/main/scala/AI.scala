// src/main/scala/AI.scala

class AI extends Player("Smart AI") {

  def decideAction(currentBullet: Bullet, opponent: Player, revolver: Revolver): String = {
    // Use GoldCoin if available to identify bullet
    if (hasItem(GoldCoin)) {
      println(s"$name uses GoldCoin to reveal the bullet.")
      useItem(GoldCoin)
      println(s"Revealed bullet is: ${currentBullet.getClass.getSimpleName.replace("$", "")}")
    }

    currentBullet match {
      case Blank =>
        if (health < 3 && hasItem(Sherbet)) {
          println(s"$name uses Sherbet to heal.")
          useItem(Sherbet)
          restoreHealth(1)
          "heal"
        } else {
          println(s"$name decides to shoot itself (blank bullet).")
          "shoot_self"
        }

      case Live =>
        if (hasItem(Lemonade)) {
          println(s"$name uses Lemonade to skip the live bullet.")
          useItem(Lemonade)
          revolver.pullTrigger()
          "skip"
        } else if (opponent.health < health) {
          println(s"$name decides to shoot the player (you).")
          if (hasItem(Blowtorch)) {
            println(s"$name uses Blowtorch for extra damage!")
            useItem(Blowtorch)
            "shoot_opponent_blowtorch"
          } else {
            "shoot_opponent"
          }
        } else if (health <= 2 && hasItem(Sherbet)) {
          println(s"$name heals using Sherbet.")
          useItem(Sherbet)
          restoreHealth(1)
          "heal"
        } else {
          println(s"$name shoots the player (default strategy).")
          "shoot_opponent"
        }
    }
  }
}
