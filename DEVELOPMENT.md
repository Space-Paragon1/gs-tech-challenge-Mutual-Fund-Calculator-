# Development Guidelines

## Code Standards

### Java/Spring Boot Backend
- Use Java 17+ features
- Follow Spring Boot conventions
- Use dependency injection via constructor
- Implement proper exception handling
- Write meaningful unit tests
- Use consistent naming conventions

### TypeScript/Angular Frontend
- Use TypeScript strict mode
- Implement proper type definitions
- Use reactive forms where appropriate
- Follow Angular style guide
- Write descriptive component/service names
- Use proper error handling in async operations

## Git Workflow

### Branch Naming
- `feature/description` - New features
- `bugfix/description` - Bug fixes
- `docs/description` - Documentation updates
- `test/description` - Test additions

### Commit Messages
```
Type: Brief description

Detailed explanation if needed.

Type can be: feat, fix, docs, style, refactor, test, chore
```

### Pull Request Process
1. Create feature branch from main
2. Make changes with frequent commits
3. Write/update tests
4. Update documentation
5. Create PR with description
6. Wait for review
7. Merge after approval

## Testing Guidelines

### Target Coverage
- Backend: >80%
- Frontend: >75%

### Writing Tests
- Use descriptive test names
- Test both happy and sad paths
- Mock external dependencies
- Keep tests focused and isolated
- Use fixtures/factories for test data

### Running Tests
```bash
# Backend
mvn test                          # Run all tests
mvn test -Dtest=ClassName        # Run specific class
mvn test jacoco:report          # Generate coverage

# Frontend
ng test                          # Run with watch
ng test --watch=false           # Run once
ng test --code-coverage         # Generate coverage
```

## Performance Considerations

### Backend
- Cache mutual fund list
- Use appropriate HTTP methods
- Validate input early
- Handle timeouts gracefully

### Frontend
- Use OnPush change detection
- Lazy load routes
- Unsubscribe from observables
- Optimize bundle size

## Security

### Backend
- Validate all inputs
- Use HTTPS in production
- Implement CORS properly
- Handle exceptions securely

### Frontend
- Sanitize user input
- Use HttpClient (built-in security)
- Implement proper error boundaries
- Don't expose sensitive data in logs

## Documentation

### Code Comments
- Comment complex logic
- Explain why, not what
- Keep comments updated
- Use JSDoc/JavaDoc for public APIs

### README
- Keep installation instructions current
- Document all endpoints
- Explain configuration options
- Add troubleshooting section

## Debugging

### Backend
```bash
# Enable debug logging
export MAVEN_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
mvn spring-boot:run

# Use IDE debugger on port 5005
```

### Frontend
```bash
# Chrome DevTools
ng serve  # Then open browser DevTools (F12)

# VS Code Debugger
# Configure .vscode/launch.json for debugging
```

## Deployment

### Production Build
```bash
# Backend
mvn clean package -DskipTests

# Frontend
ng build --configuration production
```

### Environment Configuration
- Use environment files for different configurations
- Don't commit sensitive credentials
- Use environment variables in deployment

## Useful Links

- [Spring Boot Best Practices](https://docs.spring.io/)
- [Angular Best Practices](https://angular.io/guide/styleguide)
- [RESTful API Design](https://restfulapi.net/)
- [Testing Best Practices](https://testing-library.com/docs/)
