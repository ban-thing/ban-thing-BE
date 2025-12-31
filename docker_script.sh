# Version: 20251223 Apple Silicon

#!/bin/bash
set -e

echo "반띵 백엔드 테스트 환경 구축 시작"

## Docker 패키지 다운로드
echo "Docker 패키지 다운로드 시작"

softwareupdate --install-rosetta --agree-to-license || true

sudo hdiutil attach ~/Downloads/Docker.dmg
sudo /Volumes/Docker/Docker.app/Contents/MacOS/install
sudo hdiutil detach /Volumes/Docker

docker --version
echo "Docker 패키지 다운로드 성공"

echo "Docker 환경 구축 시작"

## Docker 실행
open -a Docker

echo "화면 상단에 Docker Desktop이 실행 중이면 1, 실패면 0을 입력하세요"

read -r answer

while [ "$answer" != "1" ] && [ "$answer" != "0" ]; do
  echo "잘못된 입력입니다."
  read -r answer
done

if [ "$answer" = "0" ]; then
  echo "담당자에게 문의주세요."
  exit 1
fi

echo "Docker 준비 완료"

## MySQL 설정
docker pull mysql:8.3

docker network create docker-network || true

docker rm -f mysql-container || true

docker run -d \
  --name mysql-container \
  --network docker-network \
  -e MYSQL_ROOT_PASSWORD=1234 \
  -e LC_ALL=C.UTF-8 \
  mysql:8.3

echo "MySQL 기동 대기..."
until docker exec mysql-container mysqladmin ping -uroot -p1234 --silent; do
  sleep 2
done

docker exec mysql-container \
  mysql -uroot -p1234 \
  -e "CREATE DATABASE IF NOT EXISTS banthing CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

echo "Docker 환경 구축 성공"