# Version: 20251223 Apple Silicon

#!/bin/bash

set -e
echo "반띵 백엔드 테스트 환경 구축 시작"

# Homebrew
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Java
echo "Java 패키지 다운로드 시작"

brew install wget
brew install openjdk@17
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
java --version

echo "Java 패키지 다운로드 성공"

echo "Gradle 패키지 다운로드 시작"

wget https://services.gradle.org/distributions/gradle-8.11.1-bin.zip -P /tmp
wget --max-redirect=10 https://services.gradle.org/distributions/gradle-8.11.1-bin.zip -P /tmp
curl -L -o /tmp/gradle-8.11.1-bin.zip https://services.gradle.org/distributions/gradle-8.11.1-bin.zip
sudo unzip -d /opt/gradle /tmp/gradle-8.11.1-bin.zip
export PATH=$PATH:/opt/gradle/gradle-8.11.1/bin

echo "Gradle 패키지 다운로드 성공"

echo "Spring 빌드 시작"

chmod +x gradlew
./gradlew clean build -x test

echoi "빌드 성공"

java -jar build/libs/ban-thing-0.0.1-SNAPSHOT.jar

echo "Spring 빌드 성공"