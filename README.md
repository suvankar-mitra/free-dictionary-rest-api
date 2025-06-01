# Free Dictionary API

A free, open-source API for accessing dictionary data, powered by [Wiktionary](https://en.wiktionary.org). This project provides a RESTful API to query word definitions, pronunciations, etymologies, and more, with data sourced and processed from Wiktionary.

## Features

- Fast word lookup with language support (currently only supporting English)
- Pronunciation, IPA, and audio URLs
- Etymology, synonyms, antonyms, and related words
- REST API with JSON responses
- Caching with Redis or in-memory options
- Docker and cloud-ready

## Quick Start

### Prerequisites

- Java 17+
- Maven
- (Optional) Docker and Docker Compose
- (Optional) Redis (for production caching)

### Build and Run

#### Local (H2 Database, In-Memory Cache)

```sh
mvn clean package
SPRING_PROFILES_ACTIVE=h2 java -jar target/app.jar
```

#### Docker (Postgres, Redis)

```sh
docker-compose up --build
```

#### Test

```sh
mvn test
```

### API Usage

- **GET** `/dictionaryapi/v1/definitions/en/example`  
  Returns definitions and related data for the given word.


## Configuration

Configuration is managed via Spring profiles:

- `h2` (default): Uses H2 file database and in-memory cache.
- `docker`: Uses PostgreSQL and Redis (see `application-docker.yml`).
- `test`: Uses H2 in-memory and simple cache for tests.

You can override config via environment variables, e.g.:
```sh
export SPRING_PROFILES_ACTIVE=docker
export SPRING_DATASOURCE_USERNAME=...
```

## License & Attribution

[![License: CC BY-SA 4.0](https://licensebuttons.net/l/by-sa/4.0/88x31.png)](https://creativecommons.org/licenses/by-sa/4.0/)

This project incorporates content from Wiktionary, which is licensed under the Creative Commons Attribution-ShareAlike 4.0 International (CC BY-SA 4.0) license: https://creativecommons.org/licenses/by-sa/4.0/

The API and all code in this repository are also licensed under the same license:

**Creative Commons Attribution-ShareAlike 4.0 International (CC BY-SA 4.0)**  
https://creativecommons.org/licenses/by-sa/4.0/

You are free to:
- **Share** — copy and redistribute the material in any medium or format
- **Adapt** — remix, transform, and build upon the material for any purpose, even commercially

Under the following terms:
- **Attribution** — You must give appropriate credit to both free-dictionary-rest-api (https://api.suvankar.cc) and Wiktionary (https://en.wiktionary.org), provide a link to the license, and indicate if changes were made.
- **ShareAlike** — If you remix, transform, or build upon the material, you must distribute your contributions under the same license as the original.

**Attribution Example:**  
Data provided by [free-dictionary-rest-api](https://api.suvankar.cc) and [Wiktionary](https://en.wiktionary.org), licensed under [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/)

---

**Project by [Suvankar](https://hello.suvankar.cc)**