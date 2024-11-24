FROM clojure:tools-deps-bookworm-slim AS builder
WORKDIR /opt
COPY . .
RUN apt-get update && apt-get install -y
RUN apt-get install -y curl
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash -
RUN apt-get install -y nodejs
RUN npm install
RUN npm run release
RUN clojure -Sdeps '{:mvn/local-repo "./.m2/repository"}' -T:build uber

FROM eclipse-temurin:21-alpine AS runtime
COPY --from=builder /opt/target/app.jar /app.jar
COPY --from=builder /opt/public /public
EXPOSE 8080
ENTRYPOINT ["java", "-cp", "app.jar", "clojure.main", "-m", "app.core"]

