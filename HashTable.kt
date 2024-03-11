package edu.ucdavis.cs.ecs036c

import kotlin.math.absoluteValue

class HashTable<K, V>(var initialCapacity: Int = 8) {
    data class HashTableEntry<K, V>(val key: K, var value: V, var deleted : Boolean = false);
    // The number of elements in the storage that exist, whether or not they are marked deleted
    internal var occupied = 0

    // The number of non-deleted elements.
    internal var privateSize = 0

    // And the internal storage array
    internal var storage: Array<HashTableEntry<K, V>?> = arrayOfNulls(initialCapacity)

    val size: Int
        get() = privateSize

    // An iterator of key/value pairs, done by using a sequence and calling yield
    // on each pair that is in the table and VALID
    operator fun iterator() : Iterator<Pair<K, V>> =
        sequence<Pair<K, V>> {
            for (item in storage) {
                if ((item != null) && (!item.deleted)) {
                    yield(item.key to item.value)
                }
            }
        }.iterator()

    override fun toString() : String = this.iterator().asSequence().joinToString(prefix="{", postfix="}",
        limit = 200) { "[${it.first}/${it.second}]" }


    // Internal resize function.  It should copy all the
    // valid entries but ignore the deleted entries.
    private fun resize(){
        occupied = 0
        privateSize = 0
        var old_storage = storage
        storage = arrayOfNulls((old_storage.size) * 2)
        for (entry in old_storage) {
            if ((entry != null) && (!entry.deleted)) {
                this[entry.key] = entry.value
            }
        }
    }

    operator fun contains(key: K): Boolean {
        var hash = key.hashCode().absoluteValue
        var ind = hash % storage.size

        while (storage[ind] != null) {
            if ((storage[ind]!!.key == key) && (!storage[ind]!!.deleted)) {
                return true
            }
            ind = (ind + 1) % storage.size
        }
        return false
    }

    // Get returns null if the key doesn't exist
    operator fun get(key: K): V? {
        var hash = key.hashCode().absoluteValue
        var ind = hash % storage.size

        while (storage[ind] != null) {
            if ((storage[ind]!!.key == key) && (!storage[ind]!!.deleted)) {
                return storage[ind]!!.value
            }
            ind = (ind + 1) % storage.size
        }
        return null
    }

    // IF the key exists just update the corresponding data.
    // If the key doesn't exist, find a spot to insert it.
    // If you need to insert into a NEW entry, resize if
    // the occupancy (active & deleted entries) is >75%
    operator fun set(key: K, value: V) {
        var hash = key.hashCode().absoluteValue
        var ind = hash % storage.size
        var bool = contains(key)

        while ((storage[ind] != null)) {
            if ((storage[ind]!!.key == key) && (!storage[ind]!!.deleted) && bool) {
                storage[ind]!!.value = value
                return
            }
            else if ((storage[ind]!!.deleted) && (!bool)) {
                storage[ind] = HashTableEntry(key, value, deleted = false)
                privateSize++
                return
            }
            else {
                ind = (ind + 1) % storage.size
            }
        }
        if (occupied > (storage.size * 0.75)) {
            resize()
            set(key, value)
            return
        }
        occupied++
        privateSize++
        storage[ind] = HashTableEntry(key, value)

    }

    // If the key doesn't exist remove does nothing
    fun remove(key: K) {
        var hash = key.hashCode().absoluteValue
        var ind = hash % storage.size

        while (storage[ind] != null) {
            if(storage[ind]!!.key == key) {
                storage[ind]!!.deleted = true
                privateSize--
                return
            }
            ind = (ind + 1) % storage.size
        }
    }

}