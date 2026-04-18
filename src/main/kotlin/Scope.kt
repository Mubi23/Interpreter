package org.example

class Scope(private val store: MutableMap<String,Any> = mutableMapOf(), private val parentScope: Scope? = null) {

    fun define(name: String, value: Any) {
        store[name] = value
    }

    fun set(name: String, value: Any) {
        if (store.containsKey(name)) {
            store[name] = value
        } else {
            parentScope?.set(name, value)
                ?: throw RuntimeException("Undefined variable '$name'")
        }
    }

    fun get(name: String): Any {
        return store.get(name)
            ?: parentScope?.get(name)
            ?: throw RuntimeException("Variable $name not found in store")
    }

    fun newChild(): Scope {
        return Scope(parentScope = this)
    }

    fun getAll(): Map<String, Any> {
        return store
    }
}