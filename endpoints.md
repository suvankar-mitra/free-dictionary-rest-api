# Free Dictionary API Endpoints

Welcome to the Free Dictionary API! This document outlines the available endpoints and their expected behaviors.

## Base URL
```
https://api.suvankar.cc
```

## Endpoints

### 1. `/dictionaryapi/v1/definitions/en/{word}`

**Method**: `GET`

**Description**: Retrieves definitions for a given word in English.

#### Parameters
- **word**: The word for which definitions are requested (required).
- **compact**: Boolean flag to return compact definitions (default: `false`).

#### Example Request:
```
GET /dictionaryapi/v1/definitions/en/happy?compact=true
```

#### Example Response:
```json
{
  "word": "happy",
  "meanings": [
    {
      "partOfSpeech": "adjective",
      "senses": [
          "glosses": [
            "feeling or showing pleasure and contentment"
          ]
        ]
    }
  ]
}
```

### 2. `/dictionaryapi/v1/words/en/?filter={filter}&page={page}&size={size}`

**Method**: `GET`

**Description**: Retrieves a list of words starting with the specified filter.

#### Parameters
- **filter**: The prefix to filter words by (required).
- **page**: The page number (default: 0).
- **size**: The number of results per page (default: 10).

#### Example Request:
```
GET /dictionaryapi/v1/words/en/?filter=Happ&page=0&size=5
```

#### Example Response:
```json
["Happ","Happe","happed","Happel","Happels"]
```

### 3. `/dictionaryapi/v1/translations/en/?word={word}&pos={pos}`

**Method**: `GET`

**Description**: Retrieves translations for a given word and part of speech.

#### Parameters
- **word**: The word to translate (required).
- **pos**: The part of speech to filter translations by (optional).

#### Example Request:
```
GET dictionaryapi/v1/translations/en/?word=secure&pos=verb
```

#### Example Response:
```json
[
  {
    "lang":"Italian",
    "code":"it",
    "translationSenses":
    [
      {
        "sense":"to get possession of, to acquire",
        "roman":null,
        "word":"ottenere"
      }
    ]
  }
]
```

### 4. `/dictionaryapi/v1/wordoftheday`

**Method**: `GET`

**Description**: Retrieves the definition of a random word from a predefined list.

#### Example Request:
```
GET /dictionaryapi/v1/wordoftheday
```

#### Example Response:
```json
{
  "word":"balance",
  "lang":"en",
  "ipa":"/ˈbæləns/",
  "audioUrl":"...",
  "meanings":[
    {
      "partOfSpeech":"noun",
      "senses":[
        {
          "glosses":
          ["Mental equilibrium; mental health; calmness, a state of remaining clear-headed and unperturbed."]
        }
      ]
    }
  ]
}
```

## Error Handling

The API returns HTTP status codes and error messages when errors occur. Common status codes include:

- `400 Bad Request`: The request was invalid.
- `404 Not Found`: The requested resource was not found.
- `500 Internal Server Error`: An unexpected error occurred on the server.

## Rate Limiting

Requests are rate limited to handle abuse.
