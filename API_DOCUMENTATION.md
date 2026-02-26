# API Documentation

## Base URL

```
http://localhost:8080/api
```

## Endpoints Overview

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/mutual-funds` | Get list of supported mutual funds |
| GET | `/investments/future-value` | Calculate future investment value |

---

## 1. Get Mutual Funds

Returns a list of all supported mutual funds in the catalog.

### Request

```http
GET /api/mutual-funds HTTP/1.1
Host: localhost:8080
```

### cURL Example

```bash
curl -X GET "http://localhost:8080/api/mutual-funds"
```

### JavaScript/Fetch Example

```javascript
fetch('http://localhost:8080/api/mutual-funds')
  .then(response => response.json())
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));
```

### TypeScript/Angular Example

```typescript
import { HttpClient } from '@angular/common/http';

constructor(private http: HttpClient) {}

getMutualFunds() {
  return this.http.get<MutualFund[]>('/api/mutual-funds');
}
```

### Response

**Status Code:** `200 OK`

**Content-Type:** `application/json`

**Body:**
```json
[
  {
    "ticker": "VFIAX",
    "name": "Vanguard 500 Index Fund Admiral Shares"
  },
  {
    "ticker": "FXAIX",
    "name": "Fidelity 500 Index Fund"
  },
  {
    "ticker": "SWPPX",
    "name": "Schwab S&P 500 Index Fund"
  },
  {
    "ticker": "VTSAX",
    "name": "Vanguard Total Stock Market Index Fund Admiral Shares"
  },
  {
    "ticker": "VIGAX",
    "name": "Vanguard Growth Index Fund Admiral Shares"
  }
]
```

### Status Codes

| Code | Meaning |
|------|---------|
| 200 | Successfully retrieved list of mutual funds |
| 500 | Internal server error |

---

## 2. Calculate Future Investment Value

Calculates the projected future value of an investment using the CAPM model.

### Request

```http
GET /api/investments/future-value?ticker=VFIAX&principal=10000&years=5 HTTP/1.1
Host: localhost:8080
```

### Query Parameters

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `ticker` | string | Yes | Mutual fund ticker symbol (uppercase) | `VFIAX` |
| `principal` | number | Yes | Initial investment amount in USD | `10000` |
| `years` | integer | Yes | Investment duration in years (1-50) | `5` |

### cURL Examples

**Basic example:**
```bash
curl -X GET "http://localhost:8080/api/investments/future-value?ticker=VFIAX&principal=10000&years=5"
```

**With verbose output:**
```bash
curl -v -X GET "http://localhost:8080/api/investments/future-value?ticker=VFIAX&principal=10000&years=5"
```

**Pretty-printed JSON:**
```bash
curl -X GET "http://localhost:8080/api/investments/future-value?ticker=VFIAX&principal=10000&years=5" | jq '.'
```

### JavaScript/Fetch Example

```javascript
const ticker = 'VFIAX';
const principal = 10000;
const years = 5;

const url = new URL('/api/investments/future-value', 'http://localhost:8080');
url.searchParams.append('ticker', ticker);
url.searchParams.append('principal', principal.toString());
url.searchParams.append('years', years.toString());

fetch(url)
  .then(response => response.json())
  .then(data => console.log('Projection:', data))
  .catch(error => console.error('Error:', error));
```

### TypeScript/Angular Example

```typescript
import { HttpClient } from '@angular/common/http';

constructor(private http: HttpClient) {}

getProjection(ticker: string, principal: number, years: number) {
  return this.http.get<InvestmentProjection>(
    '/api/investments/future-value',
    {
      params: {
        ticker: ticker,
        principal: principal.toString(),
        years: years.toString()
      }
    }
  );
}

// Usage
this.getProjection('VFIAX', 10000, 5).subscribe(
  (result) => {
    console.log('Future Value:', result.futureValue);
    console.log('CAPM Rate:', (result.capmRate * 100).toFixed(2) + '%');
  },
  (error) => {
    console.error('API Error:', error);
  }
);
```

### Response

**Status Code:** `200 OK`

**Content-Type:** `application/json`

**Body:**
```json
{
  "ticker": "VFIAX",
  "principal": 10000,
  "years": 5,
  "riskFreeRate": 0.0425,
  "beta": 0.95,
  "expectedReturnRate": 0.10,
  "capmRate": 0.1103,
  "futureValue": 16879.57
}
```

### Response Fields Explanation

| Field | Type | Description |
|-------|------|-------------|
| `ticker` | string | The mutual fund ticker |
| `principal` | number | The initial investment amount |
| `years` | integer | The investment duration |
| `riskFreeRate` | number | US Treasury 10-year rate (4.25%) |
| `beta` | number | Fund's volatility relative to S&P 500 |
| `expectedReturnRate` | number | 5-year S&P 500 average annual return |
| `capmRate` | number | Calculated expected annual return (CAPM) |
| `futureValue` | number | Projected value after specified years |

### Calculation Example

```
Input:
  principal = $10,000
  ticker = VFIAX
  years = 5

API Retrieves:
  riskFreeRate = 0.0425 (4.25%)
  beta = 0.95 (from Newton Analytics)
  expectedReturnRate = 0.10 (10% from Newton Analytics)

Calculation:
  capmRate = 0.0425 + 0.95 × (0.10 - 0.0425)
           = 0.0425 + 0.95 × 0.0575
           = 0.0425 + 0.054625
           = 0.097125 (9.7125%)

  futureValue = 10,000 × (1 + 0.097125)^5
              = 10,000 × 1.6088
              = $16,088.00

Output:
  futureValue = $16,088.00
```

### Status Codes

| Code | Meaning | Example |
|------|---------|---------|
| 200 | Successfully calculated | Projection returned |
| 400 | Bad Request | Invalid ticker, principal, or years |
| 500 | Server Error | External API failure |

### Error Examples

**Invalid Ticker:**
```json
{
  "timestamp": "2026-02-26T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Unsupported mutual fund ticker: INVALID",
  "path": "/api/investments/future-value"
}
```

**Missing Parameters:**
```json
{
  "timestamp": "2026-02-26T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Required request parameter 'principal' for method parameter type BigDecimal is not present",
  "path": "/api/investments/future-value"
}
```

**Invalid Principal:**
```json
{
  "timestamp": "2026-02-26T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "must be greater than 0",
  "path": "/api/investments/future-value"
}
```

**External API Failure:**
```json
{
  "timestamp": "2026-02-26T10:30:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Unable to fetch beta from Newton Analytics",
  "path": "/api/investments/future-value"
}
```

---

## Parameter Validation Rules

### Ticker
- **Type:** string
- **Required:** Yes
- **Valid Values:** VFIAX, FXAIX, SWPPX, VTSAX, VIGAX
- **Case Handling:** Automatically converted to uppercase
- **Example:** "vfiax" → "VFIAX"

### Principal
- **Type:** number (BigDecimal)
- **Required:** Yes
- **Minimum:** Greater than 0
- **Maximum:** No upper limit (practical: up to millions)
- **Examples:** 100, 1000, 10000, 1000000

### Years
- **Type:** integer
- **Required:** Yes
- **Minimum:** 1
- **Maximum:** 50
- **Examples:** 1, 5, 10, 30

---

## Rate Limiting

Currently, there is no rate limiting implemented. External API calls are subject to Newton Analytics' own rate limits.

---

## CORS Header

The API includes CORS headers to allow requests from:
- `http://localhost:4200` (Angular dev server)
- `http://localhost:3000`
- `http://127.0.0.1:4200`

See `CorsConfig.java` for configuration.

---

## API Versions

**Current Version:** 1.0.0
**Stable:** Yes
**Deprecated Features:** None

---

## Support & Documentation

- **Full Documentation:** See [README.md](../README.md)
- **Troubleshooting:** See [TROUBLESHOOTING.md](../TROUBLESHOOTING.md)
- **Development Guide:** See [DEVELOPMENT.md](../DEVELOPMENT.md)
- **Code Examples:** Check test files in `src/test/`

---

*Last Updated: February 26, 2026*
