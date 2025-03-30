package model.playerComponent

import model.handComponent.HandInterface

trait PlayerInterface {
  def getName: String
  def getHand: HandInterface
  def getMoney: Int
  def getBet: Int
  def getState: PlayerState
}
