package io.wollinger.webgame

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.Image

open class Entity(val position: Vector2 = Vector2()) {
    open fun update(input: Input, delta: Double, coll: List<Rectangle>, onHit: (Rectangle) -> Unit) {

    }

    open fun draw(ctx: CanvasRenderingContext2D, screenX: Double, size: Double) {

    }
}

class Player(position: Vector2): Entity(position) {
    private val mario = Image().apply { src = "/img/mario.png" }
    private val speed = 8.0
    private val gravity = 30.0
    private val bbox = Rectangle(0.0, 0.0, 1.0, 1.0) //Gotta change this to not be one full block
    private val effectiveBbox = Rectangle()
    val velocity = Vector2()
    var grounded = false

    override fun update(input: Input, delta: Double, coll: List<Rectangle>, onHit: (Rectangle) -> Unit) {
        println("Calculating physics with ${coll.size} boxes!")
        effectiveBbox.from(position.x, position.y, bbox.width, bbox.height)

        if(grounded) velocity.y = 0.0

        if(input.isPressed(" ") && grounded) velocity.y -= 16.0
        grounded = false

        if(input.isPressed("d")) velocity.x = speed
        else if(input.isPressed("a")) velocity.x = -speed
        else velocity.x = 0.0
        velocity.y += gravity * delta

        fun precisionStep(value: Double, precision: Int, action: (Double) -> Unit) {
            repeat(precision) {
                action.invoke(value / precision)
            }
        }

        var hitHead: Rectangle? = null
        precisionStep(value = velocity.y * delta, precision = 20) { vY ->
            effectiveBbox.y += vY
            val cY = coll.any { it.intersects(effectiveBbox) }
            if(!cY) {
                position.y = effectiveBbox.y
            } else {
                grounded = coll.any { it.intersects(Rectangle(position.x, position.y + 0.1F, bbox.width, bbox.height)) }
                val hit = coll.firstOrNull() { it.intersects(Rectangle(position.x, position.y - 0.01F, bbox.width, bbox.height)) }
                if(hit != null) {
                    hitHead = hit
                    velocity.y = 0.0
                }
            }
        }

        precisionStep(value = velocity.x * delta, precision = 20) { vX ->
            effectiveBbox.x += vX
            effectiveBbox.y = position.y
            val cX = coll.any { it.intersects(effectiveBbox) }
            if(!cX) position.x = effectiveBbox.x
        }

        hitHead?.let(onHit)
    }

    override fun draw(ctx: CanvasRenderingContext2D, screenX: Double, size: Double) {
        ctx.drawImage(mario, (screenX + position.x) * size, position.y * size, size, size,)
    }
}

