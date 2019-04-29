package nicestring

fun String.isNice(): Boolean {

    val containsSameLetter = this.withIndex()
            .mapNotNull { if (it.index < this.lastIndex) it.value to this[it.index + 1] else null }
            .any { it.first == it.second }
    val containsThreeVowels = this.toList().count { "aeiou".contains(it) } >= 3
    if (containsSameLetter && containsThreeVowels) return true

    val containsSubstring = this.withIndex().any {
        if (it.value == 'b' && it.index < this.lastIndex)
            "uae".contains(this[it.index + 1])
        else false
    }
    return (containsSameLetter && containsThreeVowels)
            || (containsSameLetter && !containsSubstring)
            || (containsThreeVowels && !containsSubstring)
}

fun String.isNiceCourseraSolution(): Boolean {
    val noBadSubString = setOf("ba", "be", "bu").none { this.contains(it) }
    val hasThreeVowels = count { it in "aeiou" } >= 3
    val hasDouble = zipWithNext().any { it.first == it.second }
    return listOf(noBadSubString, hasThreeVowels, hasDouble).count { it } >= 2
}