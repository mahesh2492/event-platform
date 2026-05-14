FROM hseeberger/scala-sbt:17.0.2_1.6.2_2.13.8

WORKDIR /app

COPY . .

RUN sbt compile