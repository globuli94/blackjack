import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

import controller.controllerComponent.{ControllerInterface, Controller}
import model.gameComponent.{GameInterface, Game}

class BlackjackModule extends AbstractModule with ScalaModule {
  override def configure(): Unit =
    val game = Game()
    bind[GameInterface].toInstance(game)
    bind[ControllerInterface].to[Controller]
}
