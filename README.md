# Mutual Fund Investment Calculator

A comprehensive full-stack web application designed to help users estimate potential returns on their mutual fund investments using advanced financial analytics and the Capital Asset Pricing Model (CAPM).

## 🎯 Project Overview

This application enables users to:
- Select from a catalog of major mutual funds
- Input their initial investment amount
- Specify their investment time horizon
- Get detailed financial projections including:
  - CAPM-calculated expected return rates
  - Projected future value
  - Total gain and gain percentage
  - Growth charts and a year-by-year breakdown
  - Detailed breakdown of all calculation components
- See a clear note when the data provider's beta looks unrealistic and a standard value is used instead (see [Beta Safety Check](#beta-safety-check))

### Tech Stack

**Backend:**
- Java 25 LTS
- Spring Boot 3.5.16
- Maven 3.9+
- REST API Architecture

**Frontend:**
- Angular 18
- TypeScript
- Reactive Forms
- RxJS
- Chart.js

**Deployment:**
- Docker (single container for site + API)
- Render (free plan)

---

## 📊 Financial Model

### CAPM (Capital Asset Pricing Model)

The application uses the CAPM formula to calculate expected returns:

```
r = Risk-Free Rate + Beta × (Expected Market Return - Risk-Free Rate)
```

### Future Value Calculation

```
FV = Principal × (1 + r)^t
```

**Where:**
- **Principal (P)**: Initial investment amount (USD)
- **r**: CAPM-calculated rate of return (decimal)
- **t**: Investment time horizon (years)
- **FV**: Projected future value (USD)

### Components

- **Risk-Free Rate**: Hardcoded at 4.25% (US Treasury 10-Year proxy)
- **Beta**: Retrieved from Newton Analytics API (mutual fund volatility relative to S&P 500), then checked for plausibility (see below)
- **Expected Return Rate**: Calculated from 5-year S&P 500 historical returns (Newton Analytics API)
- **CAPM Rate**: The calculated expected return for the investment

### Beta Safety Check

Every fund in the catalog is a diversified US stock fund, so its beta against the S&P 500 should be close to 1. Newton Analytics currently returns betas near zero or negative for these funds (for example, about -0.09 for VFIAX, an S&P 500 index fund). Used as-is, that would project returns below the risk-free rate.

The backend (`InvestmentProjectionService`) checks every beta before using it:

| Provider beta | What happens |
|---|---|
| Between 0.5 and 2.0 | Used as-is |
| Below 0.5, above 2.0, or not a number | Replaced with the market beta of **1.0**, and a warning is logged |

When the beta is replaced, the API response includes `"betaAdjusted": true` and the original value in `providerBeta`. The results page tags the beta as **adjusted** and shows a note with the provider's value, so the substitution is never hidden. The 0.5–2.0 range is deliberately wide: real betas for these funds are typically around 0.8–1.2.

---

## 🚀 Quick Start

### Prerequisites

**Backend:**
- Java 25 LTS (see [Windows Setup](#windows-setup) below)
- Maven 3.9+ (macOS: `brew install maven`)

> **Java 25 is required.** The backend compiles for Java 25, so older JDKs (17, 21) fail with `release version 25 not supported`. Check with `java -version`. If you can't install Java 25, use [Docker](#using-docker) instead.

**Frontend:**
- Node.js 18+
- npm 9+
- Angular CLI 18+

### Installation & Running

#### Backend Setup

```bash
# Navigate to project root
cd /path/to/gs-tech-challenge

# Build the project
mvn clean package

# Start the backend server
mvn spring-boot:run
```

The backend will be available at `http://localhost:8080`

#### Frontend Setup

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start the development server
npm start
```

The frontend will be available at `http://localhost:4200`

#### Using Setup Script (macOS/Linux)

```bash
# Make scripts executable
chmod +x setup.sh start.sh test.sh

# Run setup
./setup.sh

# Start both applications
./start.sh
```

#### Using Docker

Docker runs the app without Java or Node installed locally. The backend images use Java 25 (Eclipse Temurin).

```bash
# Start backend (http://localhost:8080) and frontend (http://localhost:4200)
docker compose up
```

To run the whole app as a single container (the same image used in production):

```bash
docker build -t mutual-fund-calculator .
docker run -p 8080:8080 mutual-fund-calculator
# Open http://localhost:8080
```

#### Windows Setup

If `java` and `mvn` are not on your PATH, run the following **once** in PowerShell to set them permanently (no admin required):

```powershell
# 1. Download Maven (no installer needed)
Invoke-WebRequest -Uri "https://dlcdn.apache.org/maven/maven-3/3.9.12/binaries/apache-maven-3.9.12-bin.zip" -OutFile "$env:TEMP\maven.zip"
Expand-Archive "$env:TEMP\maven.zip" -DestinationPath "$env:USERPROFILE\maven" -Force

# 2. Set JAVA_HOME and add Java + Maven to your user PATH permanently
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Users\DELL\Downloads\oracleJdk-25", "User")
$currentPath = [System.Environment]::GetEnvironmentVariable("Path", "User")
[System.Environment]::SetEnvironmentVariable("Path", "$currentPath;C:\Users\DELL\Downloads\oracleJdk-25\bin;C:\Users\DELL\maven\apache-maven-3.9.12\bin", "User")
```

Restart your terminal after running the above. Verify with:

```powershell
java -version
mvn -version
```

> **Note:** The project is configured to support Java 25 via `net.bytebuddy.experimental=true` in `pom.xml`. No extra flags are needed when running `mvn test`.

---

## 📁 Project Structure

```
gs-tech-challenge/
├── src/main/java/com/gs/techchallenge/
│   ├── TechChallengeApplication.java
│   ├── config/
│   │   └── CorsConfig.java                    # CORS configuration
│   ├── controller/
│   │   └── MutualFundController.java          # REST endpoints
│   ├── service/
│   │   ├── MutualFundCatalogService.java     # Fund catalog
│   │   ├── InvestmentProjectionService.java  # CAPM calculations
│   │   └── RiskFreeRateService.java          # Risk-free rate
│   ├── client/
│   │   ├── NewtonAnalyticsClient.java        # Beta data
│   │   └── HistoricalReturnsClient.java      # Historical returns
│   ├── model/
│   │   ├── MutualFund.java
│   │   └── InvestmentProjectionResponse.java
│   └── exception/
│       ├── ExternalApiException.java
│       └── GlobalExceptionHandler.java
├── src/test/java/                            # Comprehensive unit tests
├── src/main/resources/
│   └── application.properties
├── frontend/
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/
│   │   │   │   ├── calculator/               # Main UI component
│   │   │   │   └── investment-chart/         # Growth and gains charts (Chart.js)
│   │   │   ├── services/
│   │   │   │   └── mutual-fund.service.ts   # API service
│   │   │   ├── models/
│   │   │   │   └── investment.model.ts      # Type definitions
│   │   │   ├── pipes/
│   │   │   │   └── currency-format.pipe.ts  # Currency formatting
│   │   │   ├── app.component.ts
│   │   │   ├── app.config.ts
│   │   │   └── app.routes.ts
│   │   ├── environments/                     # Environment config
│   │   └── styles.css                       # Global styles
│   ├── proxy.conf.json                       # Dev server forwards /api to :8080
│   ├── package.json
│   ├── angular.json
│   └── tsconfig.json
├── Dockerfile                                # Full app (site + API) for deployment
├── render.yaml                               # Render deployment settings
├── docker-compose.yml                        # Local backend + frontend containers
├── Dockerfile.backend / Dockerfile.frontend  # Separate backend/frontend images
├── API_DOCUMENTATION.md                      # Full API reference
├── README.md                                 # This file
├── DEVELOPMENT.md                            # Development guidelines
├── TROUBLESHOOTING.md                        # Troubleshooting guide
├── setup.sh                                  # Setup script
├── start.sh                                  # Start script
├── test.sh                                   # Runs backend + frontend tests
└── health-check.sh                           # Checks both servers are up
```

---

## 🔌 REST API Endpoints

### Base URL: `http://localhost:8080/api`

#### 1. Get All Mutual Funds

```http
GET /api/mutual-funds
```

**Response:**
```json
[
  {
    "ticker": "VFIAX",
    "name": "Vanguard 500 Index Fund Admiral Shares"
  },
  {
    "ticker": "FXAIX",
    "name": "Fidelity 500 Index Fund"
  }
]
```

#### 2. Calculate Investment Projection

```http
GET /api/investments/future-value?ticker=VFIAX&principal=10000&years=10
```

**Parameters:**
- `ticker` (required): Mutual fund symbol (case-insensitive)
- `principal` (required): Initial investment amount in USD, greater than 0
- `years` (required): Investment duration, 1–50 years

**Response** (a real response, where the provider's beta was replaced):
```json
{
  "ticker": "VFIAX",
  "principal": 10000,
  "years": 10,
  "riskFreeRate": 0.0425,
  "beta": 1.0,
  "providerBeta": -0.0958,
  "betaAdjusted": true,
  "expectedReturnRate": 0.11997,
  "capmRate": 0.11997,
  "futureValue": 31050.19
}
```

| Field | Meaning |
|---|---|
| `riskFreeRate` | Risk-free rate used (4.25%) |
| `beta` | Beta used in the calculation |
| `providerBeta` | Beta returned by Newton Analytics |
| `betaAdjusted` | `true` if `providerBeta` was unrealistic and 1.0 was used instead |
| `expectedReturnRate` | Expected market return (5-year S&P 500 history) |
| `capmRate` | CAPM expected annual return |
| `futureValue` | Projected value after `years`, rounded to cents |

See [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) for more examples.

---

## 📊 Supported Mutual Funds

| Ticker | Name |
|--------|------|
| VFIAX | Vanguard 500 Index Fund Admiral Shares |
| FXAIX | Fidelity 500 Index Fund |
| SWPPX | Schwab S&P 500 Index Fund |
| VTSAX | Vanguard Total Stock Market Index Fund Admiral Shares |
| VIGAX | Vanguard Growth Index Fund Admiral Shares |

To add more funds, edit `MutualFundCatalogService.java`

---

## 🧪 Testing

### Backend Tests (61 tests)

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=GlobalExceptionHandlerTest
mvn test -Dtest=NewtonAnalyticsClientTest
mvn test -Dtest=HistoricalReturnsClientTest
mvn test -Dtest=MutualFundControllerTest
mvn test -Dtest=InvestmentProjectionServiceTest

# Run all tests in a package
mvn test -Dtest="com.gs.techchallenge.service.*"
```

### Test Coverage

| Test Class | What it covers |
|---|---|
| `MutualFundControllerTest` | REST endpoints, ticker normalisation, unsupported fund rejection |
| `MutualFundControllerWebTest` | Real HTTP requests: missing parameter (400), years over 50 (400), negative amount (400), unknown page (404) |
| `InvestmentProjectionServiceTest` | CAPM formula, future value, beta safety check (fallback to 1.0 and range boundaries) |
| `InvestmentProjectionServiceExtendedTest` | Edge cases: time horizons, high beta, large amounts, precision |
| `MutualFundCatalogServiceTest` | Fund catalog, supported tickers, case-insensitive lookup |
| `RiskFreeRateServiceTest` | Risk-free rate value and consistency |
| `GlobalExceptionHandlerTest` | HTTP error responses for all exception types (400, 502, 500) |
| `NewtonAnalyticsClientTest` | Beta JSON parsing for all response shapes, error handling |
| `HistoricalReturnsClientTest` | Historical return parsing, annualisation formula, error handling |

### Frontend Tests (27 tests)

Run these from the `frontend` folder. `npx` uses the project's own Angular CLI, so no global install is needed.

```bash
# Run all tests once in headless Chrome
npx ng test --watch=false --browsers=ChromeHeadless

# Run in watch mode (re-runs on save)
npm test

# Run with coverage report
npx ng test --watch=false --browsers=ChromeHeadless --code-coverage
```

They cover the calculator page (form validation, calculations, gain figures, the beta note), the API service, and the currency pipe.

### Using Test Script

```bash
./test.sh  # Runs backend tests, then frontend tests (needs Maven and Chrome)
```

---

## 🌐 External APIs

### Newton Analytics API

**Beta Endpoint:**
```
https://api.newtonanalytics.com/stock-beta/?ticker=VFIAX&index=^GSPC&interval=1mo&observations=12
```

**Returns Endpoint:**
```
https://api.newtonanalytics.com/modern-portfolio/?tickers=^GSPC,VFIAX&interval=1mo&observations=60
```

---

## 🔒 Error Handling

Every error returns the same JSON shape from `GlobalExceptionHandler`:

| Status | When | `error` message |
|--------|------|-----------------|
| 200 | Success | — |
| 400 | Unsupported ticker | `Unsupported mutual fund ticker: XYZ` |
| 400 | Missing parameter | `Missing required parameter: principal` |
| 400 | Invalid value (not a number, amount ≤ 0, years outside 1–50) | `Invalid request parameters` |
| 404 | Unknown URL | `Not found` |
| 502 | Newton Analytics unreachable or returned bad data | Details of the upstream failure |
| 500 | Anything unexpected | `Unexpected server error` |

Example error response:
```json
{
  "timestamp": "2026-11-05T16:46:09.170Z",
  "status": 400,
  "error": "Unsupported mutual fund ticker: FAKE"
}
```

---

## ☁️ Deployment

The app is deployed on [Render](https://render.com) as a single Docker service:

- The root `Dockerfile` builds the Angular site, bundles it into the Spring Boot jar, and runs it. The website and the API share one URL, so no CORS setup is needed.
- `render.yaml` holds the Render settings (free plan, health check on `/api/mutual-funds`).
- Every push to `main` redeploys automatically.

> **Note:** On Render's free plan the app sleeps after 15 minutes without visits. The first visit after that takes about a minute to load. Open the link a minute before a demo.

To deploy your own copy:

[![Deploy to Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy?repo=https://github.com/Space-Paragon1/gs-tech-challenge-Mutual-Fund-Calculator-)

---

## 🔧 Configuration

### Backend (application.properties)
```properties
spring.application.name=gs-tech-challenge
server.port=${PORT:8080}
```

The port defaults to 8080. Hosting platforms like Render set the `PORT` environment variable instead.

### Frontend (environment.ts)
```typescript
export const environment = {
  production: false,
  apiUrl: ''
};
```

The frontend calls the API at the relative path `/api`. In development, `ng serve` forwards those calls to `http://localhost:8080` (see `frontend/proxy.conf.json`). In production, Spring Boot serves the site and the API from the same address.

---

## 📚 Learning Resources

- [CAPM Explanation - Investopedia](https://www.investopedia.com/terms/c/capm.asp)
- [Mutual Funds Guide](https://www.investopedia.com/terms/m/mutualfund.asp)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Angular Official Tutorial](https://angular.dev)
- [Newton Analytics API Docs](https://www.newtonanalytics.com/docs/)

---

## 💡 Key Features

✅ **User-Friendly Interface**: Clean, responsive design with intuitive controls
✅ **Real-time Calculations**: Live data from Newton Analytics API
✅ **Data Quality Check**: Unrealistic betas from the data provider are detected, replaced with 1.0, and flagged on the page
✅ **Comprehensive Results**: Growth charts, yearly breakdown, and every CAPM input shown
✅ **Responsive Design**: Works on desktop, tablet, and mobile devices
✅ **Error Handling**: Clear 400/404/502 responses with consistent JSON
✅ **Tested**: 61 backend tests and 27 frontend tests
✅ **Deployed**: Runs on Render as a single Docker container
✅ **Well Documented**: Extensive README, API reference, development guide, and troubleshooting docs

---

## 🚀 Bonus Features (For Enhancement)

- [ ] Multiple fund comparison
- [ ] Portfolio allocation suggestions
- [ ] Historical performance charts
- [ ] Investment history tracking with database
- [ ] ETF support
- [ ] Advanced analytics
- [ ] User authentication
- [ ] API rate limiting

---

## 🤝 Contributing

1. Read [DEVELOPMENT.md](./DEVELOPMENT.md) for guidelines
2. Create a feature branch
3. Make your changes
4. Write/update tests
5. Create a pull request

---

## 📋 Learning Outcomes

Upon completing this project, you will understand:

✅ CAPM financial model and investment calculations
✅ Full-stack web development (Java + Angular)
✅ RESTful API design and implementation
✅ Database-less application architecture
✅ External API integration
✅ Unit testing and test coverage
✅ Reactive programming with RxJS
✅ Responsive web design
✅ Error handling and validation

---

## 📝 Changelog

### 1.1.0 (November 2026)

**Upgrades**
- Backend moved from Java 17 to **Java 25 LTS**, and Spring Boot from 3.3.8 to **3.5.16**
- Docker images updated to Java 25 (Eclipse Temurin); Maven 3.9+ now required

**Deployment**
- Site and API now ship as **one Docker container** (`Dockerfile`), so they share a single URL
- Added Render deployment settings (`render.yaml`); every push to `main` redeploys
- The server port can be set with the `PORT` environment variable (default 8080)

**New**
- **Beta safety check**: unrealistic betas from Newton Analytics are replaced with the market beta of 1.0. The API returns `providerBeta` and `betaAdjusted`, and the results page shows a note.

**Fixes**
- Total Gain, gain %, and Principal on the results page used the amount typed in the box instead of the amount that was calculated, so editing the amount after calculating showed wrong numbers
- A missing request parameter returned 500; it now returns 400 with the parameter name
- An unknown URL returned 500; it now returns 404
- `years` above 50 was accepted by the API (only the form blocked it); it now returns 400
- 3 failing frontend tests fixed (mistakes in the test code)
- `test.sh` runs frontend tests with `npx` in headless Chrome, so a global Angular CLI isn't needed

**Tests**
- Backend: 45 → 61 tests. Frontend: 24 → 27 tests. All pass.

---

## 📄 License

This project is part of the GS Tech Challenge curriculum.

---

## 🆘 Need Help?

1. Check [TROUBLESHOOTING.md](./TROUBLESHOOTING.md) for common issues
2. Review [DEVELOPMENT.md](./DEVELOPMENT.md) for coding guidelines
3. Check the frontend [README](./frontend/README.md) for additional documentation
4. Review code comments and test files for examples

---

**Last Updated**: November 5, 2026
**Version**: 1.1.0
