import com.google.inject.*
import controller.ControllerInterface
import controller.controllerComponent.Controller
import model.ModelInterface
import model.modelComponent.Game
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BlackjackModuleTest extends AnyFlatSpec with Matchers {

  // Create a Guice injector to test the bindings
  val injector: Injector = Guice.createInjector(new BlackjackModule)

  "BlackjackModule" should "bind GameInterface to an instance of Game" in {
    val game = injector.getInstance(classOf[ModelInterface])
    game shouldBe a[Game]  // Verifying that the instance is of type Game
  }

  it should "bind ControllerInterface to Controller" in {
    val controller = injector.getInstance(classOf[ControllerInterface])
    controller shouldBe a[Controller]  // Verifying that the instance is of type Controller
  }

}

