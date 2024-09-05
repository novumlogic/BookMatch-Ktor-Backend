package com.novumlogic.bookmatch.backend.plugins

import com.novumlogic.bookmatch.backend.data.OpenAiClient
import com.novumlogic.bookmatch.backend.model.request.RecommendationInput
import com.novumlogic.bookmatch.backend.model.response.ErrorResponse
import com.novumlogic.bookmatch.backend.supabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.exception.AuthRestException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureRouting() {

    //plugin to configure rate-limit on the api
    install(RateLimit) {
        register(RateLimitName("protected")) {
            rateLimiter(limit = 10, refillPeriod = 30.seconds)
        }
    }

    //used to configure response based on status or exceptions, if exception is thrown or status is returned anywhere in code common code can be written here
    install(StatusPages) {

        exception<RequestValidationException> { call, requestValidationException ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(requestValidationException.reasons.joinToString()))
        }

        exception<ExceptionInInitializerError> { call, exceptionInInitializerError ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("Internal server error: $exceptionInInitializerError (missing environment variables, expired api keys)")
            )
        }

        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }

        status(HttpStatusCode.TooManyRequests) { call, status ->
            val retryAfter = call.response.headers["Retry-After"]
            call.respond(
                message = ErrorResponse("429: Too many requests. Wait for $retryAfter seconds."),
                status = status
            )
        }
    }

    //validating incoming body
    install(RequestValidation) {
        validate<RecommendationInput> { input ->
            println("server side input: $input")
            if (input.messages.any { it.role.isNullOrBlank() || it.content.isNullOrBlank() }) {
                ValidationResult.Invalid("""Key "role" or "content" must by non-empty and not-null""")
            } else {
                ValidationResult.Valid
            }
        }
    }

    routing {
        get("/") {
            call.respondText("""<h1>Welcome to BookMatch backend using KTor</h1>""".trimMargin(), contentType = ContentType.parse("text/html")            )
        }

        rateLimit(RateLimitName("protected")) {

            post("/generate-recommendations") {
                try {
                    val input = call.receive<RecommendationInput>()

                    supabaseClient.auth.importAuthToken(
                        accessToken = input.accessToken, retrieveUser = true,
                        autoRefresh = false
                    )

                    //checks if user is signed-in hence can access the api
                    supabaseClient.auth.currentUserOrNull()?.let { userInfo ->

                        OpenAiClient.generate(input.messages)
                            .onSuccess { openAiResponse ->
                                call.respond(openAiResponse)
                            }.onFailure { error ->
                                call.respond(ErrorResponse("$error"))
                            }


                    } ?: run {
                        call.respond(HttpStatusCode.Unauthorized, ErrorResponse("User not signed-in"))
                    }

                } catch (ex: AuthRestException) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("User not signed-in, ${ex.message}"))
                } catch (ex: BadRequestException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(
                            """Check the request format has correct keys: {
                                    "access_token": "..." ,
                                    "message": [{
                                    "role": "user",
                                    "content": "romance, thriller"
                                    }]
                                }"""
                        )
                    )
                }

            }
        }
    }
}
