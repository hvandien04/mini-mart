-- Seed Import và Export Receipts với nhiều dữ liệu
-- Tạo dữ liệu mẫu cho báo cáo, biểu đồ doanh thu, và thống kê
-- Dữ liệu trải đều trong tháng để biểu đồ có nhiều điểm dữ liệu

-- ===============================
-- SEED IMPORT RECEIPTS (30 phiếu nhập trong tháng)
-- ===============================

-- Import receipts đầu tháng (1-10)
INSERT INTO import_receipt (user_id, supplier_id, import_date, note, created_at) VALUES
(1, 1, DATE_SUB(NOW(), INTERVAL 25 DAY), 'Nhập hàng đầu tháng', DATE_SUB(NOW(), INTERVAL 25 DAY)),
(1, 2, DATE_SUB(NOW(), INTERVAL 24 DAY), 'Nhập đồ uống', DATE_SUB(NOW(), INTERVAL 24 DAY)),
(1, 3, DATE_SUB(NOW(), INTERVAL 23 DAY), 'Nhập sữa', DATE_SUB(NOW(), INTERVAL 23 DAY)),
(1, 1, DATE_SUB(NOW(), INTERVAL 22 DAY), 'Nhập thực phẩm khô', DATE_SUB(NOW(), INTERVAL 22 DAY)),
(1, 2, DATE_SUB(NOW(), INTERVAL 21 DAY), 'Nhập nước ngọt', DATE_SUB(NOW(), INTERVAL 21 DAY)),
(1, 4, DATE_SUB(NOW(), INTERVAL 20 DAY), 'Nhập rau củ', DATE_SUB(NOW(), INTERVAL 20 DAY)),
(1, 5, DATE_SUB(NOW(), INTERVAL 19 DAY), 'Nhập thịt cá', DATE_SUB(NOW(), INTERVAL 19 DAY)),
(1, 6, DATE_SUB(NOW(), INTERVAL 18 DAY), 'Nhập đồ gia dụng', DATE_SUB(NOW(), INTERVAL 18 DAY)),
(1, 7, DATE_SUB(NOW(), INTERVAL 17 DAY), 'Nhập mỹ phẩm', DATE_SUB(NOW(), INTERVAL 17 DAY)),
(1, 8, DATE_SUB(NOW(), INTERVAL 16 DAY), 'Nhập văn phòng phẩm', DATE_SUB(NOW(), INTERVAL 16 DAY));

-- Import receipts giữa tháng (11-20)
INSERT INTO import_receipt (user_id, supplier_id, import_date, note, created_at) VALUES
(1, 1, DATE_SUB(NOW(), INTERVAL 15 DAY), 'Nhập hàng giữa tháng', DATE_SUB(NOW(), INTERVAL 15 DAY)),
(1, 2, DATE_SUB(NOW(), INTERVAL 14 DAY), 'Nhập đồ uống', DATE_SUB(NOW(), INTERVAL 14 DAY)),
(1, 3, DATE_SUB(NOW(), INTERVAL 13 DAY), 'Nhập sữa', DATE_SUB(NOW(), INTERVAL 13 DAY)),
(1, 1, DATE_SUB(NOW(), INTERVAL 12 DAY), 'Nhập thực phẩm khô', DATE_SUB(NOW(), INTERVAL 12 DAY)),
(1, 2, DATE_SUB(NOW(), INTERVAL 11 DAY), 'Nhập nước ngọt', DATE_SUB(NOW(), INTERVAL 11 DAY)),
(1, 4, DATE_SUB(NOW(), INTERVAL 10 DAY), 'Nhập rau củ', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(1, 5, DATE_SUB(NOW(), INTERVAL 9 DAY), 'Nhập thịt cá', DATE_SUB(NOW(), INTERVAL 9 DAY)),
(1, 6, DATE_SUB(NOW(), INTERVAL 8 DAY), 'Nhập đồ gia dụng', DATE_SUB(NOW(), INTERVAL 8 DAY)),
(1, 7, DATE_SUB(NOW(), INTERVAL 7 DAY), 'Nhập mỹ phẩm', DATE_SUB(NOW(), INTERVAL 7 DAY)),
(1, 8, DATE_SUB(NOW(), INTERVAL 6 DAY), 'Nhập văn phòng phẩm', DATE_SUB(NOW(), INTERVAL 6 DAY));

-- Import receipts cuối tháng (21-30)
INSERT INTO import_receipt (user_id, supplier_id, import_date, note, created_at) VALUES
(1, 1, DATE_SUB(NOW(), INTERVAL 5 DAY), 'Nhập hàng cuối tháng', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, 2, DATE_SUB(NOW(), INTERVAL 4 DAY), 'Nhập đồ uống', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(1, 3, DATE_SUB(NOW(), INTERVAL 3 DAY), 'Nhập sữa', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), 'Nhập thực phẩm khô', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 2, DATE_SUB(NOW(), INTERVAL 1 DAY), 'Nhập nước ngọt', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 4, NOW(), 'Nhập rau củ hôm nay', NOW()),
(1, 5, NOW(), 'Nhập thịt cá hôm nay', NOW()),
(1, 6, NOW(), 'Nhập đồ gia dụng hôm nay', NOW()),
(1, 7, NOW(), 'Nhập mỹ phẩm hôm nay', NOW()),
(1, 8, NOW(), 'Nhập văn phòng phẩm hôm nay', NOW());

-- ===============================
-- SEED IMPORT RECEIPT ITEMS
-- Lưu ý: V6 đã tạo import_receipts với IDs 1-20, nên V7 sẽ tạo với IDs 21-50
-- ===============================

-- Import Receipt 21 (Thực phẩm khô - 25 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(21, 1, 100, 50, 4000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Hảo Hảo'),
(21, 4, 50, 30, 40000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Bánh quy Danisa'),
(21, 7, 80, 0, 20000, DATE_ADD(NOW(), INTERVAL 90 DAY), 'Bánh Oreo - đã hết');

-- Import Receipt 22 (Đồ uống - 24 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(22, 11, 200, 150, 6000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Nước suối Lavie'),
(22, 13, 150, 100, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Coca Cola'),
(22, 17, 50, 10, 40000, DATE_ADD(NOW(), INTERVAL 30 DAY), 'Nước cam ép - sắp hết hạn');

-- Import Receipt 23 (Sữa - 23 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(23, 21, 100, 80, 20000, DATE_ADD(NOW(), INTERVAL 7 DAY), 'Sữa tươi Vinamilk - sắp hết hạn'),
(23, 25, 50, 0, 30000, DATE_ADD(NOW(), INTERVAL -5 DAY), 'Phô mai con bò cười - đã hết hạn');

-- Import Receipt 24 (Thực phẩm khô - 22 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(24, 2, 80, 60, 5000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Omachi'),
(24, 5, 40, 25, 35000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Bánh quy Richy');

-- Import Receipt 25 (Đồ uống - 21 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(25, 14, 100, 70, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Pepsi'),
(25, 15, 80, 50, 8000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Trà xanh C2');

-- Import Receipt 26 (Rau củ - 20 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(26, 29, 50, 30, 20000, DATE_ADD(NOW(), INTERVAL 7 DAY), 'Cà chua - sắp hết hạn'),
(26, 30, 100, 80, 4000, DATE_ADD(NOW(), INTERVAL 3 DAY), 'Rau muống - sắp hết hạn');

-- Import Receipt 27 (Thịt cá - 19 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(27, 35, 20, 10, 100000, DATE_ADD(NOW(), INTERVAL 2 DAY), 'Thịt heo ba chỉ - sắp hết hạn'),
(27, 37, 15, 5, 200000, DATE_ADD(NOW(), INTERVAL 1 DAY), 'Thịt bò - sắp hết hạn');

-- Import Receipt 28 (Đồ gia dụng - 18 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(28, 41, 60, 40, 30000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Nước rửa chén Sunlight'),
(28, 43, 30, 20, 70000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Bột giặt Omo');

-- Import Receipt 29 (Mỹ phẩm - 17 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(29, 47, 50, 35, 55000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Dầu gội Clear'),
(29, 49, 40, 30, 20000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Kem đánh răng P/S');

-- Import Receipt 30 (Văn phòng phẩm - 16 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(30, 53, 100, 80, 55000, NULL, 'Giấy A4 Double A'),
(30, 55, 200, 150, 4000, NULL, 'Bút bi Thiên Long');

-- Import Receipt 31-40 (giữa tháng)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(31, 1, 120, 100, 4000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Hảo Hảo'),
(31, 4, 60, 50, 40000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Bánh quy Danisa'),
(32, 11, 150, 120, 6000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Nước suối Lavie'),
(32, 13, 100, 80, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Coca Cola'),
(33, 21, 80, 60, 20000, DATE_ADD(NOW(), INTERVAL 10 DAY), 'Sữa tươi Vinamilk'),
(33, 23, 60, 50, 4500, DATE_ADD(NOW(), INTERVAL 10 DAY), 'Sữa chua Vinamilk'),
(34, 2, 100, 80, 5000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Omachi'),
(34, 7, 90, 70, 20000, DATE_ADD(NOW(), INTERVAL 90 DAY), 'Bánh Oreo'),
(35, 14, 120, 100, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Pepsi'),
(35, 15, 100, 80, 8000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Trà xanh C2'),
(36, 29, 60, 50, 20000, DATE_ADD(NOW(), INTERVAL 5 DAY), 'Cà chua'),
(36, 30, 120, 100, 4000, DATE_ADD(NOW(), INTERVAL 2 DAY), 'Rau muống'),
(37, 35, 25, 20, 100000, DATE_ADD(NOW(), INTERVAL 1 DAY), 'Thịt heo ba chỉ'),
(37, 37, 20, 15, 200000, DATE_ADD(NOW(), INTERVAL 1 DAY), 'Thịt bò'),
(38, 41, 70, 60, 30000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Nước rửa chén Sunlight'),
(38, 45, 40, 30, 35000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Nước lau sàn Sunlight'),
(39, 47, 60, 50, 55000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Dầu gội Clear'),
(39, 51, 50, 40, 50000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Sữa tắm Lifebuoy'),
(40, 53, 120, 100, 55000, NULL, 'Giấy A4 Double A'),
(40, 55, 250, 200, 4000, NULL, 'Bút bi Thiên Long');

-- Import Receipt 41-50 (cuối tháng)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(41, 1, 150, 130, 4000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Hảo Hảo'),
(41, 4, 70, 60, 40000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Bánh quy Danisa'),
(42, 11, 180, 150, 6000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Nước suối Lavie'),
(42, 13, 130, 110, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Coca Cola'),
(43, 21, 100, 90, 20000, DATE_ADD(NOW(), INTERVAL 15 DAY), 'Sữa tươi Vinamilk'),
(43, 23, 80, 70, 4500, DATE_ADD(NOW(), INTERVAL 15 DAY), 'Sữa chua Vinamilk'),
(44, 2, 120, 100, 5000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Omachi'),
(44, 7, 100, 90, 20000, DATE_ADD(NOW(), INTERVAL 90 DAY), 'Bánh Oreo'),
(45, 14, 140, 120, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Pepsi'),
(45, 15, 120, 100, 8000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Trà xanh C2'),
(46, 29, 70, 60, 20000, DATE_ADD(NOW(), INTERVAL 7 DAY), 'Cà chua'),
(46, 30, 140, 120, 4000, DATE_ADD(NOW(), INTERVAL 3 DAY), 'Rau muống'),
(47, 35, 30, 25, 100000, DATE_ADD(NOW(), INTERVAL 2 DAY), 'Thịt heo ba chỉ'),
(47, 37, 25, 20, 200000, DATE_ADD(NOW(), INTERVAL 1 DAY), 'Thịt bò'),
(48, 41, 80, 70, 30000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Nước rửa chén Sunlight'),
(48, 45, 50, 40, 35000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Nước lau sàn Sunlight'),
(49, 47, 70, 60, 55000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Dầu gội Clear'),
(49, 51, 60, 50, 50000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Sữa tắm Lifebuoy'),
(50, 53, 150, 130, 55000, NULL, 'Giấy A4 Double A'),
(50, 55, 300, 250, 4000, NULL, 'Bút bi Thiên Long');

-- ===============================
-- SEED EXPORT RECEIPTS (40 phiếu xuất trong tháng)
-- ===============================

-- Export receipts đầu tháng (1-15)
INSERT INTO export_receipt (user_id, customer_id, export_date, note, created_at) VALUES
(1, 1, DATE_SUB(NOW(), INTERVAL 25 DAY), 'Xuất cho khách hàng thân thiết', DATE_SUB(NOW(), INTERVAL 25 DAY)),
(1, 2, DATE_SUB(NOW(), INTERVAL 24 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 24 DAY)),
(1, 3, DATE_SUB(NOW(), INTERVAL 23 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 23 DAY)),
(1, 4, DATE_SUB(NOW(), INTERVAL 22 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 22 DAY)),
(1, 5, DATE_SUB(NOW(), INTERVAL 21 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 21 DAY)),
(1, 6, DATE_SUB(NOW(), INTERVAL 20 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 20 DAY)),
(1, 7, DATE_SUB(NOW(), INTERVAL 19 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 19 DAY)),
(1, 8, DATE_SUB(NOW(), INTERVAL 18 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 18 DAY)),
(1, 9, DATE_SUB(NOW(), INTERVAL 17 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 17 DAY)),
(1, 10, DATE_SUB(NOW(), INTERVAL 16 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 16 DAY)),
(1, 11, DATE_SUB(NOW(), INTERVAL 15 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 15 DAY)),
(1, 12, DATE_SUB(NOW(), INTERVAL 14 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 14 DAY)),
(1, 13, DATE_SUB(NOW(), INTERVAL 13 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 13 DAY)),
(1, 14, DATE_SUB(NOW(), INTERVAL 12 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 12 DAY)),
(1, 15, DATE_SUB(NOW(), INTERVAL 11 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 11 DAY));

-- Export receipts giữa tháng (16-30)
INSERT INTO export_receipt (user_id, customer_id, export_date, note, created_at) VALUES
(1, 1, DATE_SUB(NOW(), INTERVAL 10 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(1, 2, DATE_SUB(NOW(), INTERVAL 9 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 9 DAY)),
(1, 3, DATE_SUB(NOW(), INTERVAL 8 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 8 DAY)),
(1, 4, DATE_SUB(NOW(), INTERVAL 7 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 7 DAY)),
(1, 5, DATE_SUB(NOW(), INTERVAL 6 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 6 DAY)),
(1, 6, DATE_SUB(NOW(), INTERVAL 5 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, 7, DATE_SUB(NOW(), INTERVAL 4 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(1, 8, DATE_SUB(NOW(), INTERVAL 3 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, 9, DATE_SUB(NOW(), INTERVAL 2 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 10, DATE_SUB(NOW(), INTERVAL 1 DAY), 'Xuất hàng', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 11, NOW(), 'Xuất hàng hôm nay', NOW()),
(1, 12, NOW(), 'Xuất hàng hôm nay', NOW()),
(1, 13, NOW(), 'Xuất hàng hôm nay', NOW()),
(1, 14, NOW(), 'Xuất hàng hôm nay', NOW()),
(1, 15, NOW(), 'Xuất hàng hôm nay', NOW());

-- Export receipts cuối tháng (31-40) - thêm nhiều hơn để có nhiều dữ liệu
INSERT INTO export_receipt (user_id, customer_id, export_date, note, created_at) VALUES
(1, 1, DATE_SUB(NOW(), INTERVAL 25 DAY), 'Xuất hàng lần 2', DATE_SUB(NOW(), INTERVAL 25 DAY)),
(1, 2, DATE_SUB(NOW(), INTERVAL 20 DAY), 'Xuất hàng lần 2', DATE_SUB(NOW(), INTERVAL 20 DAY)),
(1, 3, DATE_SUB(NOW(), INTERVAL 15 DAY), 'Xuất hàng lần 2', DATE_SUB(NOW(), INTERVAL 15 DAY)),
(1, 4, DATE_SUB(NOW(), INTERVAL 10 DAY), 'Xuất hàng lần 2', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(1, 5, DATE_SUB(NOW(), INTERVAL 5 DAY), 'Xuất hàng lần 2', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, 6, NOW(), 'Xuất hàng lần 2 hôm nay', NOW()),
(1, 7, NOW(), 'Xuất hàng lần 2 hôm nay', NOW()),
(1, 8, NOW(), 'Xuất hàng lần 2 hôm nay', NOW()),
(1, 9, NOW(), 'Xuất hàng lần 2 hôm nay', NOW()),
(1, 10, NOW(), 'Xuất hàng lần 2 hôm nay', NOW());

-- ===============================
-- SEED EXPORT RECEIPT ITEMS
-- Lưu ý: V6 đã tạo export_receipts với IDs 1-30, nên V7 sẽ tạo với IDs 31-70
-- import_receipt_item_id có thể tham chiếu cả V6 (1-47) và V7 (48+)
-- ===============================

-- Export Receipt 31 (25 ngày trước) - Xuất từ Import Receipt Item từ V6 hoặc V7
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(31, 1, 1, 20, 5000, 'Xuất mì tôm'),
(31, 2, 4, 10, 45000, 'Xuất bánh quy');

-- Export Receipt 32 (24 ngày trước)
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(32, 3, 11, 30, 8000, 'Xuất nước suối'),
(32, 4, 13, 20, 12000, 'Xuất Coca Cola');

-- Export Receipt 33 (23 ngày trước)
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(33, 5, 21, 10, 25000, 'Xuất sữa tươi'),
(33, 6, 25, 5, 35000, 'Xuất phô mai');

-- Export Receipt 34-45
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(34, 7, 2, 15, 6000, 'Xuất mì Omachi'),
(34, 8, 5, 8, 40000, 'Xuất bánh Richy'),
(35, 9, 14, 25, 12000, 'Xuất Pepsi'),
(35, 10, 15, 20, 10000, 'Xuất trà xanh'),
(36, 11, 29, 10, 25000, 'Xuất cà chua'),
(36, 12, 30, 15, 5000, 'Xuất rau muống'),
(37, 13, 35, 5, 120000, 'Xuất thịt heo'),
(37, 14, 37, 3, 250000, 'Xuất thịt bò'),
(38, 15, 41, 10, 35000, 'Xuất nước rửa chén'),
(38, 16, 43, 5, 85000, 'Xuất bột giặt'),
(39, 17, 47, 8, 65000, 'Xuất dầu gội'),
(39, 18, 49, 6, 25000, 'Xuất kem đánh răng'),
(40, 19, 53, 15, 65000, 'Xuất giấy A4'),
(40, 20, 55, 30, 5000, 'Xuất bút bi'),
(41, 21, 1, 10, 5000, 'Xuất mì tôm'),
(41, 22, 4, 5, 45000, 'Xuất bánh quy'),
(42, 23, 11, 20, 8000, 'Xuất nước suối'),
(42, 24, 13, 15, 12000, 'Xuất Coca Cola'),
(43, 25, 21, 8, 25000, 'Xuất sữa tươi'),
(43, 26, 23, 5, 5000, 'Xuất sữa chua'),
(44, 27, 2, 12, 6000, 'Xuất mì Omachi'),
(44, 28, 7, 10, 25000, 'Xuất bánh Oreo'),
(45, 29, 14, 18, 12000, 'Xuất Pepsi'),
(45, 30, 15, 15, 10000, 'Xuất trà xanh');

-- Export Receipt 46-60 (giữa tháng)
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(46, 31, 29, 8, 25000, 'Xuất cà chua'),
(46, 32, 30, 12, 5000, 'Xuất rau muống'),
(47, 33, 35, 4, 120000, 'Xuất thịt heo'),
(47, 34, 37, 2, 250000, 'Xuất thịt bò'),
(48, 35, 41, 8, 35000, 'Xuất nước rửa chén'),
(48, 36, 45, 5, 40000, 'Xuất nước lau sàn'),
(49, 37, 47, 6, 65000, 'Xuất dầu gội'),
(49, 38, 51, 5, 55000, 'Xuất sữa tắm'),
(50, 39, 53, 12, 65000, 'Xuất giấy A4'),
(50, 40, 55, 25, 5000, 'Xuất bút bi'),
(51, 41, 1, 8, 5000, 'Xuất mì tôm'),
(51, 42, 4, 4, 45000, 'Xuất bánh quy'),
(52, 43, 11, 18, 8000, 'Xuất nước suối'),
(52, 44, 13, 12, 12000, 'Xuất Coca Cola'),
(53, 45, 21, 6, 25000, 'Xuất sữa tươi'),
(53, 46, 23, 4, 5000, 'Xuất sữa chua'),
(54, 47, 2, 10, 6000, 'Xuất mì Omachi'),
(54, 48, 7, 8, 25000, 'Xuất bánh Oreo'),
(55, 49, 14, 15, 12000, 'Xuất Pepsi'),
(55, 50, 15, 12, 10000, 'Xuất trà xanh'),
(56, 51, 29, 6, 25000, 'Xuất cà chua'),
(56, 52, 30, 10, 5000, 'Xuất rau muống'),
(57, 53, 35, 3, 120000, 'Xuất thịt heo'),
(57, 54, 37, 2, 250000, 'Xuất thịt bò'),
(58, 55, 41, 6, 35000, 'Xuất nước rửa chén'),
(58, 56, 45, 4, 40000, 'Xuất nước lau sàn'),
(59, 57, 47, 5, 65000, 'Xuất dầu gội'),
(59, 58, 51, 4, 55000, 'Xuất sữa tắm'),
(60, 59, 53, 10, 65000, 'Xuất giấy A4'),
(60, 60, 55, 20, 5000, 'Xuất bút bi');

-- Export Receipt 61-70 (cuối tháng - lần 2)
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(61, 1, 1, 15, 5000, 'Xuất mì tôm lần 2'),
(61, 2, 4, 8, 45000, 'Xuất bánh quy lần 2'),
(62, 3, 11, 25, 8000, 'Xuất nước suối lần 2'),
(62, 4, 13, 18, 12000, 'Xuất Coca Cola lần 2'),
(63, 5, 21, 7, 25000, 'Xuất sữa tươi lần 2'),
(63, 6, 25, 4, 35000, 'Xuất phô mai lần 2'),
(64, 7, 2, 12, 6000, 'Xuất mì Omachi lần 2'),
(64, 8, 5, 6, 40000, 'Xuất bánh Richy lần 2'),
(65, 9, 14, 20, 12000, 'Xuất Pepsi lần 2'),
(65, 10, 15, 16, 10000, 'Xuất trà xanh lần 2'),
(66, 11, 29, 5, 25000, 'Xuất cà chua lần 2'),
(66, 12, 30, 8, 5000, 'Xuất rau muống lần 2'),
(67, 13, 35, 2, 120000, 'Xuất thịt heo lần 2'),
(67, 14, 37, 1, 250000, 'Xuất thịt bò lần 2'),
(68, 15, 41, 5, 35000, 'Xuất nước rửa chén lần 2'),
(68, 16, 43, 3, 85000, 'Xuất bột giặt lần 2'),
(69, 17, 47, 4, 65000, 'Xuất dầu gội lần 2'),
(69, 18, 49, 3, 25000, 'Xuất kem đánh răng lần 2'),
(70, 19, 53, 8, 65000, 'Xuất giấy A4 lần 2'),
(70, 20, 55, 15, 5000, 'Xuất bút bi lần 2');




