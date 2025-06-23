# Healthcare-Data-Management
## Architecture Overview

Clients interact through the API Gateway which routes traffic to backend services like Access Management and Patient Service. 
Request tracing, authentication, and rate limiting are handled via filters and integrations.

## Key Features Implemented
## Requirement	Implementation
API structure and documentation	Swagger (`springdoc-openapi`) set up in `access-management` and `patient-service`.
Client access validation	Client ID header validated via a custom `GatewayFilter`.
Multi-tenancy support	Tenant identification via `X-Client-ID`, propagated and used downstream.
1-second SLA handling	Asynchronous routing with Spring Cloud Gateway (WebFlux), efficient threading.
Technologies Used
API Gateway: Spring Cloud Gateway, Spring WebFlux
Auth & Services: Spring Boot, Spring Security, JWT, PostgreSQL
Resilience: Resilience4j (Rate Limiter)
Discovery: Eureka
Tracing: Spring Cloud Sleuth + Zipkin
Docs: SpringDoc (OpenAPI 3)(Swagger)
Build Tool: Maven
Test Coverage

Unit tests added for authentication and patient API flows. 

### Assumptions
- Each client is uniquely identified using `X-Client-ID`.
- Authentication is via JWT (no OAuth or refresh token flow).
- Zipkin is deployed locally or embedded for tracing.
- PostgreSQL is used as the RDBMS; multitenancy handled at app level.
- Rate limits are set conservatively for demo but are configurable per client.
### Limitations
- No dedicated multi-tenant DB structure (e.g., schema per client).
- No distributed cache (e.g., Redis for sessions or token blacklisting).
- No fine-grained user entitlements beyond role-based access.
- Swagger is publicly accessible (should be protected in production).
- Not containerized (no Docker/K8s deployment).
- No CI/CD automation.
-request tracing not working as expected

### Future Improvements
Rate limiting or Resiliency: yet to be implemented
Security: Add refresh token flow, revoke access tokens, and validate JWT expiry & audience.
Scalability: Use Redis-backed gateway rate limiting and distributed cache for user/session data.
Observability: Integrate Prometheus + Grafana for metrics, circuit breaker dashboards.
Multitenancy: Use schema-per-tenant or partitioned data model with tenant isolation.
Load Testing: Run JMeter or Gatling test to validate 100 concurrent users within 1s SLA.
CI/CD: Setup GitHub Actions or Jenkins pipelines.
Containerization: Dockerize each service and use Kubernetes for orchestration.

