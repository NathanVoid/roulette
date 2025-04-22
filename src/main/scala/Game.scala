// src/main/scala/Game.scala

import scala.util.Random

class Game {
  val player = new Player("You")
  val ai = new AI
  var revolver: Revolver = _
  var round = 0

  def start(): Unit = {
    println("Welcome to Russian Roulette!\n")
    gameLoop()
  }

  private def gameLoop(): Unit = {
    while (player.isAlive && ai.isAlive) {
      round += 1
      println(s"\n--- Round $round ---")
      println("=====================================")
      prepareRound()

      var continueRound = true
      var playerTurnActive = true

      while (continueRound && player.isAlive && ai.isAlive && !revolver.isEmpty) {
        println(s"\n[STATUS] Player Health: ${player.health}/6 | AI Health: ${ai.health}/6")

        while (playerTurnActive && player.isAlive && ai.isAlive && !revolver.isEmpty) {
          playerTurnActive = playerTurn()
        }

        if (ai.isAlive && !revolver.isEmpty) {
          var aiTurnActive = true
          while (aiTurnActive && ai.isAlive && player.isAlive && !revolver.isEmpty) {
            aiTurnActive = aiTurn()
          }
        }

        playerTurnActive = true
        continueRound = player.isAlive && ai.isAlive && !revolver.isEmpty
      }
    }
    endGame()
  }

  private def prepareRound(): Unit = {
    val bullets = Revolver.generateBullets()
    revolver = new Revolver(bullets)
    revolver.spin()

    val liveCount = bullets.count(_ == Live)
    val blankCount = bullets.count(_ == Blank)
    println("------")
    println(s"Bullets loaded: $liveCount live, $blankCount blank")
    println("------\n")

    val items = List(Lemonade, Sherbet, Blowtorch, GoldCoin)
    val randomPlayerItems = List.fill(2)(items(Random.nextInt(items.length)))
    val randomAIItems = List.fill(2)(items(Random.nextInt(items.length)))

    player.addItems(randomPlayerItems)
    ai.addItems(randomAIItems)

    println("You received:")
    randomPlayerItems.groupBy(identity).foreach { case (item, list) =>
      println(s"- ${item.name} x${list.size}")
    }

    println("=====================================")
    println("AI received:")
    randomAIItems.groupBy(identity).foreach { case (item, list) =>
      println(s"- ${item.name} x${list.size}")
    }
  }

  private def playerTurn(): Boolean = {
    println("\nYour turn.")
    println("Choose action:")
    println("1. Shoot yourself")
    println("2. Shoot AI")
    println("3. Use item")

    val input = scala.io.StdIn.readLine("Enter 1, 2, or 3: ")

    input match {
      case "1" =>
        val result = revolver.pullTrigger()
        result.foreach {
          case Live =>
            val damage = if (player.blowtorchReady) {
              println("You used Blowtorch! Double damage!")
              player.blowtorchReady = false
              2
            } else 1
            println("Bang! You shot yourself.")
            player.takeDamage(damage)
            return false
          case Blank =>
            println("Click! It was a blank. You get another turn.")
            return true
        }
        false

      case "2" =>
        val result = revolver.pullTrigger()
        result.foreach {
          case Live =>
            val damage = if (player.blowtorchReady) {
              println("You used Blowtorch! Double damage!")
              player.blowtorchReady = false
              2
            } else 1
            println(s"Bang! AI got hit for $damage damage.")
            ai.takeDamage(damage)
            return false
          case Blank =>
            println("Click! It was a blank. Turn ends.")
            return false
        }
        false

      case "3" =>
        player.showFullInventory()
        val indexInput = scala.io.StdIn.readLine("Enter item number to use: ")
        try {
          val index = indexInput.toInt - 1
          player.removeItem(index) match {
            case Some(item) =>
              item match {
                case Lemonade =>
                  println("You used Lemonade to fire the bullet into the air.")
                  revolver.pullTrigger().foreach {
                    case Live  => println("The fired bullet was: Live.")
                    case Blank => println("The fired bullet was: Blank.")
                  }
                case Sherbet =>
                  println("You used Sherbet to restore 1 health.")
                  player.restoreHealth(1)
                case Blowtorch =>
                  println("You used Blowtorch. Your next shot does double damage.")
                  player.blowtorchReady = true
                case GoldCoin =>
                  val currentBullet = revolver.bullets.headOption
                  currentBullet.foreach(b => println(s"The current bullet is: ${b.getClass.getSimpleName.replace("$", "")}"))
              }
            case None => println("Invalid item selection.")
          }
        } catch {
          case _: NumberFormatException => println("Invalid input.")
        }
        true

      case _ =>
        println("Invalid input.")
        true
    }
  }

  private def aiTurn(): Boolean = {
    println("\nAI's turn.")
    val currentBullet = revolver.bullets.headOption.getOrElse(return false)
    val decision = ai.decideAction(currentBullet, player, revolver)

    decision match {
      case "shoot_self" =>
        revolver.pullTrigger().foreach {
          case Live =>
            println("Bang! AI shot itself.")
            ai.takeDamage(1)
            return false
          case Blank =>
            println("Click! Blank bullet. AI gets another turn.")
            return true
        }

      case "shoot_opponent" =>
        revolver.pullTrigger().foreach {
          case Live =>
            println("Bang! You got hit.")
            player.takeDamage(1)
            return false
          case Blank =>
            println("Click! Blank bullet.")
            return false
        }

      case "shoot_opponent_blowtorch" =>
        revolver.pullTrigger().foreach {
          case Live =>
            println("Bang! You got hit hard!")
            player.takeDamage(2)
            return false
          case Blank =>
            println("Click! Blank bullet.")
            return false
        }

      case "skip" =>
        println("AI used Lemonade to fire the bullet into the air.")
        revolver.pullTrigger()
        return true

      case "heal" =>
        println("AI used Sherbet to heal.")
        return true
    }
    false
  }

  private def endGame(): Unit = {
    println("\n--- Game Over ---")
    if (player.isAlive) {
      println("You win!")
    } else {
      println("AI wins!")
    }
    println(s"Rounds survived: $round")
    println(s"Your health: ${player.health}/6")
    println(s"AI health: ${ai.health}/6")
  }
}
