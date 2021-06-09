package com.studiocinqo.diardeonandroid.connect.container

class IResult<out T>(public val status: Int, public val result: T?) {

    fun onSuccess(f: (r: T) -> Unit): IResult<T> {
        result?.let{
            f(it)
        }
        return this
    }

    fun onError(f: (code: Int) -> Unit): IResult<T> {
        if(result == null){
            f(status)
        }
        return this
    }

    fun <W>map(f: (a: T) -> W?): IResult<W>{
        return IResult<W>(status, result?.let{f(result)})
    }

    companion object {
        var INTERNAL_SERVER_ERROR = IResult(500, null)
        var NOT_AUTHENTICATED = IResult(401, null)
    }

}