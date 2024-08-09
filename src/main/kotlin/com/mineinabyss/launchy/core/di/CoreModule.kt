package com.mineinabyss.launchy.core.di

import com.mineinabyss.launchy.core.data.TasksRepository
import com.mineinabyss.launchy.core.ui.LaunchyViewModel
import org.koin.dsl.module

fun coreModule() = module {
    single { TasksRepository() }
    single { LaunchyViewModel(get()) }
}
