package io.wollinger.webgame

data class Rectangle(
    var x: Double,
    var y: Double,
    var width: Double,
    var height: Double
) {
    fun intersects(other: Rectangle): Boolean {
        return !(
                ((y + height) < (other.y)) ||
                        (y > (other.y + other.height)) ||
                        ((x + width) < other.x) ||
                        (x > (other.x + other.width))
                );
    }
}
