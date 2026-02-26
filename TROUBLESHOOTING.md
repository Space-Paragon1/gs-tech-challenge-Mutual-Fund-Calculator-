# Troubleshooting Guide

## Common Issues and Solutions

### Backend Issues

#### Port 8080 Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or change port in application.properties
# server.port=8081
```

#### CORS Errors
- Error: "Access to XMLHttpRequest blocked by CORS policy"
- Solution: Ensure CorsConfig.java is properly configured
- Check allowed origins match your frontend URL

#### Maven Build Fails
```bash
# Clean and rebuild
mvn clean install

# Check Java version
java -version  # Should be 17+

# Check Maven version
mvn -version   # Should be 3.8+
```

#### External API Connection Issues
- **Newton Analytics API unreachable**
  - Verify internet connection
  - Check API status
  - The API may have rate limiting

- **Invalid ticker response**
  - Ticker must be supported by Newton Analytics
  - Some tickers may not have available data

### Frontend Issues

#### npm install Fails
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and package-lock.json
rm -rf node_modules package-lock.json

# Reinstall
npm install
```

#### Port 4200 Already in Use
```bash
# Use different port
ng serve --port 4201

# Or kill the process using port 4200
lsof -i :4200
kill -9 <PID>
```

#### Module Not Found Errors
```bash
# Ensure all dependencies are installed
npm install

# Check that @angular/cli is globally installed
npm install -g @angular/cli
```

#### CORS Errors on API Calls
- Check that backend is running on port 8080
- Verify proxy.conf.json is correctly configured
- Ensure environment.ts has correct API URL

#### Tests Won't Run
```bash
# Install Chrome (required for Karma tests)
# Or use:
ng test --browsers=ChromeHeadless

# Update Karma configuration
ng test --browsers=Chrome
```

## Application Doesn't Start

### Step-by-Step Debugging

1. **Verify Prerequisites**
   ```bash
   java -version      # Should be 17+
   mvn -version       # Should be 3.8+
   node --version     # Should be 18+
   npm --version      # Should be 9+
   ```

2. **Backend Startup**
   ```bash
   cd /path/to/gs-tech-challenge
   mvn clean package
   mvn spring-boot:run
   ```

3. **Frontend Startup**
   ```bash
   cd frontend
   npm install
   npm start
   ```

4. **Check Logs**
   - Backend logs appear in terminal
   - Frontend logs appear in browser console (DevTools F12)

### Network Connectivity

#### API Calls Failing
1. Check backend is running: `curl http://localhost:8080/api/mutual-funds`
2. Verify environment.ts has correct API URL
3. Check browser network tab for actual request/response
4. Verify CORS configuration in backend

#### External API Issues
```bash
# Test Newton Analytics API
curl "https://api.newtonanalytics.com/stock-beta/?ticker=VFIAX&index=^GSPC&interval=1mo&observations=12"
```

## Performance Issues

### Backend Performance
- Monitor API response times
- Check for external API timeouts
- Verify no infinite loops
- Use logging to track execution

### Frontend Performance
- Check bundle size: `ng build --stats-json`
- Use Angular DevTools
- Monitor network requests in DevTools
- Check for memory leaks

## Browser Compatibility

- **Chrome**: Fully supported
- **Firefox**: Fully supported
- **Safari**: May require polyfills
- **Edge**: Fully supported

See [Angular browser support](https://angular.io/guide/browser-support)

## Logs and Debugging

### Backend Logs
```properties
# In application.properties
logging.level.root=INFO
logging.level.com.gs.techchallenge=DEBUG
```

### Frontend Logs
```typescript
// Using console
console.log('Debug message', variable);
console.error('Error:', error);

// Using Angular DevTools browser extension
```

## Getting Help

1. **Check Documentation**
   - Read README.md
   - Review DEVELOPMENT.md
   - Check code comments

2. **Search Online**
   - Search stack overflow
   - Check official documentation
   - Look for similar issues

3. **Debug Systematically**
   - Isolate the problem
   - Check logs and error messages
   - Test in isolation
   - Use debugger tools

4. **Ask for Help**
   - Provide error messages
   - Describe what you tried
   - Share relevant code
   - Mention your environment

## FAQ

**Q: How do I add a new mutual fund?**
A: Edit MutualFundCatalogService.java and add to the FUNDS list.

**Q: Can I change the risk-free rate?**
A: Yes, edit RiskFreeRateService.java

**Q: How do I use a different port?**
A: Backend - edit application.properties
   Frontend - use `ng serve --port 4201`

**Q: Are tests required?**
A: Yes, maintain >80% coverage for backend, >75% for frontend.

**Q: How do I deploy to production?**
A: Build with `mvn clean package` for backend and `ng build` for frontend, then deploy artifacts.
