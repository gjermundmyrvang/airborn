package no.uio.ifi.in2000.team18.airborn.data.repository.parsers

data class ParseState(
    val source: String,
    val pos: Int = 0,
) {
    val sourceLength get() = source.length - pos
    fun startsWith(prefix: String): Boolean =
        prefix.length <= sourceLength && source.substring(pos, pos + prefix.length) == prefix

    fun forward(n: Int) = copy(pos = pos + n)

    override fun toString(): String = "ParseState(source=${source.substring(pos)})"
}

sealed interface ParseResult<T> {
    data class Ok<T>(val value: T, val state: ParseState) : ParseResult<T>
    data class Error<T>(val expected: List<String>) : ParseResult<T>
}

abstract class Parser<T> {
    protected abstract fun parse(state: ParseState): ParseResult<T>
    fun parse(input: String) = parse(ParseState(source = input, pos = 0))

    fun <T2> bind(fn: (T) -> Parser<T2>): Parser<T2> = GenericParser { state ->
        when (val result1 = parse(state)) {
            is ParseResult.Error -> ParseResult.Error(result1.expected)
            is ParseResult.Ok -> fn(result1.value).parse(result1.state)
        }
    }

    fun <T2> map(fn: (T) -> T2): Parser<T2> = bind { it -> pure(fn(it)) }
    fun or(other: Parser<T>): Parser<T> = GenericParser { state ->
        when (val result = parse(state)) {
            is ParseResult.Ok -> result
            is ParseResult.Error -> when (val result2 = other.parse(state)) {
                is ParseResult.Ok -> result2
                is ParseResult.Error -> ParseResult.Error(expected = result.expected + result2.expected)
            }
        }
    }

    fun filter(fn: (T) -> Boolean, expected: String): Parser<T> = bind { it ->
        if (fn(it)) pure(it) else error(expected)
    }

    fun <T2> skip(p: Parser<T2>) = bind { v -> p.map { v } }

    fun skipChars(predicate: (Char) -> Boolean) = skip(chars(predicate))
    fun skipSpace() = skipChars { it.isWhitespace() }
    fun optional(): Parser<T?> = map<T?> { it }.or(pure(null))
    fun optional(default: T): Parser<T> = or(pure(default))
    fun nullable(): Parser<T?> = map { it }
}

fun <T> pure(value: T): Parser<T> = GenericParser { ParseResult.Ok(value, it) }
fun <T> error(err: String): Parser<T> = GenericParser { ParseResult.Error(expected = listOf(err)) }

class GenericParser<T>(val parseFun: (ParseState) -> ParseResult<T>) : Parser<T>() {
    override fun parse(state: ParseState) = parseFun(state)
}


fun word(word: String): Parser<String> = GenericParser { state ->
    if (state.startsWith(word)) {
        ParseResult.Ok(word, state.forward(word.length))
    } else {
        ParseResult.Error(expected = listOf(word))
    }
}

fun chars(predicate: (Char) -> Boolean) = GenericParser { state ->
    var pos = state.pos
    while (pos < state.source.length && predicate(state.source.get(pos))) {
        pos += 1
    }
    ParseResult.Ok(
        state.source.substring(state.pos, pos), state.forward(pos - state.pos)
    )
}


fun chars1(expected: String, predicate: (Char) -> Boolean): Parser<String> =
    chars(predicate).filter({ it.length != 0 }, expected)

fun char(expected: String = "<unknown>", predicate: (Char) -> Boolean = { true }) =
    GenericParser { state ->
        if (state.pos < state.source.length && predicate(state.source.get(state.pos))) ParseResult.Ok(
            state.source[state.pos], state.forward(1)
        )
        else ParseResult.Error(expected = listOf(expected))

    }

fun <T, T2> sepBy(parser: Parser<T>, sep: Parser<T2>): Parser<List<T>> = parser.bind { v1 ->
    sep.bind { sepBy(parser, sep) }.optional().map { listOf(v1) + (it ?: listOf()) }
}

fun <T> many(parser: Parser<T>): Parser<List<T>> = parser.bind { v1 ->
    many(parser).optional().map { listOf(v1) + (it ?: listOf()) }
}

fun char(c: Char) = char("$c") { it == c }

fun <T, T2> pair(p1: Parser<T>, p2: Parser<T2>): Parser<Pair<T, T2>> =
    p1.bind { a -> p2.map { Pair(a, it) } }

fun <T, T2, T3> triple(p1: Parser<T>, p2: Parser<T2>, p3: Parser<T3>): Parser<Triple<T, T2, T3>> =
    p1.bind { a -> p2.bind { b -> p3.map { Triple(a, b, it) } } }

fun <T, T2, R> lift(p1: Parser<T>, p2: Parser<T2>, fn: (T, T2) -> R): Parser<R> =
    p1.bind { a -> p2.map { fn(a, it) } }

fun <T, T2, T3, R> lift(
    p1: Parser<T>,
    p2: Parser<T2>,
    p3: Parser<T3>,
    fn: (T, T2, T3) -> R
): Parser<R> =
    p1.bind { a -> p2.bind { b -> p3.map { fn(a, b, it) } } }

fun <T, T2, T3, T4, R> lift(
    p1: Parser<T>, p2: Parser<T2>, p3: Parser<T3>, p4: Parser<T4>, fn: (T, T2, T3, T4) -> R
): Parser<R> = p1.bind { a -> p2.bind { b -> p3.bind { c -> p4.map { fn(a, b, c, it) } } } }

fun <T, T2, T3, T4, T5, R> lift(
    p1: Parser<T>,
    p2: Parser<T2>,
    p3: Parser<T3>,
    p4: Parser<T4>,
    p5: Parser<T5>,
    fn: (T, T2, T3, T4, T5) -> R
): Parser<R> = p1.bind { a ->
    p2.bind { b ->
        p3.bind { c ->
            p4.bind { d ->
                p5.map {
                    fn(
                        a,
                        b,
                        c,
                        d,
                        it
                    )
                }
            }
        }
    }
}

fun <T, T2, T3, T4, T5, T6, R> lift(
    p1: Parser<T>,
    p2: Parser<T2>,
    p3: Parser<T3>,
    p4: Parser<T4>,
    p5: Parser<T5>,
    p6: Parser<T6>,
    fn: (T, T2, T3, T4, T5, T6) -> R
): Parser<R> = p1.bind { a ->
    p2.bind { b ->
        p3.bind { c ->
            p4.bind { d ->
                p5.bind { e ->
                    p6.map {
                        fn(
                            a,
                            b,
                            c,
                            d,
                            e,
                            it
                        )
                    }
                }
            }
        }
    }
}

fun <T> either(vararg parsers: Parser<T>) = parsers.reduce { acc, cur -> acc.or(cur) }
