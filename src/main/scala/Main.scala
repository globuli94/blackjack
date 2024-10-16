import scala.io.StdIn.readLine
import model.Deck
import scala.collection.immutable.LazyList.cons

object Main {  
  def main(args: Array[String]): Unit = {
    var input: String = ""

    var deck = new Deck();

    while(input != "exit") {
      input = readLine();
    }
  }
}