package edu.ucdavis.cs.ecs036c

import kotlin.random.Random

/*
 * This is declaring an "Extension Function", basically we are
 * creating a NEW method for Array<T> items.  this will
 * refer to the Array<T> it is called on.
 *
 * We allow an optional comparison function, and this does NOT NEED TO BE
 * a stable sort.
 */
fun <T: Comparable<T>> Array<T>.selectionSort(reverse: Boolean = false) : Array<T>{
    if(reverse){
        return this.selectionSortWith(object: Comparator<T> {
            override fun compare(a: T, b: T): Int {
                return b.compareTo(a)
            }})
    }
    return this.selectionSortWith(object: Comparator<T> {
        override fun compare(a: T, b: T): Int {
            return a.compareTo(b)
        }})
}

fun <T: Comparable<T>> Array<T>.selectionSortWith(comp: Comparator<in T>) : Array<T> {
    for(i in 0..<size){
        var min = this[i]
        var minAt = i
        for(j in i..<size){
            val compare = comp.compare(this[j], min)
            if(compare < 0){
                min = this[j]
                minAt = j
            }
        }
        this[minAt] = this[i]
        this[i] = min
    }
    return this
}

fun <T: Comparable<T>> Array<T>.quickSort(reverse: Boolean = false) : Array<T>{
    if(reverse){
        return this.quickSortWith(object: Comparator<T> {
            override fun compare(a: T, b: T): Int {
                return b.compareTo(a)
            }})
    }
    return this.quickSortWith(object: Comparator<T> {
        override fun compare(a: T, b: T): Int {
            return a.compareTo(b)
        }})
}


/*
 * Here is the QuickSort function you need to implement
 */
fun <T> Array<T>.quickSortWith( comp: Comparator<in T>) : Array<T> {
    fun swap(a: Int, b :Int) {
        val temp = this[a]
        this[a] = this[b]
        this[b] = temp
    }

    fun quicksort_internal(start :Int, end :Int) {
        if (end <= start) {
            return
        }
        var pivotPoint = end
        var swapPoint = start
        var examinePoint = start

        swap(Random.nextInt(start, end + 1), pivotPoint)

        while (examinePoint < pivotPoint) {
            if ((comp.compare(this[examinePoint], this[pivotPoint]) < 0)
                or (this[pivotPoint] == this[examinePoint] && Random.nextInt(2) == 0)) {
                swap(swapPoint,examinePoint)
                swapPoint += 1
            }
            examinePoint += 1
        }

        swap(swapPoint,pivotPoint)
        quicksort_internal(start, swapPoint - 1)
        quicksort_internal(swapPoint + 1, end)
    }
    quicksort_internal(0, this.size - 1)
    return this
}
