-- Seed thêm Users (Nhân viên) và Import/Export Receipts
-- Phân bổ receipts cho nhiều nhân viên thay vì chỉ admin
-- Thêm nhiều đơn nhập/xuất hơn để có đủ dữ liệu

-- ===============================
-- SEED USERS (Nhân viên)
-- ===============================
-- Lưu ý: Password hash cho tất cả là "staff123" (đã encode bằng BCrypt)
-- Trong production, nên dùng password phức tạp hơn

INSERT INTO users (username, password_hash, full_name, email, phone, role, is_active, created_at) VALUES
('staff1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLN6', 'Nguyễn Văn Nam', 'staff1@minimart.com', '0911111111', 'Staff', 1, NOW()),
('staff2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLN6', 'Trần Thị Lan', 'staff2@minimart.com', '0922222222', 'Staff', 1, NOW()),
('staff3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLN6', 'Lê Văn Hùng', 'staff3@minimart.com', '0933333333', 'Staff', 1, NOW()),
('staff4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLN6', 'Phạm Thị Mai', 'staff4@minimart.com', '0944444444', 'Staff', 1, NOW()),
('staff5', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLN6', 'Hoàng Văn Diện', 'staff5@minimart.com', '0955555555', 'Staff', 1, NOW());

-- ===============================
-- SEED THÊM IMPORT RECEIPTS (20 phiếu nhập nữa)
-- Phân bổ cho nhiều nhân viên
-- ===============================

-- Import receipts với user_id khác nhau (2-6 là các staff mới tạo)
INSERT INTO import_receipt (user_id, supplier_id, import_date, note, created_at) VALUES
-- Staff 2 (Trần Thị Lan)
(2, 1, DATE_SUB(NOW(), INTERVAL 24 DAY), 'Nhập hàng - Staff Lan', DATE_SUB(NOW(), INTERVAL 24 DAY)),
(2, 2, DATE_SUB(NOW(), INTERVAL 20 DAY), 'Nhập đồ uống - Staff Lan', DATE_SUB(NOW(), INTERVAL 20 DAY)),
(2, 3, DATE_SUB(NOW(), INTERVAL 16 DAY), 'Nhập sữa - Staff Lan', DATE_SUB(NOW(), INTERVAL 16 DAY)),
(2, 1, DATE_SUB(NOW(), INTERVAL 12 DAY), 'Nhập thực phẩm - Staff Lan', DATE_SUB(NOW(), INTERVAL 12 DAY)),
(2, 2, DATE_SUB(NOW(), INTERVAL 8 DAY), 'Nhập nước ngọt - Staff Lan', DATE_SUB(NOW(), INTERVAL 8 DAY)),

-- Staff 3 (Lê Văn Hùng)
(3, 4, DATE_SUB(NOW(), INTERVAL 23 DAY), 'Nhập rau củ - Staff Hùng', DATE_SUB(NOW(), INTERVAL 23 DAY)),
(3, 5, DATE_SUB(NOW(), INTERVAL 19 DAY), 'Nhập thịt cá - Staff Hùng', DATE_SUB(NOW(), INTERVAL 19 DAY)),
(3, 6, DATE_SUB(NOW(), INTERVAL 15 DAY), 'Nhập đồ gia dụng - Staff Hùng', DATE_SUB(NOW(), INTERVAL 15 DAY)),
(3, 7, DATE_SUB(NOW(), INTERVAL 11 DAY), 'Nhập mỹ phẩm - Staff Hùng', DATE_SUB(NOW(), INTERVAL 11 DAY)),
(3, 8, DATE_SUB(NOW(), INTERVAL 7 DAY), 'Nhập văn phòng phẩm - Staff Hùng', DATE_SUB(NOW(), INTERVAL 7 DAY)),

-- Staff 4 (Phạm Thị Mai)
(4, 1, DATE_SUB(NOW(), INTERVAL 22 DAY), 'Nhập hàng - Staff Mai', DATE_SUB(NOW(), INTERVAL 22 DAY)),
(4, 2, DATE_SUB(NOW(), INTERVAL 18 DAY), 'Nhập đồ uống - Staff Mai', DATE_SUB(NOW(), INTERVAL 18 DAY)),
(4, 3, DATE_SUB(NOW(), INTERVAL 14 DAY), 'Nhập sữa - Staff Mai', DATE_SUB(NOW(), INTERVAL 14 DAY)),
(4, 4, DATE_SUB(NOW(), INTERVAL 10 DAY), 'Nhập rau củ - Staff Mai', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(4, 5, DATE_SUB(NOW(), INTERVAL 6 DAY), 'Nhập thịt cá - Staff Mai', DATE_SUB(NOW(), INTERVAL 6 DAY)),

-- Staff 5 (Hoàng Văn Diện)
(5, 6, DATE_SUB(NOW(), INTERVAL 21 DAY), 'Nhập đồ gia dụng - Staff Diện', DATE_SUB(NOW(), INTERVAL 21 DAY)),
(5, 7, DATE_SUB(NOW(), INTERVAL 17 DAY), 'Nhập mỹ phẩm - Staff Diện', DATE_SUB(NOW(), INTERVAL 17 DAY)),
(5, 8, DATE_SUB(NOW(), INTERVAL 13 DAY), 'Nhập văn phòng phẩm - Staff Diện', DATE_SUB(NOW(), INTERVAL 13 DAY)),
(5, 1, DATE_SUB(NOW(), INTERVAL 9 DAY), 'Nhập thực phẩm - Staff Diện', DATE_SUB(NOW(), INTERVAL 9 DAY)),
(5, 2, DATE_SUB(NOW(), INTERVAL 5 DAY), 'Nhập đồ uống - Staff Diện', DATE_SUB(NOW(), INTERVAL 5 DAY));

-- ===============================
-- SEED IMPORT RECEIPT ITEMS cho các import receipts mới (31-50)
-- ===============================

-- Import Receipt 31 (Staff 2 - Thực phẩm khô)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(31, 1, 80, 60, 4000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Hảo Hảo'),
(31, 4, 40, 30, 40000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Bánh quy Danisa'),
(31, 7, 60, 40, 20000, DATE_ADD(NOW(), INTERVAL 90 DAY), 'Bánh Oreo');

-- Import Receipt 32 (Staff 2 - Đồ uống)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(32, 11, 150, 120, 6000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Nước suối Lavie'),
(32, 13, 100, 80, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Coca Cola'),
(32, 17, 40, 20, 40000, DATE_ADD(NOW(), INTERVAL 25 DAY), 'Nước cam ép');

-- Import Receipt 33 (Staff 2 - Sữa)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(33, 21, 90, 70, 20000, DATE_ADD(NOW(), INTERVAL 8 DAY), 'Sữa tươi Vinamilk'),
(33, 23, 70, 50, 4500, DATE_ADD(NOW(), INTERVAL 8 DAY), 'Sữa chua Vinamilk');

-- Import Receipt 34 (Staff 2 - Thực phẩm)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(34, 2, 70, 55, 5000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Omachi'),
(34, 5, 35, 25, 35000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Bánh quy Richy');

-- Import Receipt 35 (Staff 2 - Nước ngọt)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(35, 14, 90, 70, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Pepsi'),
(35, 15, 70, 55, 8000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Trà xanh C2');

-- Import Receipt 36 (Staff 3 - Rau củ)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(36, 29, 45, 35, 20000, DATE_ADD(NOW(), INTERVAL 6 DAY), 'Cà chua'),
(36, 30, 90, 70, 4000, DATE_ADD(NOW(), INTERVAL 2 DAY), 'Rau muống'),
(36, 31, 40, 30, 18000, DATE_ADD(NOW(), INTERVAL 7 DAY), 'Cà rốt');

-- Import Receipt 37 (Staff 3 - Thịt cá)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(37, 35, 18, 12, 100000, DATE_ADD(NOW(), INTERVAL 1 DAY), 'Thịt heo ba chỉ'),
(37, 37, 12, 8, 200000, DATE_ADD(NOW(), INTERVAL 1 DAY), 'Thịt bò'),
(37, 38, 15, 10, 70000, DATE_ADD(NOW(), INTERVAL 2 DAY), 'Cá basa');

-- Import Receipt 38 (Staff 3 - Đồ gia dụng)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(38, 41, 55, 45, 30000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Nước rửa chén Sunlight'),
(38, 43, 28, 20, 70000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Bột giặt Omo'),
(38, 45, 35, 28, 35000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Nước lau sàn Sunlight');

-- Import Receipt 39 (Staff 3 - Mỹ phẩm)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(39, 47, 45, 35, 55000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Dầu gội Clear'),
(39, 49, 35, 28, 20000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Kem đánh răng P/S'),
(39, 51, 40, 32, 50000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Sữa tắm Lifebuoy');

-- Import Receipt 40 (Staff 3 - Văn phòng phẩm)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(40, 53, 90, 75, 55000, NULL, 'Giấy A4 Double A'),
(40, 55, 180, 150, 4000, NULL, 'Bút bi Thiên Long'),
(40, 56, 150, 120, 2500, NULL, 'Bút chì 2B');

-- Import Receipt 41-45 (Staff 4)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(41, 1, 95, 75, 4000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Hảo Hảo'),
(41, 4, 45, 35, 40000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Bánh quy Danisa'),
(42, 11, 140, 110, 6000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Nước suối Lavie'),
(42, 13, 95, 75, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Coca Cola'),
(43, 21, 85, 65, 20000, DATE_ADD(NOW(), INTERVAL 9 DAY), 'Sữa tươi Vinamilk'),
(43, 23, 65, 50, 4500, DATE_ADD(NOW(), INTERVAL 9 DAY), 'Sữa chua Vinamilk'),
(44, 29, 50, 40, 20000, DATE_ADD(NOW(), INTERVAL 5 DAY), 'Cà chua'),
(44, 30, 95, 75, 4000, DATE_ADD(NOW(), INTERVAL 2 DAY), 'Rau muống'),
(45, 35, 22, 17, 100000, DATE_ADD(NOW(), INTERVAL 1 DAY), 'Thịt heo ba chỉ'),
(45, 37, 14, 10, 200000, DATE_ADD(NOW(), INTERVAL 1 DAY), 'Thịt bò');

-- Import Receipt 46-50 (Staff 5)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(46, 41, 60, 50, 30000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Nước rửa chén Sunlight'),
(46, 43, 32, 25, 70000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Bột giặt Omo'),
(47, 47, 50, 40, 55000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Dầu gội Clear'),
(47, 49, 38, 30, 20000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Kem đánh răng P/S'),
(48, 53, 100, 85, 55000, NULL, 'Giấy A4 Double A'),
(48, 55, 200, 170, 4000, NULL, 'Bút bi Thiên Long'),
(49, 1, 110, 90, 4000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Hảo Hảo'),
(49, 4, 50, 40, 40000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Bánh quy Danisa'),
(50, 11, 160, 130, 6000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Nước suối Lavie'),
(50, 13, 110, 90, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Coca Cola');

-- ===============================
-- SEED THÊM EXPORT RECEIPTS (30 phiếu xuất nữa)
-- Phân bổ cho nhiều nhân viên
-- ===============================

-- Export receipts với user_id khác nhau
INSERT INTO export_receipt (user_id, customer_id, export_date, note, created_at) VALUES
-- Staff 2 (Trần Thị Lan) - 10 phiếu
(2, 1, DATE_SUB(NOW(), INTERVAL 24 DAY), 'Xuất hàng - Staff Lan', DATE_SUB(NOW(), INTERVAL 24 DAY)),
(2, 2, DATE_SUB(NOW(), INTERVAL 20 DAY), 'Xuất hàng - Staff Lan', DATE_SUB(NOW(), INTERVAL 20 DAY)),
(2, 3, DATE_SUB(NOW(), INTERVAL 16 DAY), 'Xuất hàng - Staff Lan', DATE_SUB(NOW(), INTERVAL 16 DAY)),
(2, 4, DATE_SUB(NOW(), INTERVAL 12 DAY), 'Xuất hàng - Staff Lan', DATE_SUB(NOW(), INTERVAL 12 DAY)),
(2, 5, DATE_SUB(NOW(), INTERVAL 8 DAY), 'Xuất hàng - Staff Lan', DATE_SUB(NOW(), INTERVAL 8 DAY)),
(2, 6, DATE_SUB(NOW(), INTERVAL 4 DAY), 'Xuất hàng - Staff Lan', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 7, NOW(), 'Xuất hàng - Staff Lan', NOW()),
(2, 8, NOW(), 'Xuất hàng - Staff Lan', NOW()),
(2, 9, NOW(), 'Xuất hàng - Staff Lan', NOW()),
(2, 10, NOW(), 'Xuất hàng - Staff Lan', NOW()),

-- Staff 3 (Lê Văn Hùng) - 10 phiếu
(3, 11, DATE_SUB(NOW(), INTERVAL 23 DAY), 'Xuất hàng - Staff Hùng', DATE_SUB(NOW(), INTERVAL 23 DAY)),
(3, 12, DATE_SUB(NOW(), INTERVAL 19 DAY), 'Xuất hàng - Staff Hùng', DATE_SUB(NOW(), INTERVAL 19 DAY)),
(3, 13, DATE_SUB(NOW(), INTERVAL 15 DAY), 'Xuất hàng - Staff Hùng', DATE_SUB(NOW(), INTERVAL 15 DAY)),
(3, 14, DATE_SUB(NOW(), INTERVAL 11 DAY), 'Xuất hàng - Staff Hùng', DATE_SUB(NOW(), INTERVAL 11 DAY)),
(3, 15, DATE_SUB(NOW(), INTERVAL 7 DAY), 'Xuất hàng - Staff Hùng', DATE_SUB(NOW(), INTERVAL 7 DAY)),
(3, 1, DATE_SUB(NOW(), INTERVAL 3 DAY), 'Xuất hàng - Staff Hùng', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 2, NOW(), 'Xuất hàng - Staff Hùng', NOW()),
(3, 3, NOW(), 'Xuất hàng - Staff Hùng', NOW()),
(3, 4, NOW(), 'Xuất hàng - Staff Hùng', NOW()),
(3, 5, NOW(), 'Xuất hàng - Staff Hùng', NOW()),

-- Staff 4 (Phạm Thị Mai) - 5 phiếu
(4, 6, DATE_SUB(NOW(), INTERVAL 22 DAY), 'Xuất hàng - Staff Mai', DATE_SUB(NOW(), INTERVAL 22 DAY)),
(4, 7, DATE_SUB(NOW(), INTERVAL 18 DAY), 'Xuất hàng - Staff Mai', DATE_SUB(NOW(), INTERVAL 18 DAY)),
(4, 8, DATE_SUB(NOW(), INTERVAL 14 DAY), 'Xuất hàng - Staff Mai', DATE_SUB(NOW(), INTERVAL 14 DAY)),
(4, 9, DATE_SUB(NOW(), INTERVAL 10 DAY), 'Xuất hàng - Staff Mai', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(4, 10, DATE_SUB(NOW(), INTERVAL 6 DAY), 'Xuất hàng - Staff Mai', DATE_SUB(NOW(), INTERVAL 6 DAY)),

-- Staff 5 (Hoàng Văn Diện) - 5 phiếu
(5, 11, DATE_SUB(NOW(), INTERVAL 21 DAY), 'Xuất hàng - Staff Diện', DATE_SUB(NOW(), INTERVAL 21 DAY)),
(5, 12, DATE_SUB(NOW(), INTERVAL 17 DAY), 'Xuất hàng - Staff Diện', DATE_SUB(NOW(), INTERVAL 17 DAY)),
(5, 13, DATE_SUB(NOW(), INTERVAL 13 DAY), 'Xuất hàng - Staff Diện', DATE_SUB(NOW(), INTERVAL 13 DAY)),
(5, 14, DATE_SUB(NOW(), INTERVAL 9 DAY), 'Xuất hàng - Staff Diện', DATE_SUB(NOW(), INTERVAL 9 DAY)),
(5, 15, DATE_SUB(NOW(), INTERVAL 5 DAY), 'Xuất hàng - Staff Diện', DATE_SUB(NOW(), INTERVAL 5 DAY));

-- ===============================
-- SEED EXPORT RECEIPT ITEMS cho các export receipts mới (41-70)
-- ===============================

-- Export Receipt 41-50 (Staff 2)
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(41, 61, 1, 12, 5000, 'Xuất mì tôm - Staff Lan'),
(41, 62, 4, 6, 45000, 'Xuất bánh quy - Staff Lan'),
(42, 63, 11, 20, 8000, 'Xuất nước suối - Staff Lan'),
(42, 64, 13, 15, 12000, 'Xuất Coca Cola - Staff Lan'),
(43, 65, 21, 8, 25000, 'Xuất sữa tươi - Staff Lan'),
(43, 66, 23, 6, 5000, 'Xuất sữa chua - Staff Lan'),
(44, 67, 2, 10, 6000, 'Xuất mì Omachi - Staff Lan'),
(44, 68, 5, 5, 40000, 'Xuất bánh Richy - Staff Lan'),
(45, 69, 14, 18, 12000, 'Xuất Pepsi - Staff Lan'),
(45, 70, 15, 14, 10000, 'Xuất trà xanh - Staff Lan'),
(46, 61, 1, 8, 5000, 'Xuất mì tôm - Staff Lan'),
(46, 62, 4, 4, 45000, 'Xuất bánh quy - Staff Lan'),
(47, 63, 11, 15, 8000, 'Xuất nước suối - Staff Lan'),
(47, 64, 13, 12, 12000, 'Xuất Coca Cola - Staff Lan'),
(48, 65, 21, 6, 25000, 'Xuất sữa tươi - Staff Lan'),
(48, 66, 23, 5, 5000, 'Xuất sữa chua - Staff Lan'),
(49, 67, 2, 8, 6000, 'Xuất mì Omachi - Staff Lan'),
(49, 68, 5, 4, 40000, 'Xuất bánh Richy - Staff Lan'),
(50, 69, 14, 15, 12000, 'Xuất Pepsi - Staff Lan'),
(50, 70, 15, 12, 10000, 'Xuất trà xanh - Staff Lan');

-- Export Receipt 51-60 (Staff 3)
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(51, 71, 29, 6, 25000, 'Xuất cà chua - Staff Hùng'),
(51, 72, 30, 10, 5000, 'Xuất rau muống - Staff Hùng'),
(52, 73, 31, 5, 20000, 'Xuất cà rốt - Staff Hùng'),
(52, 74, 35, 3, 120000, 'Xuất thịt heo - Staff Hùng'),
(53, 75, 37, 2, 250000, 'Xuất thịt bò - Staff Hùng'),
(53, 76, 38, 3, 80000, 'Xuất cá basa - Staff Hùng'),
(54, 77, 41, 5, 35000, 'Xuất nước rửa chén - Staff Hùng'),
(54, 78, 43, 3, 85000, 'Xuất bột giặt - Staff Hùng'),
(55, 79, 45, 4, 40000, 'Xuất nước lau sàn - Staff Hùng'),
(55, 80, 47, 4, 65000, 'Xuất dầu gội - Staff Hùng'),
(56, 81, 49, 3, 25000, 'Xuất kem đánh răng - Staff Hùng'),
(56, 82, 51, 4, 55000, 'Xuất sữa tắm - Staff Hùng'),
(57, 83, 53, 8, 65000, 'Xuất giấy A4 - Staff Hùng'),
(57, 84, 55, 15, 5000, 'Xuất bút bi - Staff Hùng'),
(58, 85, 56, 12, 3000, 'Xuất bút chì - Staff Hùng'),
(58, 71, 29, 5, 25000, 'Xuất cà chua - Staff Hùng'),
(59, 72, 30, 8, 5000, 'Xuất rau muống - Staff Hùng'),
(59, 74, 35, 2, 120000, 'Xuất thịt heo - Staff Hùng'),
(60, 77, 41, 4, 35000, 'Xuất nước rửa chén - Staff Hùng'),
(60, 80, 47, 3, 65000, 'Xuất dầu gội - Staff Hùng');

-- Export Receipt 61-65 (Staff 4)
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(61, 86, 1, 10, 5000, 'Xuất mì tôm - Staff Mai'),
(61, 87, 4, 5, 45000, 'Xuất bánh quy - Staff Mai'),
(62, 88, 11, 18, 8000, 'Xuất nước suối - Staff Mai'),
(62, 89, 13, 14, 12000, 'Xuất Coca Cola - Staff Mai'),
(63, 90, 21, 7, 25000, 'Xuất sữa tươi - Staff Mai'),
(63, 91, 23, 5, 5000, 'Xuất sữa chua - Staff Mai'),
(64, 92, 29, 5, 25000, 'Xuất cà chua - Staff Mai'),
(64, 93, 30, 8, 5000, 'Xuất rau muống - Staff Mai'),
(65, 94, 35, 2, 120000, 'Xuất thịt heo - Staff Mai'),
(65, 95, 37, 1, 250000, 'Xuất thịt bò - Staff Mai');

-- Export Receipt 66-70 (Staff 5)
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(66, 96, 41, 5, 35000, 'Xuất nước rửa chén - Staff Diện'),
(66, 97, 43, 3, 85000, 'Xuất bột giặt - Staff Diện'),
(67, 98, 47, 4, 65000, 'Xuất dầu gội - Staff Diện'),
(67, 99, 49, 3, 25000, 'Xuất kem đánh răng - Staff Diện'),
(68, 100, 53, 7, 65000, 'Xuất giấy A4 - Staff Diện'),
(68, 101, 55, 14, 5000, 'Xuất bút bi - Staff Diện'),
(69, 102, 1, 9, 5000, 'Xuất mì tôm - Staff Diện'),
(69, 103, 4, 4, 45000, 'Xuất bánh quy - Staff Diện'),
(70, 104, 11, 16, 8000, 'Xuất nước suối - Staff Diện'),
(70, 105, 13, 12, 12000, 'Xuất Coca Cola - Staff Diện');



