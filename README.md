# Free Dictionary API

A free, open-source API for accessing dictionary data, powered by [Wiktionary](https://en.wiktionary.org). This project
provides a RESTful API to query word definitions, pronunciations, etymologies, and more, with data sourced and processed
from Wiktionary.

No authentication required. Free to use under CC BY-SA 4.0.

**Base URL:**  
https://api.suvankar.cc

<div>
  <a href="https://api.suvankar.cc/docs/dictionary">
    <img src="https://img.shields.io/badge/📖_API_Documentation-2563EB?style=for-the-badge" alt="API Documentation">
  </a>
</div>

---

## Support This Project

If you find this API useful, consider supporting its development and infrastructure costs. Your support helps keep the
API free and available to everyone.

<div>
  <a href="https://buymeatea.online/suvankar">
    <img src="https://img.shields.io/badge/🍵_Support_via_UPI-138A50?style=for-the-badge" alt="Support via UPI">
  </a>
  <a href="https://buymeacoffee.com/suvankarmitra">
    <img src="https://img.shields.io/badge/☕_Buy_Me_a_Coffee-FFDD00?style=for-the-badge&logo=buymeacoffee&logoColor=black" alt="Buy Me a Coffee">
  </a>
</div>

---

## Quick Example

### Request

`GET /dictionaryapi/v1/definitions/en/happy?compact=true`

### Response

```json
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
- [Interactive API Documentation](https://api.suvankar.cc/docs/dictionary)

## Data Source Note

The dictionary data is sourced from Wiktionary.

This repository contains the API layer only.  
The data ingestion and processing pipeline that imports Wiktionary data into the database is maintained separately and
is not part of this repository.

## Licensing

**Code:** Licensed under the [Apache License 2.0](LICENSE.md).  
You are free to use, modify, and redistribute the code with attribution.

**Data:** The dictionary data is sourced from [Wiktionary](https://en.wiktionary.org) and is licensed
under [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/).  
Any redistribution of the data must comply with CC BY-SA 4.0 terms.

Copyright 2026 Suvankar Mitra

---

**Project by [Suvankar](https://hello.suvankar.cc)**