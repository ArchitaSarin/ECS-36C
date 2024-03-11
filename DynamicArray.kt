package edu.ucdavis.cs.ecs036c

/**
 * Welcome to the second normal data structure homework assignment for ECS 032C
 *
 * In this you will be implementing many functions for the
 * DynamicArray class.  It is strongly advised that you start
 * by implementing append (which toDynamicArray uses), get, resize, and the
 * iterator, as those 3 function are required for all the test code.
 *
 * Initial tests will use an array that IS large enough to trigger resize.
 *
 * You will also need to write a lot of tests, as we provide only the
 * most basic tests and the autograder deliberately hides the test results
 * until after the grades are released.
 *
 * However, you should be able to use your tests from Homework1 with just
 * some simple refactoring: changing every reference from LinkedList to
 * DynamicArray (and the corresponding change in the creation function).
 */

fun toDo(): Nothing {
    throw Error("Need to implement")
}


/**
 * This is the basic DynamicArray class.  This implements
 * effectively an ArrayList style implementation: We can
 * append to the front and end in (amortized) constant time,
 * thus it can be used as a stack, a queue, or a double-ended
 * queue.
 */
class DynamicArray<T> {
    /*
     * We have 3 internal private variables: the backing array that
     * stores the data, the internal size of the data, and where
     * in the array/ring the first element exists.  You must NOT
     * add any other private variables, rename these,
     * or change the types of the variables.
     *
     * We use the "reflection" interface in the autograder to access these fields
     * and to perform tests to make sure you don't add other fields.
     */

    /*
     * This is a case where Kotlin's type system interacts annoyingly with
     * Java.  We KNOW it is an array of type T?, but Kotlin requires
     * arrays themselves not to use type parameterization (for awkard reasons),
     * but any nullable T? is a subtype of Any? so this is safe, so we
     * add an annotation saying "yeah, just ignore this"
     */
    @Suppress("UNCHECKED_CAST")
    internal var storage : Array<T?> = arrayOfNulls<Any?>(4) as Array<T?>
    internal var privateSize = 0
    internal var start: Int = 0

    /**
     * This allows .size to be accessed but not set, with the private
     * variable to track the actual size elsewhere.
     */
    val size: Int
        get() = privateSize

    /**
     * You need to implement this basic iterator for the DynamicArray.  you
     * should probably take advantage of get and size and just have
     * a variable in the class to keep track of where you are in the array
     */
    class DynamicArrayIterator<T>(var array:DynamicArray<T>):AbstractIterator<T>(){
        var ind = 0
        override fun computeNext():Unit {
            if (ind < array.size) {
                setNext(array.get(ind))
                ind++
            }
            else {
                done()
            }
        }
    }

    /**
     * This needs to return an Iterator for the data in the cells.  This allows
     * the "for (x in aLinkedList) {...} to work as expected.
     */
    operator fun iterator() = DynamicArrayIterator(this)

    /**
     * And this is a useful one: indices, which is a range that
     * automatically covers the size
     */
    val indices : IntProgression
        get() = 0..<size

    /*
     * This function dynamically resizes the array by doubling its size and copying all
     * the elements over.  Although this is an O(N) operation, it gets amortized out over
     * the next N additions which means in the end the array's operations prove to be
     * constant time.
     */
    fun resize(){
        @Suppress("UNCHECKED_CAST")
        val newStorage : Array<T?> = arrayOfNulls<Any?>(storage.size * 2) as Array<T?>
        for (i in 0..< privateSize) {
            newStorage[i] = storage[(start + i) % storage.size]
        }
        start = 0
        storage = newStorage
    }

    /**
     * Append ads an item to the end of the array.  It should be
     * a constant-time (O(1)) function.  The only exception is if
     * the storage is full, in which case you call resize() and
     * then append the item.
     */
    fun append(item:T)  {
        if(storage.size == privateSize) {
            resize()
        }
        storage[(start + privateSize) % storage.size] = item
        privateSize++
    }

    /**
     * Adds an item to the START of the list.  It should be
     * a constant-time (O(1)) function as well.
     *
     * One important note, % in kotlin/java is not like % in Python.
     * In python -1 % x is x-1.  In kotlin it is -1.  So if you want
     * to decrement and mod you will want to do (-1 + x) % x.
     */
    fun prepend(item: T)  {
        if(storage.size == privateSize) {
            resize()
        }
        storage[(start - 1 + storage.size) % storage.size] = item
        start = (start - 1 + storage.size) % storage.size
        privateSize++
    }

    /**
     * Get the data at the specified index.  Because the storage
     * is an array it is constant time no matter the index.
     *
     * One note on typing: the storage array is typed as <T?>,
     * that is, T or null.  You want to return something of type T
     * however, so for the return statement, do a cast by going
     * return {whatever} as T.
     *
     * We need to do the cast rather than the !! operator because
     * we could have a DynamicArray<Int?> or similar that could
     * include nullable entries.
     *
     * You can suppress the compiler warning this generates with
     * a @Suppress("UNCHECKED_CAST") annotation before the return
     * statement.
     *
     * Invalid indices should throw an IndexOutOfBoundsException
     */
    operator fun get(index: Int) : T{
        if ((index < 0) || (index >= privateSize)) {
            throw IndexOutOfBoundsException("Out of bounds")
        }
        else {
            // This is an example of how to do the return
            // so that the compiler is happy.
            @Suppress("UNCHECKED_CAST")
            return storage[(start + index) % storage.size] as T
        }
    }

    /**
     * Replace the data at the specified index.  Again, this is an
     * constant time operation.
     *
     * Invalid indexes should throw an IndexOutOfBoundsException
     */
    operator fun set(index: Int, data: T) : Unit {
        if ((index < 0) || (index >= privateSize)) {
            throw IndexOutOfBoundsException("Out of bounds")
        }
        else {
            storage[(start + index) % storage.size] = data
        }
    }

    /**
     * This inserts the element at the index.
     *
     * If the index isn't valid, throw an IndexOutOfBounds exception
     *
     * This should be O(1) for the start and the end, O(n) for all other cases.
     */
    fun insertAt(index: Int, value: T) {
        if ((index < 0) || (index > privateSize)) {
            throw IndexOutOfBoundsException("Negative Index")
        }
        else if (index == privateSize) {
            append(value)
        }
        else if (index == 0) {
            prepend(value)
        }
        else {
            if(storage.size == privateSize) {
                resize()
            }
            storage[start]?.let { prepend(it) }
            for (i in 0..index) {
                storage[(start + i) % storage.size] = storage[(start + i + 1) % storage.size]
            }
            storage[(start + index) % storage.size] = value
        }
    }

    /**
     * This removes the element at the index and return the data that was there.
     *
     * Again, if the data doesn't exist it should throw an
     * IndexOutOfBoundsException.
     *
     * This should be O(1) for the first or last element, O(N) otherwise
     */
    fun removeAt(index: Int) : T {
        if ((index < 0) || (index >= privateSize)) {
            throw IndexOutOfBoundsException("Negative Index")
        }
        val res = storage[(start + index) % storage.size]
        if (index == 0) {
            privateSize--;
            if (privateSize == 0) {
                start = 0
            }
            else {
                start = (start + 1) % storage.size
            }
        }
        else if (index == privateSize - 1) {
            privateSize--;
        }
        else {
            for (i in index..< privateSize - 1) {
                storage[(start + i) % storage.size] = storage[(start + i + 1) % storage.size]
            }
            privateSize--
        }
        return res!!
    }

    /*
     * Functions to make this a full double ended queue, they call the appropriate
     * functions
     */
    fun push(item: T) = append(item)
    fun pushLeft(item: T) = prepend(item)
    fun pop() = removeAt(size - 1)
    fun popLeft() = removeAt(0)


    /**
     * This does a linear search for the item to see
     * what index it is at, or -1 if it isn't in the list
     */
    fun indexOf(item:T) :Int {
        var i = 0
        while (i < privateSize) {
            if (storage[(start + i) % storage.size] == item) {
                return i
            }
            i++
        }
        return -1
    }

    operator fun contains(item:T) = indexOf(item) != -1


    /**
     * A very useful function for debugging, as it will print out
     * the list in a convenient form.
     */
    override fun toString(): String {
        return iterator()
            .asSequence()
            .joinToString(prefix="[", postfix = "]", limit = 50)
    }

    /**
     * Fold
     */
    fun <R>fold(initial: R, operation: (R, T) -> R): R {
        var res = initial
        if(privateSize == 0) {
            return res
        }
        else {
            for(i in this) {
                res = operation(res, i)
            }
        }
        return res
    }

    /**
     * And you need to implement map, creating a NEW DynamicArray
     * and applying the function to each element in the old list.
     *
     * One useful note, because append is constant time, you
     * can just go in order and make a new dynamic array.
     */
    fun <R>map(operator: (T)->R): DynamicArray<R>{
        val newl = DynamicArray<R>()
        for(i in this) {
            newl.append(operator(i))
        }
        return newl
    }

    /**
     * Finally we have mapInPlace.  mapInPlace is like Map with a difference:
     * instead of creating a new array it applies the function to each data
     * element and uses that to replace the element in the existing array, returning
     * the array itself when done.
     */
    fun mapInPlace(operator: (T)->T) : DynamicArray<T> {
        for(i in 0..privateSize) {
            storage[(start + i) % storage.size] = storage[(start + i) % storage.size]?.let { operator(it) }
        }
        return this
    }

    /**
     * Likewise, filter returns a new DynamicArray.
     */
    fun filter(operator: (T)->Boolean) : DynamicArray<T>{
        val newl = DynamicArray<T>()
        for(i in this) {
            if(operator(i)) {
                newl.append(i)
            }
        }
        return newl
    }

    /**
     * And filterInPlace.  filterInPlace will keep only the elements
     * that are true.  Critically this should be LINEAR in the size
     * of the array.
     */
    fun filterInPlace(operator: (T)->Boolean) : DynamicArray<T>{
        var i = 0
        while (i in 0..< privateSize) {
            if(!storage[(start + i) % storage.size]?.let { operator(it) }!!) {
                val ret = this.removeAt(i)
            }
            else {
                i++
            }
        }
        return this
    }
}
/**
 * And this function builds a new LinkedList of the given type with
 * a vararg (variable argument) set of inputs.
 */
fun <T> toDynamicArray(vararg input:T) : DynamicArray<T> {
    val retval = DynamicArray<T>()
    for (item in input){
        retval.append(item)
    }
    return retval
}