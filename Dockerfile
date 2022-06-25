FROM openjdk:17
EXPOSE 8080
ADD build/libs/moex-stock-api.jar moex-stock-api.jar
ENTRYPOINT ["java","-jar","/moex-stock-api.jar"]