# download Docker

# Build Docker Image:
# docker build -t globuli94/blackjack:v3 .
# docker run -it --rm globuli94/blackjack:v3

# Run Docker Container Locally:
# download XQuartz
# run following 2 commands in terminal - not in quartz terminal
# ip=$(ifconfig en0 | grep inet | awk '$1=="inet" {print $2}')
# xhost + $ip
# docker run -e DISPLAY=$ip:0 -v /tmp/.X11-unix:/tmp/.X11-unix -ti globuli94/blackjack:v3

# Push to Docker Hub:
# docker login
# docker push globuli94/blackjack:v2


FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0

# Install required dependencies for X11
RUN apt-get update && \
    apt-get install -y libxrender1 libxtst6 libxi6

# Set the working directory
WORKDIR /blackjack

# Add the project files into the container
ADD . /blackjack

CMD sbt run