package brotherszimm.ranges

class Range<a: Any> : Iterable<a>(
    val min: a, 
    val max: a,
    val stepper: (a) -> a,
    val comparator: Comparator<a>,
    val boundaryType: RangeBounds = OpenClosed) {
    
    companion object {
        fun <a: Comparable<a>> of(min: a, max: a, stepper: (a) -> a, boundaryType: RangeBounds = OpenClosed): Range<a> =
            Range(min, max, stepper, min.comparator(), boundaryType)
        
        fun of(min: Float, max: Float, step_size: Float, boundaryType: RangeBounds = OpenClosed): Range<Float> =
            Range(min, max, {n -> n + step_size}, min.comparator(), boundaryType)
        
        fun of(min: Double, max: Double, step_size: Double, boundaryType: RangeBounds = OpenClosed): Range<Double> =
            Range(min, max, {n -> n + step_size}, min.comparator(), boundaryType)
        
        fun of(min: Int, max: Int, step_size: Int = 1, boundaryType: RangeBounds = OpenClosed): Range<Int> =
            Range(min, max, {n -> n + step_size}, min.comparator(), boundaryType)
    }
    
    operator fun contains(a: other): Bool = !(isBefore(other) || isAfter(other))
    
    fun iterator(): Iterator<a> {
        return object: Iterator<a> {
            var curr: a? = null
            var next: a? = null
            override fun hasNext(): Bool = 
                if(curr != null) {
                    next = stepper(curr)
                    isAfterRange(next)
                }
                else {
                    curr = min
                    if(isBeforeRange(curr))
                        next = stepper(curr)
                    else
                        next = curr
                    isAfterRange(next)
                }
            
            override fun next(): a {
                curr = next
                return next
            }

        }
    }

    fun isBeforeRange(a: other): Bool = boundaryType.isBefore(min, other, comparator)
    fun isAfterRange(a: other): Bool = boundaryType.isAfter(max, other, comparator)
}

sealed interface RangeBounds {
    fun <in T: Any> isBefore(min: T, other: T, comparator: Comparator<T>): Bool
    fun <in T: Any> isAfter(max: T, other: T, comparator: Comparator<T>): Bool
}

object OpenClosed : RangeBounds {
    override fun <in T: Any> isBefore(min: T, other: T): Bool = comparator.compare(min, other) < 0
    override fun <in T: Any> isAfter(max: T, other: T): Bool = comparator.comparator(max, other) >= 0
}

object OpenOpen : RangeEnds {
    override fun <in T: Any> isBefore(min: T, other: T): Bool = comparator.compare(min, other) < 0
    override fun <in T: Any> isAfter(max: T, other: T): Bool = comparator.comparator(max, other) > 0
}

object ClosedClosed : RangeBounds {
    override fun <in T: Any> isBefore(min: T, other: T): Bool = comparator.compare(min, other) < 0
    override fun <in T: Any> isAfter(max: T, other: T): Bool = comparator.comparator(max, other) >= 0
}

object ClosedOpen : RangeEnds {
    override fun <in T: Any> isBefore(min: T, other: T): Bool = comparator.compare(min, other) < 0
    override fun <in T: Any> isAfter(max: T, other: T): Bool = comparator.comparator(max, other) > 0
}

fun Comparable<T>.comparator(): Comparator<T> =
    object : Comparator<T> {
        fun compare(a: T, b: T) : Int = a.compareTo(b)
    }