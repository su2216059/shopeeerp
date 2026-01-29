#!/usr/bin/env bash
set -euo pipefail

echo "Running password migration (BCrypt)..."
mvn -q -DskipTests -Dapp.password-migration.enabled=true -Dspring.main.web-application-type=none spring-boot:run
