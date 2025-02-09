package me.padej.displayAPI.utils

import org.bukkit.Location
import kotlin.math.max
import kotlin.math.min

class Segment // Конструктор для создания отрезка с двумя точками
    (// Получить точку A
    private val pointA: Location, // Получить точку B
    private val pointB: Location
) {
    // Метод для вычисления расстояния между точками A и B
    fun length(): Double {
        return pointA.distance(pointB)
    }

    val midpoint: Location
        // Метод для получения координат точек на отрезке
        get() {
            val midX = (pointA.x + pointB.x) / 2
            val midY = (pointA.y + pointB.y) / 2
            val midZ = (pointA.z + pointB.z) / 2
            return Location(pointA.world, midX, midY, midZ)
        }

    val minMaxPoints: Array<Location>
        // Метод для получения минимальной и максимальной точки отрезка
        get() {
            val minX = min(pointA.x, pointB.x)
            val maxX = max(pointA.x, pointB.x)
            val minY = min(pointA.y, pointB.y)
            val maxY = max(pointA.y, pointB.y)
            val minZ = min(pointA.z, pointB.z)
            val maxZ = max(pointA.z, pointB.z)

            val minPoint = Location(pointA.world, minX, minY, minZ)
            val maxPoint = Location(pointA.world, maxX, maxY, maxZ)

            return arrayOf(minPoint, maxPoint)
        }

    // Пример метода для нахождения точки на отрезке по заданному параметру t (где t от 0 до 1)
    // t = 0 — это точка A, t = 1 — это точка B
    fun getPointOnSegment(t: Double): Location {
        val x = pointA.x + (pointB.x - pointA.x) * t
        val y = pointA.y + (pointB.y - pointA.y) * t
        val z = pointA.z + (pointB.z - pointA.z) * t
        return Location(pointA.world, x, y, z)
    }
}



