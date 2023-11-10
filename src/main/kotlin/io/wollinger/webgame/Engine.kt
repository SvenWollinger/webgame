package io.wollinger.webgame

import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Image
import kotlin.js.Date

class Engine(
    private val canvas: HTMLCanvasElement,
    private val ctx: CanvasRenderingContext2D,
    private val level: Level,
    private val input: Input
) {
    private var screenX = 0
    private val block1 = Image().apply { src = "/img/block.png" }
    private val block2 = Image().apply { src = "/img/block2.png" }
    private val lucky = Image().apply { src = "/img/lucky.png" }

    private val fpsCounter = FPSCounter()


    init {
        window.requestAnimationFrame(::loop)
    }

    private fun update(delta: Double) {
        if(input.isPressed("d")) screenX += -1
        if(input.isPressed("a")) screenX += 1

    }

    private fun draw() {
        if(canvas.width != window.innerWidth || canvas.height != window.innerHeight) {
            canvas.width = window.innerWidth
            canvas.height = window.innerHeight
            ctx.imageSmoothingEnabled = false
        }

        //Size of one sprite
        val size = window.innerHeight / 16.0

        //Fill background
        ctx.fillStyle = "#9290ff"
        ctx.fillRect(0.0, 0.0, window.innerWidth.toDouble(), window.innerHeight.toDouble())

        //Draw fps
        ctx.fillStyle = "black"
        ctx.font = "${size}px Roboto Mono"
        ctx.fillText(fpsCounter.getString(), 0.0, size)

        //Draw level
        level.data.forEachIndexed { index, line ->
            line.forEachIndexed { index2, char ->
                val draw = when(char) {
                    '1' -> block1
                    '2' -> block2
                    '3' -> lucky
                    else -> null
                }
                draw?.let { ctx.drawImage(draw, index2 * size + screenX, index * size, size, size) }
            }
        }

        fpsCounter.frame()
    }

    var lastRender = 0.0
    private fun loop(timestamp: Double) {
        val progress = timestamp - lastRender

        update(progress)
        draw()

        lastRender = timestamp
        window.requestAnimationFrame(::loop)
    }
}