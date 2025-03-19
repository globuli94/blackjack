package util

trait Observer {
  def update(e: Event): Unit
}

trait Observable {
  private var subscribers: Vector[Observer] = Vector()
  def add(s: Observer): Unit = subscribers = subscribers :+ s
  //def remove(s: Observer) = subscribers = subscribers.filterNot(o => o == s)
  def notifyObservers(e: Event): Unit = subscribers.foreach(o => o.update(e))
}

enum Event {
  case Create
  case Start
  case AddPlayer
  case hitNextPlayer
  case standNextPlayer
  case doubleDown
  case Split
  case bet
  case continue
  case leavePlayer
  case invalidCommand
  case invalidBet
  case End
  case errPlayerNameExists
}