package extensions

//print this óbject to string for information pruposes and returns itself
fun <T> T.i(): T {
    println(this.toString())
    return this
}

infix fun Boolean.ifTrue(function: () -> Unit):Boolean {
    if (this) {
        function()
    }
    return this
}

infix fun Boolean.ifFalse(function: () -> Unit):Boolean {
    if (!this) {
        function()
    }
    return this
}