
# Stage 1: Build the Maven project
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Maven configuration and source code
COPY pom.xml .
COPY src ./src

# Build WAR file
RUN mvn -B clean package -DskipTests


# Stage 2: Run application on Tomcat 10.1
FROM tomcat:10.1-jdk17-temurin-jammy

# Remove default Tomcat homepage
RUN rm -rf /usr/local/tomcat/webapps/ROOT \
           /usr/local/tomcat/webapps/ROOT.war

# Copy generated WAR as ROOT application
COPY --from=build /app/target/CollegeCanteen.war \
    /usr/local/tomcat/webapps/ROOT.war

# Render web service port
EXPOSE 10000

# Start Tomcat using Render's PORT environment variable
CMD ["sh", "-c", "sed -i \"s/port=\\\"8080\\\"/port=\\\"${PORT:-10000}\\\"/\" \"$CATALINA_HOME/conf/server.xml\" && exec catalina.sh run"]

