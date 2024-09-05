# BookMatch-Ktor-Backend

Welcome to the BookMatch Backend API repository! This project serves as a secure gateway or proxy to call the OpenAI API used in [BookMatch project](https://github.com/novumlogic/BookMatch), a book recommendation app using generative AI. The backend is built using **Kotlin and the Ktor framework**, specifically for the BookMatch project, but the idea of securing API keys and not exposing them at client side code can be used for any project.

## Tech Stack:

- _Ktor:_ Used for building the API and handling server-side logic.
- _OpenAI:_ Utilized for generating recommendation content.
- _Supabase Auth_: For authenticating and authorizating the API.


### Ktor Plugins Used:
| Plugin | Description |
|---|---|
| [Request Validation](https://ktor.io/docs/server-request-validation.html) | Validates incoming requests to ensure data integrity and adherence to expected formats. |
| [Rate Limiting](https://ktor.io/docs/server-rate-limit.html) | Manages and limits the number of requests to prevent abuse and ensure fair usage. |
| [Status Pages](https://ktor.io/docs/server-status-pages.html) | Provides custom error pages and responses for various HTTP status codes, improving debugging and user experience. |
| [Routing ](https://ktor.io/docs/server-routing.html)| Defines and handles API routes, facilitating request handling and routing logic. |
| [Content Serialization](https://ktor.io/docs/server-serialization.html) | Manages JSON serialization and deserialization for efficient data exchange between client and server. |



## Features:
- **Secure API Key Handling:** Keeps OpenAI API keys on the server-side to prevent exposure.
- **Proxy Requests:** Forwards client requests to OpenAI while managing all authentication and security.
- **Kotlin & Ktor Framework:** Uses Kotlin with the Ktor framework for an efficient and lightweight backend.


## Workflow: 

```mermaid
graph TD
    A[Client Request] --> B{Rate Limiting Check}
    B -->|Limit Exceeded| C[Return Rate Limit Error]
    B -->|Within Limit| D{Request Validation}
    D -->|Invalid| E[Return Error Response]
    D -->|Valid| F{Supabase Auth}
    F -->|Unauthenticated| G[Return Authentication Error]
    F -->|Authenticated| H[Process Request]
    H --> I{Call OpenAI API}
    I -->|Error| J[Handle OpenAI Error]
    I -->|Success| K[Process OpenAI Response]
    K --> L[Prepare Response]
    J --> L
    L --> M[Return Response to Client]
```

## Project Structure:

```
src/
└── main/
    ├── kotlin/
    │   └── com.novumlogic.bookmatch.backend/
    │       ├── data/
    │       │   └── OpenAiClient
    │       ├── model/
    │       │   ├── request/
    │       │   │   ├── OpenAiRequest.kt
    │       │   │   └── RecommendationRequest.kt
    │       │   └── response/
    │       │       ├── ErrorResponse
    │       │       └── OpenAiResponse.kt
    │       ├── plugins/
    │       │   ├── Routing.kt
    │       │   └── Serialization.kt
    │       ├── Constants
    │       └── Application.kt
    └── resources/
        ├── application.yaml
        └── logback.xml
```

Here's a brief overview of the main components:

- data/: Contains the OpenAiClient which has logic for interacting with the OpenAI API.
- model/: Holds data models for requests and responses.
- plugins/: Includes Routing and Serialization configurations.
- Constants: Contains project-wide constant values.
- Application.kt: The main entry point of the application.
- resources/: Contains configuration files like application.yaml and logback.xml for logging.
  
## Getting Started

### Prerequisites:

- Intellj Idea: Provides JDK and Gradle (dependency management and build automation tool) to run the project.
- OpenAI Chat Completion API: You need an API key from OpenAI to use this services.
- Supabase Auth: For Authenticating API you need to setup supabase project 

### Installation:

1. Clone the repository:
```bash
git clone https://github.com/novumlogic/BookMatch-Ktor-Backend.git
cd BookMatch-Ktor-Backend
```
2. Set up Environment Variables:
    - In your Intellij Idea IDE, from **main menu** select **Edit Configurations** and your variables
    - ![Image depicts how to configure the environment variables](src/main/resources/envconfig.png)

3. Build the project:
```bash
./gradlew build
```



### Usage

- Starting the Server, run the server using the following command:
```bash
./gradlew run
```

The server will start on http://localhost:8080 by default.

#### Making Requests

Send POST requests to the /generate-recommendations endpoint with the necessary payload to interact with the OpenAI API.


```curl 
curl -X POST http://localhost:8080/generate-recommendations \
-H "Content-Type: application/json" \
-d '{
    "access_token": "eyJabcdiOiJIUzI1Ni12345ZCI6IjZjNHJKeUtSK1ZLeW654msiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2htcG55anRncWtramd1dGxsc3pwLnN1cGFiYXNlLmNvL2F1dGgvdjEiLCJzdWIiOiI0NWE5NDk5ZS1iY2E1LTQ5YTQtOTI2NS1lN2VkODY4NzI4MjAiLCJhdWQiOiJhdXRoZW50aWNhdGVkIiwiZXhwIjoxNzI1MjkwMDk1LCJpYXQiOjE3MjUyODY0OTUsImVtYWlsIjoiZGhhbmFuamF5Lm5hdmxhbmlAbm92dW1sb2dpYy5jb20iLCJwaG9uZSI6IiIsImFwcF9tZXRhZGF0YSI6eyJwcm92aWRlciI6Imdvb2dsZSIsInByb3ZpZGVycyI6WyJnb29nbGUiXX0sInVzZXJfbWV0YWRhdGEiOnsiYXZhdGFyX3VybCI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hL0FDZzhvY0tRMXhOTHZUY2Iyc0tsalJmd1kxNDctSElSazZFRmVTMjRaRkFqcl95T0phQlBDZz1zOTYtYyIsImVtYWlsIjoiZGhhbmFuamF5Lm5hdmxhbmlAbm92dW1sb2dpYy5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiZnVsbF9uYW1lIjoiRGhhbmFuamF5IE5hdmxhbmkiLCJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJuYW1lIjoiRGhhbmFuamF5IE5hdmxhbmkiLCJwaG9uZV92ZXJpZmllZCI6ZmFsc2UsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS9BQ2c4b2NLUTF4Tkx2VGNiMnNLbGpSZndZMTQ3LUhJUms2RUZlUzI0WkZBanJfeU9KYUJQQ2c9czk2LWMiLCJwcm92aWRlcl9pZCI6IjExMTM5ODAwOTQ3NzA1NjkxNDgwMiIsInN1YiI6IjExMTM5ODAwOTQ3NzA1NjkxNDgwMiJ9LCJyb2xlIjoiYXV0aGVudGljYXRlZCIsImFhbCI6ImFhbDEiLCJhbXIiOlt7Im1ldGhvZCI6Im9hdXRoIiwidGltZXN0YW1wIjoxNzI1MjYwNTc1fV0sInNlc3Npb25faWQiOiI4YmVkMjQ0OS1lYzAyLTQ2MmItOThlMi02ODEyM2QwM2YzMjkiLCJpc19hbm9ueW1vdXMiOmZhbHNlfQ.AeG2PFT2tCF003eNa2Jkv3IXXPjNWyncyUMq8RDl--E" ,
    "messages": [{
    "role": "user",
    "content": "romance, thriller"
    }]
}'
```
Example Response

```json
{
  "data": [
    {
      "genre": "romance",
      "list": [
        {
          "book_name": "Pride and Prejudice",
          "author_name": "Jane Austen",
          "genre_tags": [
            "romance",
            "classic",
            "fiction"
          ],
          "description": "A classic novel that explores the themes of love, reputation, and class in 19th century England as Elizabeth Bennet navigates issues of morality, education, and marriage.",
          "pages": "432",
          "isbn": "978-1503290563",
          "first_date_of_publication": "1813-01-28"
        }
      ]
    },
    {
      "genre": "thriller",
      "list": [
        {
          "book_name": "The Girl with the Dragon Tattoo",
          "author_name": "Stieg Larsson",
          "genre_tags": [
            "thriller",
            "mystery",
            "crime"
          ],
          "description": "A gripping modern mystery revolving around a journalist and a hacker as they investigate a decades-old disappearance, uncovering dark secrets along the way.",
          "pages": "465",
          "isbn": "978-0307949486",
          "first_date_of_publication": "2005-08-01"
        }
      ]
    }
  ]
}
```

### API Endpoints

POST /generate-recommendations: Generate book recommendations based on user preferences.

- Request Payload:
    - access_token: JWT token provided by supabase auth to authenticate the request 
    - messages: List of messages to provide context to OpenAI APIs

- Response:
    - Returns a JSON object containing book recommendations for each genre.


## Contributing
We welcome contributions from the community. Please fork the repository and create a pull request with your changes.

***

Feel free to reach out if you have any questions or need further assistance. Enjoy discovering your next favorite book with BookMatch