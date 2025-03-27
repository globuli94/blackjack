package model.dealerComponent

import model.gameComponent.GameState
import model.handComponent.HandInterface

trait DealerInterface {
  def getHand: HandInterface
  def getState: DealerState
}
