INSERT INTO users(id, username, password, role, nickname, phone, address, create_time) VALUES
 (1,'admin','0192023a7bbd73250516f069df18b500','ADMIN','Administrator','000','HQ', CURRENT_TIMESTAMP()),
 (2,'alice','6ad14ba9986e3615423dfca256d04e3f','USER','Alice','123','Street 1', CURRENT_TIMESTAMP()),
 (3,'bob','merchant123','MERCHANT','Bob Merchant','321','Shop road', CURRENT_TIMESTAMP());

UPDATE users SET password='a52f2c0dbf38ade4f715e02c7124046e' WHERE id=3;

INSERT INTO merchant(id, user_id, name, description, address, status) VALUES
 (1,3,'Bob\'s Burger','Great burgers','Food street','APPROVED');

INSERT INTO dish(id, merchant_id, name, description, price, category, status) VALUES
 (1,1,'Cheese Burger','Tasty cheese burger',25.50,'Burger','ON'),
 (2,1,'Fries','French fries',10.00,'Snack','ON');
