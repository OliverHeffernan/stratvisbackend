# StratVis Backend (Java + Spring Boot)

REST API that accepts a high-altitude image and returns environmental metrics JSON generated via the OpenAI API (`gpt-4o`).

## Requirements

- Java 17+
- Maven 3.9+

## Setup

1. Copy `.env.example` to `.env`
2. Put your key in `.env`:

```bash
OPENAI_API_KEY=your_real_key_here
```

The app reads `OPENAI_API_KEY` from either:
- an exported environment variable, or
- the local `.env` file

## Run

```bash
mvn spring-boot:run
```

Server starts on `http://localhost:8080`.

## API

### `POST /api/v1/analyze`

Multipart form field:
- `image` (required): jpg/png/etc

Example:

```bash
curl -X POST "http://localhost:8080/api/v1/analyze" \
  -F "image=@/absolute/path/to/image.jpg"
```

Response: JSON object in the structure specified by your prompt.
