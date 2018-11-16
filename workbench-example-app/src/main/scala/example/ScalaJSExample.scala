package example
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import org.scalajs.dom.html

import scala.scalajs.js
import scala.util.Random

case class Point(x: Int, y: Int){
  def +(p: Point) = Point(x + p.x, y + p.y)
  def /(d: Int) = Point(x / d, y / d)
}

@JSExport
object ScalaJSExample {
  @JSExport
  def main(canvas: html.Canvas): Unit = {
    /*setup*/
    val renderer = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    canvas.width=canvas.parentElement.clientWidth
    canvas.height = 400
    renderer.font = "50px sans-serif"
    renderer.textAlign = "center"
    renderer.textBaseline = "middle"

    /*variables*/
    val obstacleGap = 200
    val holeSize = 50
    val gravity = 0.1

    var playerY = canvas.height / 2.0
    var playerV = 0.0

    var dead = 0
    var frame = -50
    val obstacles = collection.mutable.Queue.empty[Int]
    var point = 0

    /*Game Logic*/

    def runLive() = {
      frame +=2

      // Create new obstacles, or kill old ones as necessary
      if(frame >= 0 && frame % obstacleGap == 0){
        obstacles.enqueue(Random.nextInt(canvas.height - 2 * holeSize) + holeSize)
      }
      if(obstacles.length > 7){
        obstacles.dequeue()
        frame -= obstacleGap
      }
      // Apply physics
      playerY = playerY + playerV
      playerV = playerV + gravity

      renderer.fillStyle = "darkblue"
      for((holeY,i) <- obstacles.zipWithIndex){
        // Where each obstacle appears depends on what frame it is.
        // This is what keeps the obstacles moving to the left as time passes.
        val holeX = i * obstacleGap - frame + canvas.width
        renderer.fillRect(holeX, 0, 5, holeY - holeSize)
        renderer.fillRect(
          holeX, holeY + holeSize, 5, canvas.height - holeY - holeSize
        )

        // Kill the player if he hits some obstacle
        if (math.abs(holeX - canvas.width/2) < 5 &&
          math.abs(holeY - playerY) > holeSize){
          dead = 50
        }
      }
      // Render player
      renderer.fillStyle = "darkgreen"
      renderer.fillRect(canvas.width / 2 - 5, playerY - 5, 10, 10)
      // Check for out-of-bounds player
      if (playerY < 0 || playerY > canvas.height){
        dead = 50
      }

    }

    def runDead(){
      playerY = canvas.height / 2
      playerV = 0
      frame = -50
      obstacles.clear()
      dead = -1
      renderer.fillStyle = "darkred"
      renderer.fillText("Game Over", canvas.width/2, canvas.height / 2)

    }

    /*code*/
    /* renderer.fillStyle="black"*/
    def run() = {
      renderer.clearRect(0, 0, canvas.width, canvas.height)
      if (dead > 0) runDead()
      else runLive()
    }

    dom.window.setInterval(run _, 20)

    canvas.onclick = (e: dom.MouseEvent) => {
      playerV -= 5
    }

  }
}
