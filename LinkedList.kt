package edu.ucdavis.cs.ecs036c

/**
 *
 */


/**
 * This is the data class for a cell within the LinkedList.  You won't need to
 * add anything to this class.
 */
data class LinkedListCell<T>(var data:T, var next:LinkedListCell<T>?) {
}

/**
 * This is the basic LinkedList class, you will need to
 * implement several member functions in this class to
 * implement a LinkedList library.
 */
class LinkedList<T> {
    // You should have 3 variables:
    // A pointer to the first cell or null
    // A pointer to the last cell or null
    // An internal record to keep track of the number
    // of elements present.
    private var headCell: LinkedListCell<T>? = null
    private var tailCell: LinkedListCell<T>? = null
    private var privateSize = 0

    /**
     * This allows .size to be accessed but not set, with the private
     * variable to track the actual size elsewhere.
     */
    val size: Int
        get() = privateSize


    /**
     * You want to implement an iterator for the DATA in the LinkedList,
     * So you will need to implement a computeNext() function.
     *
     * Note: Kotlin will continue to treat at as nullable within the
     * computeNext function, because you could have some concurrent access
     * that could change the structure of the list.
     *
     * Thus in accessing the data one option you can use is an elvis operator ?:
     * to throw an IllegalStateException if there is an inconsistency
     * during iteration.
     *
     * We will not check whether your code would actually throw such an error,
     * because we aren't assuming the LinkedList class is thread safe, but by
     * doing that you can make the Kotlin type system happy.
     *
     * As an alternative you could just use the !!. operation that
     * will throw a NullPointerException instead.  Either are acceptable.
     */
    class LinkedListIterator<T>(var at: LinkedListCell<T>?):AbstractIterator<T>(){
        override fun computeNext():Unit {
            if (at == null) {
                done()
            } else {
                val data = at!!.data
                at = at!!.next
                setNext(data)
            }
        }
    }

    /**
     * You will also want an iterator for the cells themselves in the LinkedList,
     * as there are multiple cases where you are going to want to iterate over
     * the cells not just the data in the cells.
     */
    class LinkedListCellIterator<T>(var at: LinkedListCell<T>?):AbstractIterator<LinkedListCell<T>>(){
        override fun computeNext():Unit {
             if (at == null) {
                done()
            } else {
                val data = at!!
                at = at!!.next
                setNext(data)
            }
        }
    }




    /**
     * Append ads an item to the end of the list.  It should be
     * a constant-time (O(1)) function regardless of the number
     * of elements in the LinkedList
     */
    fun append(item:T)  {
        if (tailCell == null){
            tailCell = LinkedListCell(item, null)
            headCell = tailCell
            privateSize = 1
        } else {
            tailCell?.next = LinkedListCell(item, null)
            tailCell = tailCell?.next
            privateSize += 1
        }
    }

    /**
     * Adds an item to the START of the list.  It should be
     * a constant-time (O(1)) function.
     */
    fun prepend(item: T)  {
        if (headCell == null){
            headCell = LinkedListCell(item, null)
            tailCell = headCell
            privateSize = 1
        } else {
            headCell = LinkedListCell(item, headCell)
            privateSize += 1
        }
    }

    /**
     * Get the data at the specified index.  For a linked-list
     * this is an O(N) operation in general, but it should be O(1)
     * for both the first and last element.
     *
     * Invalid indices should throw an IndexOutOfBoundsException
     */
    operator fun get(index: Int) : T{
        if (index < 0) {
            throw IndexOutOfBoundsException("Negative Index")
        }
        if (index == size-1){
            return tailCell!!.data
        }
        var x = 0
        for(item in this){
            if (x == index) {
                return item
            }
            x++
        }
        throw IndexOutOfBoundsException("Exceeded Index Range")
    }

    /**
     * Replace the data at the specified index.  Again, this is an
     * O(N) operation, except if it is the first or last element
     * in which case it should be O(1)
     *
     * Invalid indexes should throw an IndexOutOfBoundsException
     */
    operator fun set(index: Int, data: T) : Unit {
        if (index < 0) throw IndexOutOfBoundsException("Negative Index")
        if (index == size-1){
            tailCell!!.data = data
            return
        }
        var x = 0
        for(item in this.cellIterator()){
            if (x == index) {
                item.data = data
                return
            }
            x++
        }
        throw IndexOutOfBoundsException("Exceeded Index Range")
    }

    /**
     * This inserts the element at the index.
     *
     * If the index isn't valid, throw an IndexOutOfBounds exception
     *
     * This should be O(1) for the start and the end, O(n) for all other cases.
     */
    fun insertAt(index: Int, value: T) {
        if(index < 0){
            throw IndexOutOfBoundsException("Invalid index")
        }
        if(index == 0) {
            return prepend(value)
        }
        if(index > size) {
            throw IndexOutOfBoundsException("Invalid index")
        }
        if(index == size) {
            return append(value)
        }
        var cellAt = headCell
        for(x in 0..<(index-1)){
            cellAt = cellAt!!.next
        }
        val newCell = LinkedListCell<T>(value, cellAt!!.next)
        cellAt!!.next = newCell
        privateSize++
    }

    /**
     * This removes the element at the index and return the data that was there.
     *
     * Again, if the data doesn't exist it should throw an
     * IndexOutOfBoundsException.
     *
     * This is O(N), and there is no shortcut possible for the last element
     */
    fun removeAt(index: Int) : T {
        if (index < 0 ){
            throw IndexOutOfBoundsException("Wrong Index")
        }
        if (index >= size) {
            throw IndexOutOfBoundsException("Wrong Index")
        }
        if (index == 0) {
            if (headCell == null) throw IndexOutOfBoundsException("Wrong Index")
            val data = headCell!!.data
            headCell = headCell!!.next
            privateSize += -1
            if (privateSize == 0){
                tailCell = headCell
            }
            return data
        }
        var x = 0
        for(cell in cellIterator()){
            x += 1
            if (x == index) {
                val data = cell.next!!.data
                if (x + 1 == size){
                    tailCell = cell
                }
                cell.next = cell.next?.next
                privateSize += -1
                return data
            }
        }
        throw UnsupportedOperationException("Unreachable Code")
    }

    /**
     * This does a linear search for the item to see
     * what index it is at, or -1 if it isn't in the list
     */
    fun indexOf(item:T) :Int {
        var x = 0
        for (item2 in this){
            if (item == item2) {
                return x
            }
            x++
        }
        return -1
    }

    operator fun contains(item:T) = indexOf(item) != -1


    /**
     * This needs to return an Iterator for the data in the cells.  This allows
     * the "for (x in aLinkedList) {...} to work as expected.
     */
    operator fun iterator() = LinkedListIterator(headCell)

    /**
     * An internal helper function that returns an iterator for the
     * cells themselves.  This is very useful for both mapInPlace and
     * other functions you may need to implement.
     */
    fun cellIterator() = LinkedListCellIterator(headCell)

    /**
     * A very useful function for debugging, as it will print out
     * the list in a convenient form.  Actually showing you the code
     * as is rather than having you implement it, because it gives you
     * an idea of how powerful things are now that you have an iterator
     * and can convert that iterator to a sequence (which supports fold).
     */
    override fun toString(): String {
        return iterator()
            .asSequence()
            .fold("[") {initial , item ->
                if (initial != "[") initial + ", " + item.toString()
                else "[" + item.toString()} + "]"
    }

    /**
     * Of course, however, you have to implement your own version of fold
     * directly...
     */
    fun <R>fold(initial: R, operation: (R, T) -> R): R {
        var result = initial
        for(data in this){
            result = operation(result, data)
        }
        return result
    }

    /**
     * And you need to implement map, creating a NEW LinkedList
     * and applying the function to each element in the old list.
     *
     * One useful note, because append is constant time, you
     * can just go in order and make a new list.
     */
    fun <R>map(operator: (T)->R): LinkedList<R>{
        val final = LinkedList<R>()
        for (data in this) {
            final.append(operator(data))
        }
        return final
    }

    /**
     * Finally we have mapInPlace.  mapInPlace is like Map with a difference:
     * instead of creating a new list it applies the function to each data
     * element and uses that to replace the element in the existing list, returning
     * the list itself when done.
     */
    fun mapInPlace(operator: (T)->T) : LinkedList<T>{
        for (cell in this.cellIterator()){
            cell.data = operator(cell.data)
        }
        return this
    }

    fun filter(operator: (T)->Boolean) : LinkedList<T>{
        val newList = LinkedList<T>()
        for(data in this){
            if(operator(data)){
                newList.append(data)
            }
        }
        return newList
    }

    /**
     * And filterInPlace.  filterInPlace will keep only the elements
     * that are true.
     */
    fun filterInPlace(operator: (T)->Boolean) : LinkedList<T>{
        var cellAt = headCell
        var newSize = 0
        var newCellAt: LinkedListCell<T>? = null
        var newHead: LinkedListCell<T>? = null
        while(cellAt != null) {
            if(operator(cellAt.data)){
                newSize += 1
                if(newCellAt == null){
                    newHead = cellAt
                    newCellAt = cellAt
                } else {
                    newCellAt.next = cellAt
                    newCellAt = cellAt
                }
            }
            cellAt = cellAt.next
        }
        if(newCellAt != null) {
            newCellAt.next = null
        }
        privateSize = newSize
        headCell = newHead
        tailCell = newCellAt
        return this
    }

    fun insertionSortWith(comp: Comparator<in T>) : LinkedList<T> {
        if (size < 2) {
            return this
        }
        var atCell = headCell!!.next
        headCell!!.next = null
        tailCell = headCell
        while (atCell != null){
            val current = atCell
            atCell = atCell.next
            current.next = null
            if (comp.compare(current.data, headCell!!.data) < 0){
                current.next = headCell
                headCell = current
            } else {
                var insertAt = headCell
                while(insertAt!!.next != null){
                    if(comp.compare(current.data, insertAt.next!!.data) < 0){
                        current.next = insertAt.next
                        insertAt.next = current
                        break
                    }
                    insertAt = insertAt!!.next
                }
                if(insertAt.next == null){
                    insertAt.next = current
                    tailCell = current
                }
            }
        }
        return this
    }

    /*
     * Declared outside the mergeSortWith function so you can build
     * separate testcases if you want, but we don't do that in the
     * testing code
     */
    fun split(start: LinkedListCell<T>?) : Pair<LinkedListCell<T>?,LinkedListCell<T>?> {
        var length = 0;
        var temp = start
        while (temp != null) {
            length++
            temp = temp.next
        }
        var endfirst = start
        var i = 0
        while (i < (length / 2) - 1) {
            endfirst = endfirst?.next
            i += 1
        }
        val startsecond = endfirst?.next
        endfirst?.next = null
        return (start to startsecond)
    }

    fun mergeSortWith(comp: Comparator<in T>) : LinkedList<T> {
        fun mergeSortInternal(cells: LinkedListCell<T>) : LinkedListCell<T>? {
            if (cells.next == null){
                return cells
            }
            var (firstHalf, secondHalf) = split(cells)
            firstHalf = mergeSortInternal(firstHalf!!)
            secondHalf = mergeSortInternal(secondHalf!!)
            var result : LinkedListCell<T>?
            //var at : LinkedListCell<T>?

            if (comp.compare(secondHalf!!.data, firstHalf!!.data) < 0) {
                result = secondHalf
                secondHalf = secondHalf.next
            }
            else {
                result = firstHalf
                firstHalf = firstHalf.next
            }

            var at = result
            at.next = null

            while (firstHalf != null) {
                if (secondHalf == null) {
                    at?.next = firstHalf
                    return result
                }
                else if (comp.compare(secondHalf.data, firstHalf.data) < 0) {
                    at?.next = secondHalf
                    secondHalf = secondHalf.next
                }
                else {
                    at?.next = firstHalf
                    firstHalf = firstHalf.next
                }

                at = at?.next
                at?.next = null
            } 

            if (secondHalf != null) {
                at?.next = secondHalf
            }

            return result
        }

        if (size < 2) {
            return this
        }
        headCell = mergeSortInternal(headCell!!)
        var temp = headCell
        while (temp!!.next != null) {
            tailCell = temp
            temp = temp.next
        }
        return this
    }
}


fun <T: Comparable<T>> LinkedList<T>.insertionSort(reverse: Boolean = false) : LinkedList<T>{
    if(reverse){
        return this.insertionSortWith(object: Comparator<T> {
            override fun compare(a: T, b: T): Int {
                return b.compareTo(a)
            }})
    }
    return this.insertionSortWith(object: Comparator<T> {
        override fun compare(a: T, b: T): Int {
            return a.compareTo(b)
        }})
}

fun <T: Comparable<T>> LinkedList<T>.mergeSort(reverse: Boolean = false) : LinkedList<T>{
    if(reverse){
        return this.mergeSortWith(object: Comparator<T> {
            override fun compare(a: T, b: T): Int {
                return b.compareTo(a)
            }})
    }
    return this.mergeSortWith(object: Comparator<T> {
        override fun compare(a: T, b: T): Int {
            return a.compareTo(b)
        }})
}


/**
 * And this function builds a new LinkedList of the given type with
 * a vararg (variable argument) set of inputs.  You should
 * implement this first as all other tests will depend on this.
 */
fun <T> toLinkedList(vararg input:T) : LinkedList<T> {
    val retval = LinkedList<T>()
    for (item in input){
        retval.append(item)
    }
    return retval
}