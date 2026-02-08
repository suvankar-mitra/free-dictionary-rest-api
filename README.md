# Free Dictionary API

A free, open-source API for accessing dictionary data, powered by [Wiktionary](https://en.wiktionary.org). This project provides a RESTful API to query word definitions, pronunciations, etymologies, and more, with data sourced and processed from Wiktionary.

No authentication required. Free to use under CC BY-SA 4.0.

Base URL:
https://api.suvankar.cc

## Quick Example

### Request
`GET /dictionaryapi/v1/definitions/en/happy?compact=true`
### Response:
``` JSON
{
  "word": "happy",
  "meanings": [
    {
      "partOfSpeech": "adjective",
      "senses": [
        {
          "glosses": [
            "feeling or showing pleasure and contentment"
          ]
        }
      ]
    }
  ]
}
```

## API Endpoints

Full endpoint documentation:
- [API Endpoints](endpoints.md)

## Data Source Note

The dictionary data is sourced from Wiktionary.

This repository contains the API layer only.  
The data ingestion and processing pipeline that imports Wiktionary data into the database is maintained separately and is not part of this repository.


## License

CC BY-SA 4.0 (same as Wiktionary). Attribution required.
See [LICENSE](LICENSE.md) for details.

---

**Project by [Suvankar](https://hello.suvankar.cc)**