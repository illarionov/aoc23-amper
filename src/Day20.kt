import PulseType.HIGH
import PulseType.LOW

fun main() {
    val testInput = readInput("Day20_test").parse()
    val input = readInput("Day20").parse()

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 11687500L)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        check(it == 670984704L)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        check(it == 262775362119547) // 3853*4093*4091*4073
    }
}

private enum class PulseType { HIGH, LOW}

private data class Module(
    val name: String,
    val type: Char,
    val isOn: Boolean,
    val memory: Map<String, PulseType>
)

private data class ModuleLink(
    val src: String,
    val dst: String
)

private data class Pulse(
    val type: PulseType,
    val src: String,
    val dst: String,
)

private data class Modules(
    val modules: Map<String, Module>,
    val links: Set<ModuleLink>,
) {
    val linksFrom: Map<String, List<String>> = links.groupBy(ModuleLink::src) { it.dst }
    val linksTo: Map<String, List<String>> = links.groupBy(ModuleLink::dst) { it.src}
}

private fun List<String>.parse(): Modules {
    var modules = mutableMapOf<String, Module>()
    val links = mutableSetOf<ModuleLink>()

    this.forEach { str ->
        val (src, dst) = str.split(" -> ")
        val module = when (src[0]) {
            '%' -> {
                Module(
                    name = src.drop(1),
                    type = '%',
                    isOn = false,
                    memory = mutableMapOf()
                )
            }
            '&' -> {
                Module(
                    name = src.drop(1),
                    type = '&',
                    isOn = false,
                    memory = mutableMapOf()
                )
            }
            else -> {
                Module(
                    name = src,
                    type = if (src == "broadcaster") 'b' else  '*',
                    isOn = false,
                    memory = mutableMapOf()
                )
            }
        }
        modules[module.name] = module

        val dstModules = dst.split(", ")
        for (m in dstModules) {
            links.add(ModuleLink(module.name, m))
        }
    }
    return Modules(
        modules = modules,
        links = links
    )
}

private fun part1(input: Modules): Long {
    val pulses: MutableMap<PulseType, Long> = mutableMapOf()
    pulses[HIGH] = 0
    pulses[LOW] = 0
    val queue = ArrayDeque<Pulse>()
    var modules = input.modules.toMutableMap()

    fun queueForAllDst(
        src: Module,
        pulseType: PulseType
    ) {
        for (m in input.linksFrom.getOrDefault(src.name, emptyList())) {
            queue.add(Pulse(pulseType, src.name, m))
        }
    }

    for (buttonPress in 1..1000) {
        queue.add(Pulse(LOW, "button", "broadcaster"))
        while (queue.isNotEmpty()) {
            val pulse = queue.removeFirst()
            pulses[pulse.type] = pulses.getOrDefault(pulse.type, 0) + 1
            val module = modules[pulse.dst] ?: Module(
                name = "ignore",
                type = 'I',
                isOn = false,
                memory = emptyMap()
            )
            when (module.type) {
                'b' -> {
                    queueForAllDst(module, pulse.type)
                }
                '%' -> {
                    if (pulse.type == LOW) {
                        val oldState = module.isOn
                        val newModule = module.copy(
                            isOn = !oldState
                        )
                        modules = modules.toMutableMap().apply {
                            this[module.name] = newModule
                        }
                        queueForAllDst(
                            module,
                            if (oldState == false) HIGH else LOW
                        )
                    }
                }
                '&' -> {
                    val newMemory = mutableMapOf<String, PulseType>()
                    input.linksTo[module.name]!!.forEach {
                        newMemory[it] = module.memory.getOrDefault(it, LOW)
                    }
                    newMemory[pulse.src] = pulse.type
                    val newModule = module.copy(
                        memory = newMemory
                    )
                    modules = modules.toMutableMap().apply {
                        this[module.name] = newModule
                    }
                    val newPulseType = if (newMemory.values.all { it == HIGH }) LOW else HIGH
                    queueForAllDst(
                        module,
                        newPulseType
                    )
                }
                else -> {
                    // ignore
                }
            }
        }
    }
    return pulses[HIGH]!! * pulses[LOW]!!
}

private fun part2(input: Modules): Long {
    val pulses: MutableMap<PulseType, Long> = mutableMapOf()
    val queue = ArrayDeque<Pulse>()
    var modules = input.modules.toMutableMap()

    val lastActivationCycle: MutableMap<String, Long> = mutableMapOf()

    fun queueForAllDst(
        src: Module,
        pulseType: PulseType
    ) {
        for (m in input.linksFrom.getOrDefault(src.name, emptyList())) {
            queue.add(Pulse(pulseType, src.name, m))
        }
    }

    var buttonPress = 0L
    e@ while (true)  {
        buttonPress += 1
        queue.add(Pulse(LOW, "button", "broadcaster"))
        while (queue.isNotEmpty()) {
            val pulse = queue.removeFirst()
            pulses[pulse.type] = pulses.getOrDefault(pulse.type, 0) + 1
            if (pulse.dst == "rx") {
                if (pulse.type == LOW) {
                    println("$buttonPress, pulse to rx $pulse")
                    break@e
                }
            }
            val module = modules[pulse.dst] ?: Module(
                name = pulse.dst,
                type = 'I',
                isOn = false,
                memory = emptyMap()
            )
            when (module.type) {
                'b' -> {
                    queueForAllDst(module, pulse.type)
                }
                '%' -> {
                    if (pulse.type == LOW) {
                        val oldState = module.isOn
                        val newModule = module.copy(
                            isOn = !oldState
                        )
                        modules = modules.toMutableMap().apply {
                            this[module.name] = newModule
                        }
                        queueForAllDst(
                            module,
                            if (oldState == false) HIGH else LOW
                        )
                    }
                }
                '&' -> {
                    val newMemory = mutableMapOf<String, PulseType>()
                    input.linksTo[module.name]!!.forEach {
                        newMemory[it] = module.memory.getOrDefault(it, LOW)
                    }
                    newMemory[pulse.src] = pulse.type

                    val newModule = module.copy(memory = newMemory)
                    modules = modules.toMutableMap().apply {
                        this[module.name] = newModule
                    }
                    val newPulseType = if (newMemory.values.all { it == HIGH }) LOW else HIGH
                    queueForAllDst(
                        module,
                        newPulseType
                    )

                    if (newPulseType == HIGH) {
                        val old = lastActivationCycle[module.name]
                        if (old == null) {
                            lastActivationCycle[module.name] = buttonPress
                        } else {
                            val diff = buttonPress - old
                            lastActivationCycle[module.name] = buttonPress
                            // Parents of "rc"
                            if (module.name == "gc" || module.name == "sz" || module.name == "cm" || module.name == "xf") {
                                println("activation since last ${module.name}: $diff")
                            }
                        }
                    }
                }
                else -> {
                    // ignore
                }
            }
        }
    }
    return buttonPress
}
