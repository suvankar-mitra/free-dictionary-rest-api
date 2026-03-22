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

## Licensing

**Code:** Licensed under the [Apache License 2.0](LICENSE.md).  
You are free to use, modify, and redistribute the code with attribution.

**Data:** The dictionary data is sourced from [Wiktionary](https://en.wiktionary.org)  
and is licensed under [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/).  
Any redistribution of the data must comply with CC BY-SA 4.0 terms.

Copyright 2026 Suvankar Mitra

---

**Project by [Suvankar](https://hello.suvankar.cc)**