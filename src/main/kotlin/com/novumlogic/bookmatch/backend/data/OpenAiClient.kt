package com.novumlogic.bookmatch.backend.data

import com.novumlogic.bookmatch.backend.model.Constants
import com.novumlogic.bookmatch.backend.model.request.Message
import com.novumlogic.bookmatch.backend.model.response.OpenAIResponse
import com.novumlogic.bookmatch.backend.model.request.OpenAiRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object OpenAiClient {
    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }
    private val client = HttpClient{
        install(Logging){
            level = LogLevel.BODY
        }
        install(ContentNegotiation){
            json(json)
        }
    }

    private val apiKey = System.getenv("OPENAI_APIKEY")

    private val requestBody = OpenAiRequest(
        model = "gpt-4o-mini",
        responseFormat = json.parseToJsonElement(Constants.RESPONSE_FORMAT),
        messages = mutableListOf(Message("system",Constants.SYSTEM_INSTRUCTION))
    )


    suspend fun generate(input: List<Message>): Result<OpenAIResponse>{

        //adding message from 2nd index to maintain system prompt
        requestBody.messages.addAll(1, input)
        //maintaining the message size array upto added input
        if(input.size + 1 < requestBody.messages.size){
            val toRemove = requestBody.messages.subList(input.size+1, requestBody.messages.size)
            requestBody.messages.removeAll(toRemove)
        }
        println("Checking the openai input messages: ${requestBody.messages} ")

        return try {

            val httpResponse: HttpResponse = client.post(Constants.OPENAI_CHAT_COMPLETION_URL){
                header("Authorization", apiKey)
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            if(httpResponse.status.value in 200..299){
                //NoTransformationFoundException - If no transformation is found for the type T.
                //DoubleReceiveException - If already called body.
                val response = httpResponse.body<OpenAIResponse>()

                Result.success(response)

            } else{
                Result.failure(Exception("OpenAI API failed: ${httpResponse.status}\n${httpResponse.bodyAsText()}"))
            }

        }catch (ex: Exception){
             Result.failure(ex)
        }

    }
}