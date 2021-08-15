package br.com.edumatt3

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.com.edumatt3")
		.start()
}

