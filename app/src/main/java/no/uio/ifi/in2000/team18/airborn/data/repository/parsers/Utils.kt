package no.uio.ifi.in2000.team18.airborn.data.repository.parsers

// Generic stuff
val digit = char { it.isDigit() }.map { it.digitToInt() }
val twoDigitNumber = pair(digit, digit).map { it.first * 10 + it.second }
val threeDigitNumber = lift(digit, digit, digit) { a, b, c -> 100 * a + 10 * b + c }
val number = chars1("a number") { it.isDigit() }.map { it.toInt() }

