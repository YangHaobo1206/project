package com.food.delivery;

import com.food.delivery.server.HttpJsonServer;

public class FoodDeliveryApplication {
    public static void main(String[] args) {
        int port = 8080;
        new HttpJsonServer(port).start();
    }
}
