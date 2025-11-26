package modules

import org.koin.dsl.module
import presentation.viewmodels.DetailsViewModel
import presentation.viewmodels.MainViewModel
import presentation.viewmodels.UserViewModel

val viewModelModule = module {
    single { MainViewModel(get(), get(), get()) } // use single for keeping state
    factory { DetailsViewModel(get(), get()) } // use factory for cleaning state every time the screen is closed
    single { UserViewModel(get()) }
}