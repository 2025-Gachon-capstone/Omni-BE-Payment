# builder image
FROM amazoncorretto:17-al2-jdk AS builder

RUN mkdir /Omni-BE-Payment
WORKDIR /Omni-BE-Payment

COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean bootJar

# runtime image
FROM amazoncorretto:17.0.12-al2

ENV TZ=Asia/Seoul
ENV PROFILE=${PROFILE}

RUN mkdir /Omni-BE-Payment
WORKDIR /Omni-BE-Payment

COPY --from=builder /Omni-BE-Payment/build/libs/Omni-BE-Payment-* /Omni-BE-Payment/app.jar

CMD ["sh", "-c", " \
    java -Dspring.profiles.active=${PROFILE} \
         -jar /Omni-BE-Payment/app.jar"]
