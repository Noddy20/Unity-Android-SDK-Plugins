package com.arupakaman.pluginpermissionhelper.utils

import org.json.JSONObject


class JsonObject() : JSONObject() {

    constructor(json: JsonObject.() -> Unit) : this() {
        this.json()
    }

    infix fun <T> String.to(value: T) {
        put(this, value)
    }
}

fun emptyJson() = JsonObject()