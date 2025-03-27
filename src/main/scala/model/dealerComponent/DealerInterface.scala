package model.dealerComponent

import model.gameComponent.GameState

trait DealerInterface {
  def getHand: Hand
  def getState: DealerState
}
