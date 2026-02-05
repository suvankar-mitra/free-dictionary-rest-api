# Use a minimal official OpenJDK image
FROM azul/zulu-openjdk:17

# Set timezone to IST
ENV TZ=Asia/Kolkata
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Create a non-root user
RUN useradd -ms /bin/bash springuser

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/app.jar app.jar

# Change ownership of the app files
RUN chown -R springuser:springuser /app

# Switch to the non-root user
USER springuser

# Expose app's port
EXPOSE 8800

# Run the app with some sane default JVM memory settings
ENTRYPOINT ["java", "-Xms256m", "-Xmx1G", "-jar", "app.jar"]