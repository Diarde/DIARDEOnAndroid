package com.studiocinqo.diardeonandroid.engine3d.modelbuilder

import com.studiocinqo.diardeonandroid.connect.container.IModel
import com.studiocinqo.diardeonandroid.engine3d.Vector2D
import com.studiocinqo.diardeonandroid.engine3d.Vector3D
import kotlin.math.abs
import kotlin.math.sign

class Displacements {

    class IIDAndVector2D(val id: String, val point: Vector2D){}

    companion object {

        fun getDisplacementsMap(
            model: IModel.IData.IModel,
            vertexMap: HashMap<String, Vector3D>
        ): HashMap<String, Vector3D> {

            val points = model.ground.mapNotNull { vertex ->
                vertexMap.get(vertex)?.let { v ->
                    IIDAndVector2D(vertex, Vector2D(v.X, v.Z))
                }
            }

            val outerSign = sign(points.mapIndexed { index, point ->
                val current = point.point
                val previous = points[(index - 1 + points.size) % points.size].point
                val next = points[(index + 1 + points.size) % points.size].point
                val v1 = current.subs(previous).normalise()
                val v2 = next.subs(current).normalise()
                v1.X * v2.Y - v1.Y * v2.X
            }.reduce { a, b -> a + b })

            return HashMap<String, Vector3D>().also { map ->
                points.forEachIndexed { index, point ->
                    val current = point.point
                    val previous = points[(index - 1 + points.size) % points.size].point
                    val next = points[(index + 1 + points.size) % points.size].point
                    val v1 = current.subs(previous).normalise()
                    val v2 = next.subs(current).normalise()
                    if ((v1.X == v2.X) && (v1.Y == v2.Y)) {
                        map[point.id] = Vector3D(v1.Y, 0.0, -v1.X).scale(0.15 * outerSign)
                    } else {
                        val v3 = v1.subs(v2).normalise()
                        val scale = 0.15f / abs(v3.X * v2.Y - v3.Y * v2.X);
                        map[point.id] =
                            Vector3D(v3.X, 0.0, v3.Y).scale(scale * outerSign * sign(v1.X * v2.Y - v1.Y * v2.X))
                    }
                }
            }
        }

    }

}