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
-- SEED IMPORT RECEIPT ITEMS cho các import receipts mới (1-20)
-- ===============================

-- Import Receipt 1 (Staff 2 - Thực phẩm khô)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(1, 1, 80, 60, 4000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Hảo Hảo'),
(1, 4, 40, 30, 40000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Bánh quy Danisa'),
(1, 7, 60, 40, 20000, DATE_ADD(NOW(), INTERVAL 90 DAY), 'Bánh Oreo');

-- Import Receipt 2 (Staff 2 - Đồ uống)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(2, 11, 150, 120, 6000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Nước suối Lavie'),
(2, 13, 100, 80, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Coca Cola'),
(2, 17, 40, 20, 40000, DATE_ADD(NOW(), INTERVAL 25 DAY), 'Nước cam ép');

-- Import Receipt 3 (Staff 2 - Sữa)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(3, 21, 90, 70, 20000, DATE_ADD(NOW(), INTERVAL 8 DAY), 'Sữa tươi Vinamilk'),
(3, 23, 70, 50, 4500, DATE_ADD(NOW(), INTERVAL 8 DAY), 'Sữa chua Vinamilk');

-- Import Receipt 4 (Staff 2 - Thực phẩm)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(4, 2, 70, 55, 5000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Omachi'),
(4, 5, 35, 25, 35000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Bánh quy Richy');

-- Import Receipt 5 (Staff 2 - Nước ngọt)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(5, 14, 90, 70, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Pepsi'),
(5, 15, 70, 55, 8000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Trà xanh C2');

-- Import Receipt 6 (Staff 3 - Rau củ)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(6, 29, 45, 35, 20000, DATE_ADD(NOW(), INTERVAL 6 DAY), 'Cà chua'),
(6, 30, 90, 70, 4000, DATE_ADD(NOW(), INTERVAL 2 DAY), 'Rau muống'),
(6, 31, 40, 30, 18000, DATE_ADD(NOW(), INTERVAL 7 DAY), 'Cà rốt');

-- Import Receipt 7 (Staff 3 - Thịt cá)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(7, 35, 18, 12, 100000, DATE_ADD(NOW(), INTERVAL 1 DAY), 'Thịt heo ba chỉ'),
(7, 37, 12, 8, 200000, DATE_ADD(NOW(), INTERVAL 1 DAY), 'Thịt bò'),
(7, 38, 15, 10, 70000, DATE_ADD(NOW(), INTERVAL 2 DAY), 'Cá basa');

-- Import Receipt 8 (Staff 3 - Đồ gia dụng)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(8, 41, 55, 45, 30000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Nước rửa chén Sunlight'),
(8, 43, 28, 20, 70000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Bột giặt Omo'),
(8, 45, 35, 28, 35000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Nước lau sàn Sunlight');

-- Import Receipt 9 (Staff 3 - Mỹ phẩm)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(9, 47, 45, 35, 55000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Dầu gội Clear'),
(9, 49, 35, 28, 20000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Kem đánh răng P/S'),
(9, 51, 40, 32, 50000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Sữa tắm Lifebuoy');

-- Import Receipt 10 (Staff 3 - Văn phòng phẩm)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(10, 53, 90, 75, 55000, NULL, 'Giấy A4 Double A'),
(10, 55, 180, 150, 4000, NULL, 'Bút bi Thiên Long'),
(10, 56, 150, 120, 2500, NULL, 'Bút chì 2B');

-- Import Receipt 11-15 (Staff 4)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(11, 1, 95, 75, 4000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Hảo Hảo'),
(11, 4, 45, 35, 40000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Bánh quy Danisa'),
(12, 11, 140, 110, 6000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Nước suối Lavie'),
(12, 13, 95, 75, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Coca Cola'),
(13, 21, 85, 65, 20000, DATE_ADD(NOW(), INTERVAL 9 DAY), 'Sữa tươi Vinamilk'),
(13, 23, 65, 50, 4500, DATE_ADD(NOW(), INTERVAL 9 DAY), 'Sữa chua Vinamilk'),
(14, 29, 50, 40, 20000, DATE_ADD(NOW(), INTERVAL 5 DAY), 'Cà chua'),
(14, 30, 95, 75, 4000, DATE_ADD(NOW(), INTERVAL 2 DAY), 'Rau muống'),
(15, 35, 22, 17, 100000, DATE_ADD(NOW(), INTERVAL 1 DAY), 'Thịt heo ba chỉ'),
(15, 37, 14, 10, 200000, DATE_ADD(NOW(), INTERVAL 1 DAY), 'Thịt bò');

-- Import Receipt 16-20 (Staff 5)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(16, 41, 60, 50, 30000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Nước rửa chén Sunlight'),
(16, 43, 32, 25, 70000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Bột giặt Omo'),
(17, 47, 50, 40, 55000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Dầu gội Clear'),
(17, 49, 38, 30, 20000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Kem đánh răng P/S'),
(18, 53, 100, 85, 55000, NULL, 'Giấy A4 Double A'),
(18, 55, 200, 170, 4000, NULL, 'Bút bi Thiên Long'),
(19, 1, 110, 90, 4000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Hảo Hảo'),
(19, 4, 50, 40, 40000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Bánh quy Danisa'),
(20, 11, 160, 130, 6000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Nước suối Lavie'),
(20, 13, 110, 90, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Coca Cola');

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
-- SEED EXPORT RECEIPT ITEMS cho các export receipts mới (1-30)
-- Lưu ý: 
-- - export_receipt_id: V6 tạo 30 export_receipts với IDs 1-30
-- - import_receipt_item_id: được map dựa trên thứ tự tạo trong V6 (IDs 1-47)
-- ===============================

-- Export Receipt 1-10 (Staff 2)
-- Mapping: Receipt 1 items (1,2,3), Receipt 2 items (4,5,6), Receipt 3 items (7,8), Receipt 4 items (9,10), Receipt 5 items (11,12)
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(1, 1, 1, 12, 5000, 'Xuất mì tôm - Staff Lan'),
(1, 2, 4, 6, 45000, 'Xuất bánh quy - Staff Lan'),
(2, 4, 11, 20, 8000, 'Xuất nước suối - Staff Lan'),
(2, 5, 13, 15, 12000, 'Xuất Coca Cola - Staff Lan'),
(3, 7, 21, 8, 25000, 'Xuất sữa tươi - Staff Lan'),
(3, 8, 23, 6, 5000, 'Xuất sữa chua - Staff Lan'),
(4, 9, 2, 10, 6000, 'Xuất mì Omachi - Staff Lan'),
(4, 10, 5, 5, 40000, 'Xuất bánh Richy - Staff Lan'),
(5, 11, 14, 18, 12000, 'Xuất Pepsi - Staff Lan'),
(5, 12, 15, 14, 10000, 'Xuất trà xanh - Staff Lan'),
(6, 1, 1, 8, 5000, 'Xuất mì tôm - Staff Lan'),
(6, 2, 4, 4, 45000, 'Xuất bánh quy - Staff Lan'),
(7, 4, 11, 15, 8000, 'Xuất nước suối - Staff Lan'),
(7, 5, 13, 12, 12000, 'Xuất Coca Cola - Staff Lan'),
(8, 7, 21, 6, 25000, 'Xuất sữa tươi - Staff Lan'),
(8, 8, 23, 5, 5000, 'Xuất sữa chua - Staff Lan'),
(9, 9, 2, 8, 6000, 'Xuất mì Omachi - Staff Lan'),
(9, 10, 5, 4, 40000, 'Xuất bánh Richy - Staff Lan'),
(10, 11, 14, 15, 12000, 'Xuất Pepsi - Staff Lan'),
(10, 12, 15, 12, 10000, 'Xuất trà xanh - Staff Lan');

-- Export Receipt 11-20 (Staff 3)
-- Mapping: Receipt 6 items (13,14,15), Receipt 7 items (16,17,18), Receipt 8 items (19,20,21), Receipt 9 items (22,23,24), Receipt 10 items (25,26,27)
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(11, 13, 29, 6, 25000, 'Xuất cà chua - Staff Hùng'),
(11, 14, 30, 10, 5000, 'Xuất rau muống - Staff Hùng'),
(12, 15, 31, 5, 20000, 'Xuất cà rốt - Staff Hùng'),
(12, 16, 35, 3, 120000, 'Xuất thịt heo - Staff Hùng'),
(13, 17, 37, 2, 250000, 'Xuất thịt bò - Staff Hùng'),
(13, 18, 38, 3, 80000, 'Xuất cá basa - Staff Hùng'),
(14, 19, 41, 5, 35000, 'Xuất nước rửa chén - Staff Hùng'),
(14, 20, 43, 3, 85000, 'Xuất bột giặt - Staff Hùng'),
(15, 21, 45, 4, 40000, 'Xuất nước lau sàn - Staff Hùng'),
(15, 22, 47, 4, 65000, 'Xuất dầu gội - Staff Hùng'),
(16, 23, 49, 3, 25000, 'Xuất kem đánh răng - Staff Hùng'),
(16, 24, 51, 4, 55000, 'Xuất sữa tắm - Staff Hùng'),
(17, 25, 53, 8, 65000, 'Xuất giấy A4 - Staff Hùng'),
(17, 26, 55, 15, 5000, 'Xuất bút bi - Staff Hùng'),
(18, 27, 56, 12, 3000, 'Xuất bút chì - Staff Hùng'),
(18, 13, 29, 5, 25000, 'Xuất cà chua - Staff Hùng'),
(19, 14, 30, 8, 5000, 'Xuất rau muống - Staff Hùng'),
(19, 16, 35, 2, 120000, 'Xuất thịt heo - Staff Hùng'),
(20, 19, 41, 4, 35000, 'Xuất nước rửa chén - Staff Hùng'),
(20, 22, 47, 3, 65000, 'Xuất dầu gội - Staff Hùng');

-- Export Receipt 21-25 (Staff 4)
-- Mapping: Receipt 11 items (28,29), Receipt 12 items (30,31), Receipt 13 items (32,33), Receipt 14 items (34,35), Receipt 15 items (36,37)
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(21, 28, 1, 10, 5000, 'Xuất mì tôm - Staff Mai'),
(21, 29, 4, 5, 45000, 'Xuất bánh quy - Staff Mai'),
(22, 30, 11, 18, 8000, 'Xuất nước suối - Staff Mai'),
(22, 31, 13, 14, 12000, 'Xuất Coca Cola - Staff Mai'),
(23, 32, 21, 7, 25000, 'Xuất sữa tươi - Staff Mai'),
(23, 33, 23, 5, 5000, 'Xuất sữa chua - Staff Mai'),
(24, 34, 29, 5, 25000, 'Xuất cà chua - Staff Mai'),
(24, 35, 30, 8, 5000, 'Xuất rau muống - Staff Mai'),
(25, 36, 35, 2, 120000, 'Xuất thịt heo - Staff Mai'),
(25, 37, 37, 1, 250000, 'Xuất thịt bò - Staff Mai');

-- Export Receipt 26-30 (Staff 5)
-- Mapping: Receipt 16 items (38,39), Receipt 17 items (40,41), Receipt 18 items (42,43), Receipt 19 items (44,45), Receipt 20 items (46,47)
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(26, 38, 41, 5, 35000, 'Xuất nước rửa chén - Staff Diện'),
(26, 39, 43, 3, 85000, 'Xuất bột giặt - Staff Diện'),
(27, 40, 47, 4, 65000, 'Xuất dầu gội - Staff Diện'),
(27, 41, 49, 3, 25000, 'Xuất kem đánh răng - Staff Diện'),
(28, 42, 53, 7, 65000, 'Xuất giấy A4 - Staff Diện'),
(28, 43, 55, 14, 5000, 'Xuất bút bi - Staff Diện'),
(29, 44, 1, 9, 5000, 'Xuất mì tôm - Staff Diện'),
(29, 45, 4, 4, 45000, 'Xuất bánh quy - Staff Diện'),
(30, 46, 11, 16, 8000, 'Xuất nước suối - Staff Diện'),
(30, 47, 13, 12, 12000, 'Xuất Coca Cola - Staff Diện');



