import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import controller.controllerComponent.{Controller, ControllerInterface}
import model.fileIOComponent.FileIOInterface
import model.fileIOComponent.JSON.FileIOJSON
import model.fileIOComponent.XML.FileIOXML
import model.gameComponent.{Game, GameInterface}

class BlackjackModule extends AbstractModule with ScalaModule {
  override def configure(): Unit =
    val game = Game()
    bind[GameInterface].toInstance(game)
    bind[ControllerInterface].to[Controller]

    val useJson = System.getProperty("fileio.json", "false").toBoolean
    bind[FileIOInterface].to[FileIOJSON]
    
    /*
    if (useJson) {
      bind[FileIOInterface].to[FileIOJSON]
    } else {
      bind[FileIOInterface].to[FileIOXML]
    }
     */
}
