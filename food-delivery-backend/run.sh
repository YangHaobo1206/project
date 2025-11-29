#!/usr/bin/env bash
set -euo pipefail

if [ ! -f build/food-delivery-backend.jar ];
then
  ./build.sh
fi

java -cp build/food-delivery-backend.jar com.food.delivery.FoodDeliveryApplication
