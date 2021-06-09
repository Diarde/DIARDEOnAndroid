package com.studiocinqo.diardeonandroid.engine3d.modelbuilder

import com.github.randomdwi.polygonclipping.BooleanOperation
import com.github.randomdwi.polygonclipping.Polygon
import com.github.randomdwi.polygonclipping.geometry.Contour
import com.github.randomdwi.polygonclipping.geometry.Point
import com.studiocinqo.diardeonandroid.engine3d.GLRenderer
import com.studiocinqo.diardeonandroid.engine3d.Vector2D
import com.studiocinqo.diardeonandroid.engine3d.Vector3D
import earcut4j.Earcut
import kotlin.math.round

class Wall(
    val renderer: GLRenderer,
    val vertices: HashMap<String, Vector3D>,
    val displacements: HashMap<String, Vector3D>
) {

    class IIDAndVector3D(val id: String, val point: Vector3D) {}

    fun addWall(
        wall: List<String>,
        windows: List<List<String>>,
        doors: List<List<String>>
    ) {

        wall.mapNotNull { vertices.get(it)?.let { point -> IIDAndVector3D(it, point) } }
            .let { wall ->
                doors.map { it.mapNotNull { vertices.get(it) } }.let { doors ->
                    windows.map { it.mapNotNull { vertices.get(it) } }.let { windows ->
                        addFace(wall.map { it.point }, windows, doors)

                        val v1 = wall[0];
                        val v2 = wall[1];
                        val v3 = wall[2];
                        val v4 = wall[3]
                        displacements.get(v1.id)?.let { displacement1 ->
                            displacements.get(v2.id)?.let { displacement2 ->
                                val v5 = v1.point.add(displacement1)
                                val v6 = v2.point.add(displacement2)
                                val v7 = v3.point.add(displacement2)
                                val v8 = v4.point.add(displacement1)
                                listOf(v5, v6, v7, v8).let { wall ->
                                    addFace(wall, windows, doors)
                                }
                                addSlate(v1.point, v5, v8, v4.point)
                                addSlate(v1.point, v5, v6, v2.point)
                                addSlate(v3.point, v7, v8, v4.point)
                                addSlate(v2.point, v6, v7, v3.point)

                                getDisplacementAlongNormal(
                                    v1.point, v2.point, v4.point,
                                    displacement1
                                ).let { displacement ->

                                    windows.forEach { window ->
                                        window.forEachIndexed { index, vec1 ->
                                            window.get((index + 1) % window.size).let { vec2 ->
                                                val vec3 = vec2.add(displacement)
                                                val vec4 = vec1.add(displacement)
                                                addSlate(vec1, vec2, vec3, vec4)
                                            }
                                        }
                                    }

                                    doors.forEach { door ->
                                        door.forEachIndexed { index, vec1 ->
                                            door.get((index + 1) % door.size).let { vec2 ->
                                                val vec3 = vec2.add(displacement)
                                                val vec4 = vec1.add(displacement)
                                                addSlate(vec1, vec2, vec3, vec4)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

    }

    private fun addFace(
        wall: List<Vector3D>,
        windows: List<List<Vector3D>>,
        doors: List<List<Vector3D>>
    ) {
        if (wall.size != 4) return

        val v1 = wall[0]
        val v2 = wall[1]
        val v3 = wall[2]
        val v4 = wall[3]
        val b1 = v2.subs(v1).normalise()
        val e2 = v4.subs(v1).normalise()
        val e3 = e2.cross(b1).normalise()
        val e1 = e2.cross(e3).run { scale(dot(b1)) }.normalise()

        val projector = { v: Vector3D ->
            v.subs(v1).run {
                Vector2D(dot(e1), dot(e2))
            }
        }
        val composer = { v: Vector2D -> v1.add(e1.scale(v.X)).add(e2.scale(v.Y)) }

        val _wall = Polygon.from(Contour(wall.map {
            projector(it).run {
                Point(discretize(X), discretize(Y))
            }
        }))
        val _windows = windows.filter { window -> window.size > 0 }.map { window ->
            Polygon.from(Contour(window.map {
                projector(it).run {
                    Point(discretize(X), discretize(Y))
                }
            }))
        }
        val _doors = doors.filter { door -> door.size > 0 }.map { door ->
            Polygon.from(Contour(door.map {
                projector(it).run {
                    Point(discretize(X), discretize(Y))
                }
            }))
        }

        doClipping(_wall, _windows, _doors).let { polygon ->
            doEarcutTriangulation(polygon, composer, Vector3D(e3.X, e3.Y, e3.Z)).run {
                renderer.run {
                    com.studiocinqo.diardeonandroid.engine3d.Polygon.Polygon.Texture(
                        textureMap.Wall,
                        vertices.toFloatArray(),
                        indices.toShortArray(),
                        uvs.toFloatArray(),
                        normals.toFloatArray()
                    ).let {
                        addPolygon(it)
                    }
                }
            }
        }
    }

    private fun doClipping(wall: Polygon, doors: List<Polygon>, windows: List<Polygon>): Polygon {
        val clipper1 = { x: Polygon -> BooleanOperation.INTERSECTION(wall, x) }
        val clipper2 = Clipper(wall.contour(0).points.map { it.run { doubleArrayOf(x, y) } })
        fun intersect(p: Polygon): Polygon {
            try {
                return clipper1(p)
            } catch (e: Exception) {
                return clipper2.clipPolygon(
                    p.contour(0).points.map { it.run { doubleArrayOf(x, y) } }).let {
                    Polygon.from(it.toTypedArray())
                }
            }
        }

        fun differ(a: Polygon, b: Polygon): Polygon {
            try {
                return BooleanOperation.DIFFERENCE(a, b)
            } catch (e: Exception) {
                a.addContour(b.contour(0)); return a
            }
        }

        return mutableListOf(wall).apply {
            addAll(windows)
            addAll(doors)
        }.run {
            reduce { a, b ->
                intersect(b).let {
                    differ(a, it)
                }
            }
        }
    }

    private fun doEarcutTriangulation(
        polygon: Polygon, composer: (Vector2D) -> Vector3D,
        normal: Vector3D
    ): ITriangles {
        val vertices = polygon.contours.flatMap { contour ->
            contour.points.map { point ->
                Vector2D(point.x, point.y)
            }
        }
        val indices = polygon.contours.fold<Contour, MutableList<Int>>(
            mutableListOf(0),
            { a, b ->
                a.apply { add(last() + b.points.size) }
            }).drop(1).dropLast(1)

        return Earcut.earcut(
            vertices.flatMap { listOf(round(it.X * 100), round(it.Y * 100)) }.toDoubleArray(),
            indices.toIntArray(), 2
        ).let { indices ->
            val _uvs = vertices.flatMap {
                listOf(it.X.toFloat() / 2f, it.Y.toFloat() / 2f)
            }
            val _vertices = vertices.flatMap {
                composer(it).run {
                    listOf(X.toFloat() / 10f, Y.toFloat() / 10f, -Z.toFloat() / 10f)
                }
            }
            val _indices = indices.map { x -> x.toShort() }
            val _normals = vertices.flatMap {
                listOf(
                    normal.X.toFloat(),
                    normal.Y.toFloat(), normal.Z.toFloat()
                )
            }
            return ITriangles(_vertices, _uvs, _normals, _indices)
        }

    }

    private fun addSlate(v1: Vector3D, v2: Vector3D, v3: Vector3D, v4: Vector3D) {

        val b1 = v2.subs(v1).normalise()
        val e2 = v4.subs(v1).normalise()
        val e3 = e2.cross(b1).normalise()
        val e1 = e2.cross(e3).normalise()

        val projector = { v: Vector3D ->
            v.subs(v1).run {
                Vector2D(dot(e1), dot(e2))
            }
        }

        listOf(v1, v2, v3, v4).let { vertices ->
            Earcut.earcut(
                vertices.flatMap { projector(it).run { listOf(discretize(X), discretize(Y)) } }.toDoubleArray(),
                intArrayOf(), 2
            ).let { indices ->
                val normals = vertices.flatMap {
                    listOf(
                        e3.X.toFloat(),
                        e3.Y.toFloat(), e3.Z.toFloat()
                    )
                }
                renderer.run {
                    com.studiocinqo.diardeonandroid.engine3d.Polygon.Polygon.Color(
                        vertices.flatMap {
                            it.run {
                                listOf(X.toFloat() / 10f, Y.toFloat() / 10f, -Z.toFloat() / 10f)
                            }
                        }.toFloatArray(),
                        indices.map { it.toShort() }.toShortArray(),
                        normals.toFloatArray()
                    ).let { addPolygon(it) }
                }

            }
        }
    }

    private fun getDisplacementAlongNormal(
        v1: Vector3D, v2: Vector3D, v3: Vector3D,
        displacement: Vector3D
    ): Vector3D {

        val e1 = v2.subs(v1)
        val e2 = v3.subs(v1)
        return e1.cross(e2).normalise().run {
            scale(dot(displacement))
        }
    }

    private fun discretize(x: Double): Double {
        return (round(x * 1000.0) / 1000.0)
    }
}


class ITriangles(
    val vertices: List<Float>,
    val uvs: List<Float>,
    val normals: List<Float>,
    val indices: List<Short>
) {}