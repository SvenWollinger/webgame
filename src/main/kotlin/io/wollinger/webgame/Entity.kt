package io.wollinger.webgame

import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.Image

open class Entity(val position: Vector2 = Vector2()) {
    open fun update(input: Input, delta: Double, coll: ArrayList<Rectangle>) {

    }

    open fun draw(ctx: CanvasRenderingContext2D, screenX: Double, size: Double) {

    }
}

class Player(position: Vector2): Entity(position) {
    private val mario = Image().apply { src = "/img/mario.png" }
    private val speed = 8.0
    private val gravity = 19.0
    private val bbox = Rectangle(0.0, 0.0, 1.0, 1.0)
    private val effectiveBbox = Rectangle()
    val velocity = Vector2()
    var grounded = false

    override fun update(input: Input, delta: Double, coll: ArrayList<Rectangle>) {
        effectiveBbox.from(position.x, position.y, bbox.width, bbox.height)

        if(grounded) velocity.y = 0.0

        if(input.isPressed(" ") && grounded) velocity.y -= 12.0
        grounded = false

        if(input.isPressed("d")) velocity.x = speed
        else if(input.isPressed("a")) velocity.x = -speed
        else velocity.x = 0.0
        velocity.y += gravity * delta

        effectiveBbox.y += velocity.y * delta
        val cY = coll.any { it.intersects(effectiveBbox) }
        if(!cY) position.y = effectiveBbox.y
        else {
            grounded = coll.any { it.intersects(Rectangle(position.x, position.y + 0.1F, bbox.width, bbox.height)) }
            if(coll.any { it.intersects(Rectangle(position.x, position.y - 0.1F, bbox.width, bbox.height)) }) velocity.y = 0.0
        }

        effectiveBbox.x += velocity.x * delta
        effectiveBbox.y = position.y//if(velocity.y > 0) 0.1F else -0.2F
        val cX = coll.any { it.intersects(effectiveBbox) }
        if(!cX) position.x = effectiveBbox.x
    }

    override fun draw(ctx: CanvasRenderingContext2D, screenX: Double, size: Double) {
        ctx.drawImage(mario, (screenX + position.x) * size, position.y * size, size, size,)
    }
}

