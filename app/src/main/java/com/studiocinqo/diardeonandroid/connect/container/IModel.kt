package com.studiocinqo.diardeonandroid.connect.container

import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class IModel(
    val id: String,
    val date: Date,
    val data: IData
) {

    class IData(
        val cameras: List<Any>,
        val model: IModel, val floorplan: IFloorplan
    ) {

        class IModel(
            val ground: List<String>,
            val vertices: List<IVertex>,
            val faces: List<IFace>
        ) {

            class IVertex(val id: String, val point: IPoint) {

                class IPoint(
                    val x: Double, val y: Double, val z: Double
                ) {

                    companion object {

                        fun getIPoint(obj: JSONObject?): IPoint? {
                            return JWrap(obj)?.let { obj ->
                                obj.getDouble("x")?.let { _x ->
                                    obj.getDouble("y")?.let { _y ->
                                        obj.getDouble("z")?.let { _z ->
                                            IPoint(_x, _y, _z)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    override fun toString(): String{
                        return "{x: $x, y: $y, z: $z}"
                    }

                }


                companion object {
                    fun getVertex(obj: JSONObject?): IVertex? {
                        return JWrap(obj)?.let { obj ->
                            obj.getString("id")?.let { id ->
                                IPoint.getIPoint(obj.getObject("point"))?.let { point ->
                                    IVertex(id, point)
                                }
                            }
                        }
                    }
                }

                override fun toString(): String{
                    return "{id: ${id}, point: ${point.toString()}}"
                }

            }

            class IFace(
                val id: String,
                val vertices: List<String>,
                val doors: List<List<String>>,
                val windows: List<List<String>>
            ) {

                companion object {
                    fun getFace(obj: JSONObject?): IFace? {
                        return JWrap(obj)?.let { obj ->
                            obj.getString("id")?.let { id ->
                                JAWrap(obj.getArray("vertices")).getStringList()?.let { vertices ->
                                    val doors =
                                        JAWrap(obj.getArray("doors")).getArrayList().map { door ->
                                            JAWrap(door).getStringList()
                                        }
                                    val windows = JAWrap(obj.getArray("windows")).getArrayList()
                                        .map { window ->
                                            JAWrap(window).getStringList()
                                        }
                                    IFace(id, vertices, doors, windows)
                                }
                            }
                        }
                    }
                }

                override fun toString(): String{
                    return "{id: ${id}, vertices: ${vertices.toString()}, " +
                            "doors: ${doors.toString()}, windows: ${windows.toString()}}"
                }

            }


            companion object {
                fun getModel(obj: JSONObject?): IModel? {
                    return JWrap(obj)?.let { obj ->
                        JAWrap(obj.getArray("ground")).getStringList()?.let { ground ->
                            JAWrap(obj.getArray("vertices")).getCustomList<IVertex> {
                                IVertex.getVertex(it)
                            }?.let { vertices ->
                                JAWrap(obj.getArray("faces")).getCustomList<IFace> {
                                    IFace.getFace(it)
                                }?.let { faces ->
                                    IModel(ground, vertices, faces)
                                }
                            }
                        }
                    }
                }
            }

            override fun toString(): String{
                return "{ground: ${ground.toString()}, vertices: ${vertices.toString()}, " +
                        "faces: ${faces.toString()}}"
            }

        }

        class IFloorplan(
            val deltaZ: Double,
            val vertices: List<IPointAndID>,
            val displacements: List<IPointAndID>,
            val walls: List<IWall>
        ) {

            class IPoint2D(val x: Double, val y: Double) {

                companion object {

                    fun getPoint(obj: JSONObject?): IPoint2D? {
                        return JWrap(obj).let { obj ->
                            obj.getDouble("x")?.let { x ->
                                obj.getDouble("y")?.let { y ->
                                    IPoint2D(x, y)
                                }
                            }
                        }
                    }
                }

                override fun toString(): String{
                    return "{x: ${x}, y: ${y}}"
                }

            }

            class IPointAndID(
                val id: String,
                val point: IPoint2D,
                private val key: String
            ) {

                companion object {

                    fun getIDPoint(obj: JSONObject?, key: String = "point"): IPointAndID? {
                        return JWrap(obj).let { obj ->
                            obj.getString("id")?.let { id ->
                                IPoint2D.getPoint(obj.getObject(key))?.let { point ->
                                    IPointAndID(id, point, key)
                                }
                            }
                        }
                    }
                }

                override fun toString(): String{
                    return "{id: ${id}, ${key}: ${point.toString()}}"
                }

            }


            class IWall(
                val wall: IIDPair,
                val sections: List<IIDPair>,
                val doors: List<IIDPair>,
                val windows: List<IIDPair>
            ) {

                class IIDPair(
                    val id1: String,
                    val id2: String
                ) {


                    companion object {
                        fun getIDDPair(array: JSONArray?): IIDPair? {
                            return array?.getString(0)?.let { id1 ->
                                array?.getString(1)?.let { id2 ->
                                    IIDPair(id1, id2)
                                }
                            }
                        }
                    }

                    override fun toString(): String{
                        return listOf(id1, id2).toString()
                    }

                }


                companion object {
                    fun getWall(obj: JSONObject?): IWall? {
                        return JWrap(obj)?.let { obj ->
                            IIDPair.getIDDPair(obj.getArray("wall"))?.let { wall ->
                                val sections =
                                    JAWrap(obj.getArray("sections")).getArrayList().mapNotNull {
                                        IIDPair.getIDDPair(it)
                                    }
                                val doors =
                                    JAWrap(obj.getArray("doors")).getArrayList().mapNotNull {
                                        IIDPair.getIDDPair(it)
                                    }
                                val windows =
                                    JAWrap(obj.getArray("windows")).getArrayList().mapNotNull {
                                        IIDPair.getIDDPair(it)
                                    }
                                IWall(wall, sections, doors, windows)
                            }
                        }
                    }
                }

                override fun toString(): String{
                    return "{wall: ${wall.toString()}, sections: ${sections.toString()}, " +
                            "doors: ${doors.toString()}, windows: ${windows.toString()}}"
                }

            }


            companion object {
                fun getFloorplan(obj: JSONObject?): IFloorplan? {
                    return JWrap(obj)?.let { obj ->
                        obj.getDouble("deltaZ")?.let { deltaZ ->
                            val vertices = JAWrap(obj.getArray("vertices")).getCustomList {
                                IPointAndID.getIDPoint(it)
                            }
                            val displacements =
                                JAWrap(obj.getArray("displacements")).getCustomList {
                                    IPointAndID.getIDPoint(it, "vector")
                                }
                            val walls =
                                JAWrap(obj.getArray("walls")).getCustomList {
                                    IWall.getWall(it)
                                }
                            IFloorplan(
                                deltaZ,  vertices, displacements, walls
                            )
                        }
                    }
                }
            }

            override fun toString(): String{
                return "{deltaZ: ${deltaZ}, vertices: ${vertices.toString()}, " +
                        "displacements: ${displacements.toString()}, " +
                        "walls: ${walls.toString()}}"
            }

        }

        companion object {
            fun getModel(obj: JSONObject?): IData? {
                return JWrap(obj)?.let { obj ->
                    IModel.getModel(obj.getObject("model"))?.let { model ->
                        IFloorplan.getFloorplan(obj.getObject("floorplan"))?.let { floorplan ->
                            IData(listOf<Any>(), model, floorplan)
                        }
                    }
                }
            }
        }

        override fun toString(): String{
            return "{model: ${model.toString()}, floorplan: ${floorplan.toString()}}"
        }

    }


    companion object {

        fun getModel(obj: JSONObject): IModel? {
            return JWrap(obj).let { obj ->
                obj.getString("_id")?.let { id ->
                    obj.getDate("date")?.let { date ->
                        IData.getModel(obj.getObject("data"))?.let { data ->
                            IModel(id, date, data)
                        }
                    }
                }
            }
        }
    }

    override fun toString(): String{
        return "{ _id: ${id}, date: ${JWrap.dateToString(date)}, data: ${data.toString()}}"
    }
}



