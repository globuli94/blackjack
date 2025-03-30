import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import controller.controllerComponent.{Controller, ControllerInterface}
import model.gameComponent.{Game, GameInterface}
import util.fileIOComponent.FileIOInterface
import util.fileIOComponent.JSON.FileIOJSON
import util.fileIOComponent.XML.FileIOXML

class BlackjackModule extends AbstractModule with ScalaModule {
  override def configure(): Unit =
    val game = Game()
    bind[GameInterface].toInstance(game)
    bind[ControllerInterface].to[Controller]

    val useJson = System.getProperty("fileio.json", "false").toBoolean
    if (useJson) {
      bind[FileIOInterface].to[FileIOJSON]
    } else {
      bind[FileIOInterface].to[FileIOXML]
    }
}