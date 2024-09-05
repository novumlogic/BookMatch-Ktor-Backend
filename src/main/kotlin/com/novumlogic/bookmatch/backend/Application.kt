package com.novumlogic.bookmatch.backend

import com.novumlogic.bookmatch.backend.plugins.configureRouting
import com.novumlogic.bookmatch.backend.plugins.configureSerialization
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.minimalSettings
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.server.application.*


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}


val supabaseClient = createSupabaseClient(System.getenv("SUPABASE_URL"), System.getenv("SUPABASE_KEY")){
    install(Auth){
        minimalSettings()
    }
}

fun Application.module() {
    configureSerialization()
    configureRouting()
}
