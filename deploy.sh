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

echo "🏗️  빌드 및 기동..."
$COMPOSE_CMD up --build -d

echo "⏳ MySQL 헬스체크 대기..."
set +e
for i in {1..60}; do
  $COMPOSE_CMD ps | grep -q "(healthy)" && break
  sleep 2
done
set -e

echo "📝 스키마 상태 확인 및 초기화(비어있으면 import)"
if ! $COMPOSE_CMD exec -T mysql sh -lc "mysql -uroot -p\"$DB_PASSWORD\" -N -e \"SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='${DB_NAME}';\"" | grep -qE '^[1-9]'; then
  echo "➡️  테이블이 없어 보입니다. schema.sql을 import 합니다."
  $COMPOSE_CMD exec -T mysql sh -lc "mysql -uroot -p\"$DB_PASSWORD\" ${DB_NAME} < /docker-entrypoint-initdb.d/schema.sql" || true
fi

echo "🌐 애플리케이션을 기동했습니다. (헬스체크 생략)"
echo "로그 일부를 출력합니다."
$COMPOSE_CMD logs --no-log-prefix app | tail -n 100 || true

echo "✅ 배포 완료"
echo "📱 http://localhost:8080"
