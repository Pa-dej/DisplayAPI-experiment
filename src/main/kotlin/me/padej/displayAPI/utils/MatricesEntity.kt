package me.padej.displayAPI.utils

import org.bukkit.util.Transformation
import org.bukkit.util.Vector
import org.joml.Matrix4f
import org.joml.Vector3f

class MatricesEntity {
    companion object {

        @JvmStatic
        fun transformationToMatrix4f(transformation: Transformation): Matrix4f {
            val matrix = Matrix4f().identity()
            val translation = transformation.translation
            val rotation = transformation.leftRotation
            val scale = transformation.scale

            matrix.translate(translation.x(), translation.y(), translation.z())
            matrix.rotate(rotation)
            matrix.scale(scale.x(), scale.y(), scale.z())

            return matrix
        }

        @JvmStatic
        fun intersectsVectorWithMatrix(
            origin: Vector3f,
            direction: Vector3f,
            matrix: Matrix4f
        ): Boolean {
            val invMatrix = Matrix4f(matrix).invert()

            // Переносим начальную точку и направление в пространство матрицы
            val transformedOrigin = origin.mulPosition(invMatrix)
            val transformedDirection = direction.mulDirection(invMatrix)

            // Проверяем, пересекает ли луч плоскость Z=0 в локальных координатах
            val t = -transformedOrigin.z / transformedDirection.z
            return t in 0.0..1.0
        }

        @JvmStatic
        fun vectorToSection(startVector: Vector, endVector: Vector): Pair<Vector3f, Vector3f> {
            return Pair(
                Vector3f(startVector.x.toFloat(), startVector.y.toFloat(), startVector.z.toFloat()),
                Vector3f(endVector.x.toFloat(), endVector.y.toFloat(), endVector.z.toFloat())
            )
        }

        @JvmStatic
        fun findInteraction(
            section: Pair<Vector3f, Vector3f>,
            sectionMatrix: Matrix4f,
            planeLocation: Pair<Vector3f, Vector3f>,
            planeMatrix: Matrix4f
        ): Boolean {
            val (start, end) = section

            // Преобразуем точки отрезка в пространство плоскости
            val invMatrix = Matrix4f(planeMatrix).invert()
            val startTransformed = start.mulPosition(invMatrix)
            val endTransformed = end.mulPosition(invMatrix)

            // Проверяем, находятся ли точки по разные стороны плоскости (z=0 в локальных координатах)
            val startZ = startTransformed.z
            val endZ = endTransformed.z

            return startZ * endZ < 0 // Если знаки разные, значит пересекает
        }

        @JvmStatic
        fun isPointBetween(point: Vector3f, start: Vector3f, end: Vector3f, matrix: Matrix4f?): Boolean {
            // Обратная матрица для преобразования в локальное пространство
            val invMatrix = Matrix4f(matrix).invert()


            // Перенос точек в пространство матрицы
            val localPoint = point.mulPosition(invMatrix, Vector3f())
            val localStart = start.mulPosition(invMatrix, Vector3f())
            val localEnd = end.mulPosition(invMatrix, Vector3f())


            // Проверка, лежит ли точка между start и end
            return isBetween(localPoint, localStart, localEnd)
        }

        private fun isBetween(point: Vector3f, start: Vector3f, end: Vector3f): Boolean {
            // Вектор от start до end
            val segment = Vector3f(end).sub(start)
            val pointVec = Vector3f(point).sub(start)

            val segmentLengthSquared = segment.lengthSquared()
            val dotProduct = pointVec.dot(segment)

            return dotProduct in 0.0..segmentLengthSquared.toDouble()
        }
    }
}