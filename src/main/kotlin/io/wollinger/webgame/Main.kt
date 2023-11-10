package io.wollinger.webgame

import kotlinx.browser.window
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.KeyboardEvent

fun main() {
    val input = Input()
    val callback : (Event) -> Unit = {
        if(it is KeyboardEvent)
            input.handle(it)
    }
    window.addEventListener(type = "keydown", options = false, callback = callback)
    window.addEventListener(type = "keyup", options = false, callback = callback)

    dl<Level>("/json/level0.json") {
        val canvas = id<HTMLCanvasElement>("canvas")
        val ctx = (canvas.getContext("2d") as CanvasRenderingContext2D)
        Engine(canvas, ctx, it, input)
    }
}