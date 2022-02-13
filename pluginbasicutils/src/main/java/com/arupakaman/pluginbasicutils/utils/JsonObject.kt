package com.arupakaman.pluginbasicutils.utils

import org.json.JSONObject

internal class JsonObject() : JSONObject() {

    internal constructor(json: JsonObject.() -> Unit) : this() {
        this.json()
    }

    infix fun <T> String.to(value: T) {
        put(this, value)
    }

}

operator fun <T> T.invoke(block:T.()->Unit) = block()