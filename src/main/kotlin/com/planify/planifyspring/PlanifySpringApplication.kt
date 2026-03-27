package com.planify.planifyspring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PlanifySpringApplication

fun main(args: Array<String>) {
	runApplication<PlanifySpringApplication>(*args)
}
