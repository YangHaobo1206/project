#!/usr/bin/env bash
set -euo pipefail

mkdir -p build/classes
find build/classes -maxdepth 1 -type f -delete

javac -d build/classes $(find src/main/java -name "*.java")
jar --create --file build/food-delivery-backend.jar -C build/classes .

echo "Built build/food-delivery-backend.jar"
