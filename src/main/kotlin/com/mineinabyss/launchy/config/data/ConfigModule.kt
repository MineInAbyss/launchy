package com.mineinabyss.launchy.config.data

import org.koin.dsl.module

fun configModule() = module {
    single { ConfigDataSource() }
}
