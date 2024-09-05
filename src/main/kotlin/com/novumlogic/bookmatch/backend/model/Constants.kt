package com.novumlogic.bookmatch.backend.model

object Constants{
    const val OPENAI_CHAT_COMPLETION_URL = "https://api.openai.com/v1/chat/completions"
    val SYSTEM_INSTRUCTION = """
        You are a book recommending expert with knowledge about all books and specialization in recommending them. 
        When provided with genres, you will give book recommendations for each genre separately, including: Book name, Author name,Genre tags,Book description,no of pages,ISBN,First publication date. 
        For example, if the user says Fiction, Non-fiction, History, provide the list of fictional books followed by non-fictional and then historical books. 
        User preferences, such as liked or disliked books and personal ratings (1-5), will influence future recommendations. 
        For three or fewer genres, provide 1 book per genre. 
        Ensure new recommendations are unique by checking previous suggestions.
    """.trimIndent()

    val RESPONSE_FORMAT = """
         {
        "type": "json_schema",
        "json_schema": {
            "name": "book_recommendation",
            "description": "List of generated book recommendation details seperated genre wise",
            "schema": {
                "type": "object",
                "properties": {
                    "data": {
                        "type": "array",
                        "items": {
                            "type": "object",
                            "properties": {
                                "genre": {
                                    "type": "string",
                                    "description": "Genre associated to a book list",
                                    "enum": [
                                        "fantasy",
                                        "science fiction",
                                        "mystery",
                                        "romance",
                                        "historical fiction",
                                        "thriller",
                                        "horror",
                                        "biography",
                                        "self help",
                                        "history",
                                        "science",
                                        "non fiction",
                                        "young adult",
                                        "graphic novels"
                                    ]
                                },
                                "list": {
                                    "type": "array",
                                    "description": "List of books for particular genre",
                                    "items": {
                                        "type": "object",
                                        "description": "Book details containing different attributes",
                                        "properties": {
                                            "book_name": {
                                                "type": "string",
                                                "description": "Title of the book"
                                            },
                                            "author_name": {
                                                "type": "string",
                                                "description": "Author of the book"
                                            },
                                            "genre_tags": {
                                                "type": "array",
                                                "description": "Different genres to which this book can belong",
                                                "items": {
                                                    "type": "string"
                                                }
                                            },
                                            "description": {
                                                "type": "string",
                                                "description": "A 1 line description for the book"
                                            },
                                            "pages": {
                                                "type": "integer",
                                                "description": "Number of the pages in book"
                                            },
                                            "isbn": {
                                                "type": "string",
                                                "description": "Unique isbn of the book"
                                            },
                                            "first_date_of_publication": {
                                                "type": "string",
                                                "description": "First date on which book was published"
                                            }
                                        },
                                        "additionalProperties": false,
                                        "required": [
                                            "book_name",
                                            "author_name",
                                            "genre_tags",
                                            "description",
                                            "pages",
                                            "isbn",
                                            "first_date_of_publication"
                                        ]
                                    }
                                }
                            },
                            "additionalProperties": false,
                            "required": [
                                "genre",
                                "list"
                            ]
                        }
                    }
                },
                "additionalProperties": false,
                "required": [
                    "data"
                ]
            },
            "strict": true
        }
    }
    """.trimIndent()

}