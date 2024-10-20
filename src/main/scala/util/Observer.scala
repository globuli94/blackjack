package util

trait Observer {
  def update(e: Event): Unit
}

trait Observable {
  var subscribers: Vector[Observer] = Vector()
  def add(s: Observer) = subscribers = subscribers :+ s
  //def remove(s: Observer) = subscribers = subscribers.filterNot(o => o == s)
  def notifyObservers(e: Event) = subscribers.foreach(o => o.update(e))
}

enum Event {
  case Start
  case AddPlayer
  case hitNextPlayer
  case standNextPlayer
  case bet
  case invalidCommand
  case End
}