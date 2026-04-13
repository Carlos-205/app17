package com.example.app17

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://ptbvapvowrnxghbvhcwh.supabase.co",
        supabaseKey ="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB0YnZhcHZvd3JueGdoYnZoY3doIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzU4NTMwNjEsImV4cCI6MjA5MTQyOTA2MX0.9HIzmWTgTIMq-bFdlJyF-geZrCHnbj5LLAGgv_HeQ3k"
    ){
        install(Postgrest)
        install(Auth)
    }
}