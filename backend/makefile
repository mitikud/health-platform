# --- Variables ---
DOCKER_COMPOSE=docker compose
MVN=mvn -q

# Default
all: build up

# Build all services & shared modules
build:
	$(MVN) clean package -DskipTests -f ./pom.xml

# Start whole stack (including infra)
up:
	$(DOCKER_COMPOSE) up -d --build

# Stop stack
down:
	$(DOCKER_COMPOSE) down

# View logs of all services
logs:
	$(DOCKER_COMPOSE) logs -f

# Rebuild + restart one service
restart:
	@if [ -z "$(svc)" ]; then echo "Usage: make restart svc=service-name"; exit 1; fi
	$(DOCKER_COMPOSE) build $(svc) && $(DOCKER_COMPOSE) up -d $(svc)

# Run unit tests
test:
	$(MVN) test -f ./pom.xml

# Clean compiled artifacts
clean:
	$(MVN) clean -f ./pom.xml

#make build       # build all services
# make up          # spin up Compose stack
# make restart svc=auth-service
 #make logs
 #make down