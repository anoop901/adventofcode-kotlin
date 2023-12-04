fun <T> Iterable<T>.groupedBySeparator(isSeparator: (T) -> Boolean): Sequence<List<T>> = sequence {
    var currentGroup = mutableListOf<T>()
    for (item in this@groupedBySeparator) {
        if (isSeparator(item)) {
            if (currentGroup.isNotEmpty()) {
                yield(currentGroup)
                currentGroup = mutableListOf()
            }
        } else {
            currentGroup.add(item)
        }
    }
    if (currentGroup.isNotEmpty()) {
        yield(currentGroup)
    }
}