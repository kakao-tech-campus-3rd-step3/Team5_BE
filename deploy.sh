#!/bin/bash

set -euo pipefail

# ---------------------------------
# Deploy with docker compose (app + mysql)
# - Stops host MySQL (systemd) to free port 3306
# - Brings up services via docker compose
# ---------------------------------

echo "===== Deploy DailyQ (compose) ====="

if [ ! -f .env ]; then
    echo "❌ .env 파일이 없습니다. 프로젝트 루트에 .env 파일을 생성해주세요." >&2
    exit 1
fi

# export .env variables for this script (also used by docker compose)
set -a
source ./.env
set +a

if ! command -v docker &> /dev/null; then
    echo "❌ Docker가 설치되어 있지 않습니다." >&2
    exit 1
fi

# docker compose command
if command -v docker-compose &> /dev/null; then
  COMPOSE_CMD="docker-compose"
else
  COMPOSE_CMD="docker compose"
fi

echo "🛑 호스트 MySQL(systemd) 중지 시도..."
if command -v systemctl &> /dev/null; then
  sudo systemctl stop mysql || true
  # 포트가 비었는지 확인
  sleep 1
fi

echo "🧹 기존 컨테이너/네트워크 정리..."
$COMPOSE_CMD down --remove-orphans || true

echo "🗑️  Docker 리소스 정리..."
# 디스크 공간 확보를 위한 강화된 정리
echo "📊 정리 전 디스크 사용량:"
df -h / || true
docker system df || true

# 사용하지 않는 볼륨 정리
docker volume prune -f || true

echo "📊 정리 후 디스크 사용량:"
df -h / || true

echo "🏗️  Dockerfile로 이미지 빌드..."
if ! docker build -t dailyq-app:latest ./dailyq; then
    echo "❌ Docker 이미지 빌드 실패"
    exit 1
fi

echo "🚀 컨테이너 기동..."
if ! $COMPOSE_CMD up -d; then
    echo "❌ Docker Compose 빌드/기동 실패"
    echo "📋 컨테이너 상태 확인:"
    $COMPOSE_CMD ps -a || true
    echo "📋 최근 로그:"
    $COMPOSE_CMD logs --tail=50 || true
    exit 1
fi

echo "⏳ MySQL 헬스체크 대기..."
set +e
MYSQL_HEALTHY=false
for i in {1..60}; do
  if $COMPOSE_CMD ps | grep -q "(healthy)"; then
    MYSQL_HEALTHY=true
    break
  fi
  echo "⏳ MySQL 헬스체크 대기 중... ($i/60)"
  sleep 2
done
set -e

if [ "$MYSQL_HEALTHY" = false ]; then
    echo "❌ MySQL 헬스체크 실패 (2분 타임아웃)"
    echo "📋 컨테이너 상태:"
    $COMPOSE_CMD ps -a
    echo "📋 MySQL 로그:"
    $COMPOSE_CMD logs mysql --tail=50
    exit 1
fi
echo "✅ MySQL 헬스체크 성공"

echo "📝 스키마 및 Mock 데이터 강제 재적용"
echo "➡️  최신 스키마를 적용합니다."
$COMPOSE_CMD exec -T mysql sh -lc "mysql -uroot -p\"$DB_PASSWORD\" ${DB_NAME} < /docker-entrypoint-initdb.d/01_schema.sql" || true
echo "➡️  Mock 데이터를 적용합니다."
$COMPOSE_CMD exec -T mysql sh -lc "mysql -uroot -p\"$DB_PASSWORD\" ${DB_NAME} < /docker-entrypoint-initdb.d/02_mock.sql" || true

echo "🔍 애플리케이션 헬스체크..."
APP_HEALTHY=false
for i in {1..90}; do
  if $COMPOSE_CMD ps app | grep -q "(healthy)"; then
    APP_HEALTHY=true
    break
  elif curl -f http://localhost:80/actuator/health &>/dev/null; then
    APP_HEALTHY=true
    break
  fi
  echo "🔍 애플리케이션 헬스체크 대기 중... ($i/90)"
  sleep 3
done

if [ "$APP_HEALTHY" = false ]; then
    echo "❌ 애플리케이션 헬스체크 실패 (4.5분 타임아웃)"
    echo "📋 컨테이너 상태:"
    $COMPOSE_CMD ps -a
    echo "📋 애플리케이션 로그:"
    $COMPOSE_CMD logs app --tail=100
    exit 1
fi

echo "✅ 애플리케이션 헬스체크 성공"
echo "📋 최종 컨테이너 상태:"
$COMPOSE_CMD ps

echo "📊 디스크 사용량:"
df -h / | tail -1

echo "🗑️  배포 후 정리..."
# 더 이상 사용하지 않는 이미지 정리
docker image prune -f || true

echo "✅ 배포 완료"
echo "📱 https://be.dailyq.my"
echo "📱 https://be.dailyq.my/swagger-ui.html"
echo "📱 Local: http://localhost:8080"
