# gs-tech-challenge

Spring Boot REST API that predicts the **future value of a mutual fund investment** using:

- CAPM (Capital Asset Pricing Model)
- A hardcoded risk-free rate
- Mutual fund beta from Newton Analytics
- Historical average return (last 12 monthly observations) from Newton Analytics

---

## What this project does

This backend exposes two REST endpoints:

1. **List supported mutual funds** (hardcoded catalog)
2. **Calculate future value** for an investment amount and time horizon

The calculation is driven by live market data from Newton APIs and returns a full breakdown of all intermediate values used.

---

## Tech stack

- Java 17
- Spring Boot 3
- Maven

---

## CAPM formula used

The API computes rate and future value as:

- `r = riskFreeRate + beta * (expectedReturnRate - riskFreeRate)`
- `futureValue = principal * (1 + r)^years`

### Variable definitions

- `principal` = initial investment amount
- `years` = investment horizon in years
- `riskFreeRate` = hardcoded US Treasury proxy (`0.0425`)
- `beta` = mutual fund beta relative to S&P 500
- `expectedReturnRate` = annualized return based on monthly average return from last 12 observations

---

## External APIs used

### 1) Newton Stock Beta API

Used to fetch `beta` for the selected mutual fund:

- Example format:
	`https://api.newtonanalytics.com/stock-beta/?ticker=VFIAX&index=%5EGSPC&interval=1mo&observations=12`

### 2) Newton Modern Portfolio API

Used to fetch `averageReturns` (monthly average returns) and extract the selected fund return:

- Docs:
	`https://www.newtonanalytics.com/docs/api/modernportfolio.php`
- Request format used in this app:
	`https://api.newtonanalytics.com/modern-portfolio/?tickers={MUTUAL_FUND},^GSPC&interval=1mo&observations=12`

The app takes the **first item** in `averageReturns` (corresponding to `{MUTUAL_FUND}`), then annualizes it via:

- `annualizedReturn = (1 + monthlyAverageReturn)^12 - 1`

---

## API endpoints

Base URL when running locally: `http://localhost:8080`

### GET `/api/mutual-funds`

Returns the hardcoded supported mutual funds.

#### Example request

```bash
curl -s http://localhost:8080/api/mutual-funds
```

#### Example response

```json
[
	{"ticker":"VFIAX","name":"Vanguard 500 Index Fund Admiral Shares"},
	{"ticker":"FXAIX","name":"Fidelity 500 Index Fund"},
	{"ticker":"SWPPX","name":"Schwab S&P 500 Index Fund"},
	{"ticker":"VTSAX","name":"Vanguard Total Stock Market Index Fund Admiral Shares"},
	{"ticker":"VIGAX","name":"Vanguard Growth Index Fund Admiral Shares"}
]
```

---

### GET `/api/investments/future-value`

Calculates projected future value using CAPM.

#### Query parameters

- `ticker` (required): one of the supported mutual funds
- `principal` (required): positive number (initial investment)
- `years` (required): integer >= 1

#### Example request

```bash
curl -s "http://localhost:8080/api/investments/future-value?ticker=VFIAX&principal=10000&years=5"
```

#### Example response

```json
{
	"ticker":"VFIAX",
	"principal":10000,
	"years":5,
	"riskFreeRate":0.0425,
	"beta":0.2961106082546264,
	"expectedReturnRate":0.27174410154950457,
	"capmRate":0.11038161034860913,
	"futureValue":16879.57
}
```

---

## How to run locally

### Prerequisites

- Java 17+
- Maven 3.9+
- Internet access (for Newton API calls)

### 1) Build

```bash
mvn -DskipTests package
```

### 2) Start the API

```bash
mvn spring-boot:run
```

### 3) Test quickly

```bash
curl -s http://localhost:8080/api/mutual-funds
curl -s "http://localhost:8080/api/investments/future-value?ticker=VFIAX&principal=10000&years=5"
```

---

## Error behavior

The API returns JSON error objects with HTTP status codes.

Common cases:

- `400 Bad Request`
	- Unsupported ticker
	- Invalid/missing query parameters
- `502 Bad Gateway`
	- External Newton API unavailable or malformed response
- `500 Internal Server Error`
	- Unexpected server issue

---

## How to add/edit supported mutual funds

Mutual funds are hardcoded in the catalog service.

When adding a new fund, ensure:

1. It is a valid ticker
2. Newton APIs support the ticker for beta/returns calls

---

## Assumptions and notes

- Risk-free rate is hardcoded by requirement (not dynamically fetched from FRED yet).
- Expected return uses last 12 monthly observations from Newton Modern Portfolio API.
- This is an educational projection tool, not financial advice.

---

## Future improvements (optional)

- Make risk-free rate configurable via environment variable or FRED API.
- Add API-key based configuration for external providers.
- Add unit/integration tests for formula and API client parsing.
