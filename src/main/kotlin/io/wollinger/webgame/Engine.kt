package io.wollinger.webgame

import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Image

class Engine(
    private val canvas: HTMLCanvasElement,
    private val ctx: CanvasRenderingContext2D,
    private val level: Level,
    private val input: Input
) {
    private var screenX = 0.0
    private val block1 = Image().apply { src = "/img/block.png" }
    private val block2 = Image().apply { src = "/img/block2.png" }
    private val lucky = Image().apply { src = "/img/lucky.png" }

    private val objects = ArrayList<Entity>()

    private val fpsCounter = FPSCounter()

    val levelCollision = ArrayList<Rectangle>()

    private val useLevelData = ArrayList<ArrayList<Char>>()

    init {
        window.requestAnimationFrame(::loop)
        objects.add(Player(Vector2(1.0, 1.0)))
        level.data.forEachIndexed { index1, line ->
            val l = ArrayList<Char>()
            useLevelData.add(l)
            line.forEachIndexed { index, c ->
                l.add(c)
            }
        }
        updateLevelCollision()
    }

    fun updateLevelCollision() {
        levelCollision.clear()
        useLevelData.forEachIndexed { y, parts ->
            parts.forEachIndexed { x, part ->
                if(part != '0')
                levelCollision.add(Rectangle(x.toDouble(), y.toDouble(), 1.0, 1.0))
            }
        }
    }

    private val distanceCheckVector = Vector2()
    private val maxDst = 3.0
    private fun update(delta: Double) {
        objects.forEach { entity ->
            entity.update(input, delta, levelCollision.filter {
                distanceCheckVector.set(x = it.x, y = it.y)
                distanceCheckVector.distance(entity.position) < maxDst
            }) {
                useLevelData.get(it.y.toInt()).set(it.x.toInt(), '0')
                updateLevelCollision()
            }
        }
        val player = (objects.first() as Player)
        val playerPixelX = player.position.x * size()
        val pure = playerPixelX - (screenX * size())
    }

    fun size() = canvas.height / 16.0

    private fun draw() {
        if(canvas.width != window.innerWidth || canvas.height != window.innerHeight) {
            canvas.width = window.innerWidth
            canvas.height = window.innerHeight
            ctx.imageSmoothingEnabled = false
        }
        val size = size()
        //Size of one sprite

        //Fill background
        ctx.fillStyle = "#9290ff"
        ctx.fillRect(0.0, 0.0, window.innerWidth.toDouble(), window.innerHeight.toDouble())

        //Draw level
        useLevelData.forEachIndexed { index, parts ->
            parts.forEachIndexed { index2, char ->
                val draw = when(char) {
                    '1' -> block1
                    '2' -> block2
                    '3' -> lucky
                    else -> null
                }
                draw?.let { ctx.drawImage(draw, index2 * size + (screenX * size), index * size, size, size) }
            }
        }

        objects.forEach {
            it.draw(ctx, screenX, size)
        }
        if(input.isPressed("f")) {
            levelCollision.forEach {
                ctx.strokeStyle = "black"
                ctx.rect(it.x * size, it.y * size, it.width * size, it.height * size)
            }
            ctx.stroke()
        }

        //Draw fps
        val player = (objects.first() as Player)
        ctx.fillStyle = "black"
        ctx.font = "${size}px Roboto Mono"
        ctx.fillText("FPS: ${fpsCounter.getString()}", 0.0, size)
        ctx.fillText("Velocity: ${player.velocity}", 0.0, size * 2)
        ctx.fillText("Grounded: ${player.grounded}", 0.0, size * 3)

        fpsCounter.frame()
    }

    var lastRender = 0.0
    private fun loop(timestamp: Double) {
        val delta = (timestamp - lastRender) / 1000
        update(delta.coerceIn(0.0, 0.1))
        draw()

        lastRender = timestamp
        window.requestAnimationFrame(::loop)
    }
}