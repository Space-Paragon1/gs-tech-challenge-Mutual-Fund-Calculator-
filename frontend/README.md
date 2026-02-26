# Mutual Fund Investment Calculator

A full-stack web application designed to help users estimate potential returns on their mutual fund investments using advanced financial analytics.

## 🎯 Features

- **Mutual Fund Selection**: Choose from a curated list of major mutual funds
- **Investment Projection**: Calculate future investment values using the Capital Asset Pricing Model (CAPM)
- **Technical Analysis**: Displays risk-free rate, fund beta, expected returns, and CAPM rate
- **User-Friendly Interface**: Clean, modern UI with responsive design
- **Real-time Calculations**: Integrates with external APIs for accurate beta and return data

## 📚 Key Formulas

### Capital Asset Pricing Model (CAPM)
```
r = Risk-Free Rate + Beta × (Expected Return Rate - Risk-Free Rate)
```

### Future Value Calculation
```
FV = Principal × (1 + r)^t
```

Where:
- **Principal (P)**: Initial investment amount
- **r**: CAPM rate (calculated rate of return)
- **t**: Time in years

## 📋 Prerequisites

### Backend
- Java 17+
- Maven 3.8+
- Spring Boot 3.3.8

### Frontend
- Node.js 18+
- npm 9+
- Angular 18+

## 🚀 Quick Start

### Backend Setup

1. **Navigate to project root**
   ```bash
   cd /path/to/gs-tech-challenge
   ```

2. **Build the project**
   ```bash
   mvn clean package
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start the development server**
   ```bash
   npm start
   ```

The frontend will be available at `http://localhost:4200`

## 📁 Project Structure

### Backend
```
src/main/java/com/gs/techchallenge/
├── TechChallengeApplication.java
├── config/
│   └── CorsConfig.java
├── controller/
│   └── MutualFundController.java
├── model/
│   ├── MutualFund.java
│   └── InvestmentProjectionResponse.java
├── service/
│   ├── MutualFundCatalogService.java
│   ├── InvestmentProjectionService.java
│   └── RiskFreeRateService.java
├── client/
│   ├── NewtonAnalyticsClient.java
│   └── HistoricalReturnsClient.java
└── exception/
    ├── ExternalApiException.java
    └── GlobalExceptionHandler.java
```

### Frontend
```
frontend/
├── src/
│   ├── app/
│   │   ├── components/
│   │   │   └── calculator/
│   │   │       ├── calculator.component.ts
│   │   │       ├── calculator.component.html
│   │   │       ├── calculator.component.css
│   │   │       └── calculator.component.spec.ts
│   │   ├── services/
│   │   │   ├── mutual-fund.service.ts
│   │   │   └── mutual-fund.service.spec.ts
│   │   ├── models/
│   │   │   └── investment.model.ts
│   │   ├── pipes/
│   │   │   ├── currency-format.pipe.ts
│   │   │   └── currency-format.pipe.spec.ts
│   │   ├── app.component.ts
│   │   ├── app.config.ts
│   │   └── app.routes.ts
│   ├── environments/
│   │   ├── environment.ts
│   │   └── environment.prod.ts
│   └── style.css
├── angular.json
├── package.json
└── tsconfig.json
```

## 🔌 API Endpoints

### Backend REST API

#### Get All Mutual Funds
```http
GET /api/mutual-funds
```

**Response:**
```json
[
  {
    "ticker": "VFIAX",
    "name": "Vanguard 500 Index Fund Admiral Shares"
  }
]
```

#### Calculate Investment Projection
```http
GET /api/investments/future-value?ticker=VFIAX&principal=10000&years=5
```

**Response:**
```json
{
  "ticker": "VFIAX",
  "principal": 10000,
  "years": 5,
  "riskFreeRate": 0.0425,
  "beta": 1.05,
  "expectedReturnRate": 0.10,
  "capmRate": 0.1176,
  "futureValue": 15896.50
}
```

## 🧪 Running Tests

### Backend Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MutualFundControllerTest

# Run with coverage
mvn test jacoco:report
```

### Frontend Tests
```bash
# Run all tests
npm test

# Run tests in headless mode
ng test --watch=false

# Generate coverage report
ng test --code-coverage
```

## 📊 Supported Mutual Funds

Currently, the application supports the following funds:

| Ticker | Name |
|--------|------|
| VFIAX | Vanguard 500 Index Fund Admiral Shares |
| FXAIX | Fidelity 500 Index Fund |
| SWPPX | Schwab S&P 500 Index Fund |
| VTSAX | Vanguard Total Stock Market Index Fund Admiral Shares |
| VIGAX | Vanguard Growth Index Fund Admiral Shares |

To add more funds, modify `MutualFundCatalogService.java`

## 🔧 Configuration

### Environment Variables

Create a `.env` file in the frontend directory (optional):
```
ANGULAR_APP_API_URL=http://localhost:8080
```

### Application Properties

Edit `src/main/resources/application.properties`:
```properties
spring.application.name=gs-tech-challenge
server.port=8080
```

## 🌐 External APIs Used

### Newton Analytics API
- **Beta Calculation**: `https://api.newtonanalytics.com/stock-beta/`
- **Historical Returns**: `https://api.newtonanalytics.com/modern-portfolio/`

### FRED (Federal Reserve Economic Data)
- **Risk-Free Rate**: Uses US Treasury 10-Year yield (DGS10)
- Currently hardcoded at 4.25%

## 📈 Bonus Features (Future Enhancements)

- [ ] Multiple fund comparison
- [ ] Portfolio allocation suggestions
- [ ] Historical performance charts
- [ ] Investment history tracking with database
- [ ] ETF support
- [ ] Advanced analytics and projections
- [ ] User authentication and accounts
- [ ] API rate limiting

## 🔒 Error Handling

The application includes comprehensive error handling:

- **400 Bad Request**: Invalid ticker or parameters
- **500 Internal Server Error**: External API failures
- **Client-side validation**: Form validation with user-friendly error messages

## 📝 Development Guidelines

### Code Style
- **Backend**: Follow Spring Boot conventions
- **Frontend**: Follow Angular style guide
- **Naming**: Use clear, descriptive names for variables and functions

### Testing
- Aim for >80% code coverage
- Write unit tests for all services and components
- Use mocking for external dependencies

### Git Workflow
```bash
# Create feature branch
git checkout -b feature/feature-name

# Commit changes
git commit -m "Add feature description"

# Push to remote
git push origin feature/feature-name
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Implement your changes
4. Write/update tests
5. Create a pull request

## 📄 Learning Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Angular Official Guide](https://angular.dev)
- [CAPM Explanation](https://www.investopedia.com/terms/c/capm.asp)
- [Mutual Funds Guide](https://www.investopedia.com/terms/m/mutualfund.asp)

## 📞 Support

For issues or questions:
1. Check existing documentation
2. Review error logs
3. Open an issue with detailed information

## 📄 License

This project is part of the GS Tech Challenge curriculum.

## 🎓 Learning Outcomes

Upon completion of this project, students will:

- ✅ Understand mutual fund investing and CAPM
- ✅ Build RESTful APIs with Spring Boot
- ✅ Create dynamic user interfaces with Angular
- ✅ Integrate frontend and backend applications
- ✅ Write comprehensive unit tests
- ✅ Handle external API integrations
- ✅ Implement error handling and validation
- ✅ Deploy and manage full-stack applications

---

**Last Updated**: February 26, 2026
