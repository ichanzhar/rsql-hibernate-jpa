package com.github.ichanzhar.rsql.exception

class InvalidDateFormatException(argument: String?, property: String?) :
    RuntimeException("The datetime parameter: '$argument' for the field: '$property' has an invalid date format.")
