# Use a known-good OpenJDK base image
FROM eclipse-temurin:21-jdk

# Optional: set up display (for GUI forwarding on macOS via XQuartz)
ENV DISPLAY=host.docker.internal:0

# Install dependencies for GUI + Maven build
RUN apt-get update && \
    apt-get install -y maven wget unzip libgtk-3-0 libgbm1 libx11-6 \
    libxext6 libxrender1 libxtst6 libxxf86vm1 libgl1 libasound2t64 && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Download JavaFX SDK 21 (detect architecture: aarch64 for Apple Silicon, x64 for Intel)
RUN ARCH=$(dpkg --print-architecture) && \
    if [ "$ARCH" = "arm64" ] || [ "$ARCH" = "aarch64" ]; then \
      JAVAFX_URL="https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-aarch64_bin-sdk.zip"; \
    else \
      JAVAFX_URL="https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-x64_bin-sdk.zip"; \
    fi && \
    wget "$JAVAFX_URL" -O /tmp/openjfx.zip && \
    unzip /tmp/openjfx.zip -d /opt && \
    rm /tmp/openjfx.zip

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the shaded JAR
RUN mvn clean package -DskipTests

# List target folder to check JAR
RUN ls -l target


# Run the **shaded JAR** with JavaFX modules
CMD ["java", "--module-path", "/opt/javafx-sdk-21/lib", "--add-modules", "javafx.controls,javafx.fxml", "-jar", "target/sum-product_fx-1.0-SNAPSHOT.jar"]
