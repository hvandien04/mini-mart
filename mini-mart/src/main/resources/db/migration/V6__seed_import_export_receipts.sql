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
-- ===============================

-- Import Receipt 1 (Thực phẩm khô - 25 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(1, 1, 100, 50, 4000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Hảo Hảo'),
(1, 4, 50, 30, 40000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Bánh quy Danisa'),
(1, 7, 80, 0, 20000, DATE_ADD(NOW(), INTERVAL 90 DAY), 'Bánh Oreo - đã hết');

-- Import Receipt 2 (Đồ uống - 24 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(2, 11, 200, 150, 6000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Nước suối Lavie'),
(2, 13, 150, 100, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Coca Cola'),
(2, 17, 50, 10, 40000, DATE_ADD(NOW(), INTERVAL 30 DAY), 'Nước cam ép - sắp hết hạn');

-- Import Receipt 3 (Sữa - 23 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(3, 21, 100, 80, 20000, DATE_ADD(NOW(), INTERVAL 7 DAY), 'Sữa tươi Vinamilk - sắp hết hạn'),
(3, 25, 50, 0, 30000, DATE_ADD(NOW(), INTERVAL -5 DAY), 'Phô mai con bò cười - đã hết hạn');

-- Import Receipt 4 (Thực phẩm khô - 22 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(4, 2, 80, 60, 5000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Omachi'),
(4, 5, 40, 25, 35000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Bánh quy Richy');

-- Import Receipt 5 (Đồ uống - 21 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(5, 14, 100, 70, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Pepsi'),
(5, 15, 80, 50, 8000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Trà xanh C2');

-- Import Receipt 6 (Rau củ - 20 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(6, 29, 50, 30, 20000, DATE_ADD(NOW(), INTERVAL 7 DAY), 'Cà chua - sắp hết hạn'),
(6, 30, 100, 80, 4000, DATE_ADD(NOW(), INTERVAL 3 DAY), 'Rau muống - sắp hết hạn');

-- Import Receipt 7 (Thịt cá - 19 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(7, 35, 20, 10, 100000, DATE_ADD(NOW(), INTERVAL 2 DAY), 'Thịt heo ba chỉ - sắp hết hạn'),
(7, 37, 15, 5, 200000, DATE_ADD(NOW(), INTERVAL 1 DAY), 'Thịt bò - sắp hết hạn');

-- Import Receipt 8 (Đồ gia dụng - 18 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(8, 41, 60, 40, 30000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Nước rửa chén Sunlight'),
(8, 43, 30, 20, 70000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Bột giặt Omo');

-- Import Receipt 9 (Mỹ phẩm - 17 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(9, 47, 50, 35, 55000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Dầu gội Clear'),
(9, 49, 40, 30, 20000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Kem đánh răng P/S');

-- Import Receipt 10 (Văn phòng phẩm - 16 ngày trước)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(10, 53, 100, 80, 55000, NULL, 'Giấy A4 Double A'),
(10, 55, 200, 150, 4000, NULL, 'Bút bi Thiên Long');

-- Tiếp tục với các import receipts còn lại (11-30) với dữ liệu tương tự
-- Import Receipt 11-20 (giữa tháng)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(11, 1, 120, 100, 4000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Hảo Hảo'),
(11, 4, 60, 50, 40000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Bánh quy Danisa'),
(12, 11, 150, 120, 6000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Nước suối Lavie'),
(12, 13, 100, 80, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Coca Cola'),
(13, 21, 80, 60, 20000, DATE_ADD(NOW(), INTERVAL 10 DAY), 'Sữa tươi Vinamilk'),
(13, 23, 60, 50, 4500, DATE_ADD(NOW(), INTERVAL 10 DAY), 'Sữa chua Vinamilk'),
(14, 2, 100, 80, 5000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Omachi'),
(14, 7, 90, 70, 20000, DATE_ADD(NOW(), INTERVAL 90 DAY), 'Bánh Oreo'),
(15, 14, 120, 100, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Pepsi'),
(15, 15, 100, 80, 8000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Trà xanh C2'),
(16, 29, 60, 50, 20000, DATE_ADD(NOW(), INTERVAL 5 DAY), 'Cà chua'),
(16, 30, 120, 100, 4000, DATE_ADD(NOW(), INTERVAL 2 DAY), 'Rau muống'),
(17, 35, 25, 20, 100000, DATE_ADD(NOW(), INTERVAL 1 DAY), 'Thịt heo ba chỉ'),
(17, 37, 20, 15, 200000, DATE_ADD(NOW(), INTERVAL 1 DAY), 'Thịt bò'),
(18, 41, 70, 60, 30000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Nước rửa chén Sunlight'),
(18, 45, 40, 30, 35000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Nước lau sàn Sunlight'),
(19, 47, 60, 50, 55000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Dầu gội Clear'),
(19, 51, 50, 40, 50000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Sữa tắm Lifebuoy'),
(20, 53, 120, 100, 55000, NULL, 'Giấy A4 Double A'),
(20, 55, 250, 200, 4000, NULL, 'Bút bi Thiên Long');

-- Import Receipt 21-30 (cuối tháng)
INSERT INTO import_receipt_item (import_receipt_id, product_id, quantity, remaining_quantity, import_price, expire_date, note) VALUES
(21, 1, 150, 130, 4000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Hảo Hảo'),
(21, 4, 70, 60, 40000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Bánh quy Danisa'),
(22, 11, 180, 150, 6000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Nước suối Lavie'),
(22, 13, 130, 110, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Coca Cola'),
(23, 21, 100, 90, 20000, DATE_ADD(NOW(), INTERVAL 15 DAY), 'Sữa tươi Vinamilk'),
(23, 23, 80, 70, 4500, DATE_ADD(NOW(), INTERVAL 15 DAY), 'Sữa chua Vinamilk'),
(24, 2, 120, 100, 5000, DATE_ADD(NOW(), INTERVAL 180 DAY), 'Mì tôm Omachi'),
(24, 7, 100, 90, 20000, DATE_ADD(NOW(), INTERVAL 90 DAY), 'Bánh Oreo'),
(25, 14, 140, 120, 10000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Pepsi'),
(25, 15, 120, 100, 8000, DATE_ADD(NOW(), INTERVAL 365 DAY), 'Trà xanh C2'),
(26, 29, 70, 60, 20000, DATE_ADD(NOW(), INTERVAL 7 DAY), 'Cà chua'),
(26, 30, 140, 120, 4000, DATE_ADD(NOW(), INTERVAL 3 DAY), 'Rau muống'),
(27, 35, 30, 25, 100000, DATE_ADD(NOW(), INTERVAL 2 DAY), 'Thịt heo ba chỉ'),
(27, 37, 25, 20, 200000, DATE_ADD(NOW(), INTERVAL 1 DAY), 'Thịt bò'),
(28, 41, 80, 70, 30000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Nước rửa chén Sunlight'),
(28, 45, 50, 40, 35000, DATE_ADD(NOW(), INTERVAL 730 DAY), 'Nước lau sàn Sunlight'),
(29, 47, 70, 60, 55000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Dầu gội Clear'),
(29, 51, 60, 50, 50000, DATE_ADD(NOW(), INTERVAL 1095 DAY), 'Sữa tắm Lifebuoy'),
(30, 53, 150, 130, 55000, NULL, 'Giấy A4 Double A'),
(30, 55, 300, 250, 4000, NULL, 'Bút bi Thiên Long');

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
-- ===============================

-- Export Receipt 1 (25 ngày trước) - Xuất từ Import Receipt Item 1, 2
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(1, 1, 1, 20, 5000, 'Xuất mì tôm'),
(1, 2, 4, 10, 45000, 'Xuất bánh quy');

-- Export Receipt 2 (24 ngày trước) - Xuất từ Import Receipt Item 3, 4
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(2, 3, 11, 30, 8000, 'Xuất nước suối'),
(2, 4, 13, 20, 12000, 'Xuất Coca Cola');

-- Export Receipt 3 (23 ngày trước) - Xuất từ Import Receipt Item 5, 6
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(3, 5, 21, 10, 25000, 'Xuất sữa tươi'),
(3, 6, 25, 5, 35000, 'Xuất phô mai');

-- Export Receipt 4-15 (tiếp tục với pattern tương tự)
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(4, 7, 2, 15, 6000, 'Xuất mì Omachi'),
(4, 8, 5, 8, 40000, 'Xuất bánh Richy'),
(5, 9, 14, 25, 12000, 'Xuất Pepsi'),
(5, 10, 15, 20, 10000, 'Xuất trà xanh'),
(6, 11, 29, 10, 25000, 'Xuất cà chua'),
(6, 12, 30, 15, 5000, 'Xuất rau muống'),
(7, 13, 35, 5, 120000, 'Xuất thịt heo'),
(7, 14, 37, 3, 250000, 'Xuất thịt bò'),
(8, 15, 41, 10, 35000, 'Xuất nước rửa chén'),
(8, 16, 43, 5, 85000, 'Xuất bột giặt'),
(9, 17, 47, 8, 65000, 'Xuất dầu gội'),
(9, 18, 49, 6, 25000, 'Xuất kem đánh răng'),
(10, 19, 53, 15, 65000, 'Xuất giấy A4'),
(10, 20, 55, 30, 5000, 'Xuất bút bi'),
(11, 21, 1, 10, 5000, 'Xuất mì tôm'),
(11, 22, 4, 5, 45000, 'Xuất bánh quy'),
(12, 23, 11, 20, 8000, 'Xuất nước suối'),
(12, 24, 13, 15, 12000, 'Xuất Coca Cola'),
(13, 25, 21, 8, 25000, 'Xuất sữa tươi'),
(13, 26, 23, 5, 5000, 'Xuất sữa chua'),
(14, 27, 2, 12, 6000, 'Xuất mì Omachi'),
(14, 28, 7, 10, 25000, 'Xuất bánh Oreo'),
(15, 29, 14, 18, 12000, 'Xuất Pepsi'),
(15, 30, 15, 15, 10000, 'Xuất trà xanh');

-- Export Receipt 16-30 (giữa tháng)
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(16, 31, 29, 8, 25000, 'Xuất cà chua'),
(16, 32, 30, 12, 5000, 'Xuất rau muống'),
(17, 33, 35, 4, 120000, 'Xuất thịt heo'),
(17, 34, 37, 2, 250000, 'Xuất thịt bò'),
(18, 35, 41, 8, 35000, 'Xuất nước rửa chén'),
(18, 36, 45, 5, 40000, 'Xuất nước lau sàn'),
(19, 37, 47, 6, 65000, 'Xuất dầu gội'),
(19, 38, 51, 5, 55000, 'Xuất sữa tắm'),
(20, 39, 53, 12, 65000, 'Xuất giấy A4'),
(20, 40, 55, 25, 5000, 'Xuất bút bi'),
(21, 41, 1, 8, 5000, 'Xuất mì tôm'),
(21, 42, 4, 4, 45000, 'Xuất bánh quy'),
(22, 43, 11, 18, 8000, 'Xuất nước suối'),
(22, 44, 13, 12, 12000, 'Xuất Coca Cola'),
(23, 45, 21, 6, 25000, 'Xuất sữa tươi'),
(23, 46, 23, 4, 5000, 'Xuất sữa chua'),
(24, 47, 2, 10, 6000, 'Xuất mì Omachi'),
(24, 48, 7, 8, 25000, 'Xuất bánh Oreo'),
(25, 49, 14, 15, 12000, 'Xuất Pepsi'),
(25, 50, 15, 12, 10000, 'Xuất trà xanh'),
(26, 51, 29, 6, 25000, 'Xuất cà chua'),
(26, 52, 30, 10, 5000, 'Xuất rau muống'),
(27, 53, 35, 3, 120000, 'Xuất thịt heo'),
(27, 54, 37, 2, 250000, 'Xuất thịt bò'),
(28, 55, 41, 6, 35000, 'Xuất nước rửa chén'),
(28, 56, 45, 4, 40000, 'Xuất nước lau sàn'),
(29, 57, 47, 5, 65000, 'Xuất dầu gội'),
(29, 58, 51, 4, 55000, 'Xuất sữa tắm'),
(30, 59, 53, 10, 65000, 'Xuất giấy A4'),
(30, 60, 55, 20, 5000, 'Xuất bút bi');

-- Export Receipt 31-40 (cuối tháng - lần 2)
INSERT INTO export_receipt_item (export_receipt_id, import_receipt_item_id, product_id, quantity, selling_price, note) VALUES
(31, 1, 1, 15, 5000, 'Xuất mì tôm lần 2'),
(31, 2, 4, 8, 45000, 'Xuất bánh quy lần 2'),
(32, 3, 11, 25, 8000, 'Xuất nước suối lần 2'),
(32, 4, 13, 18, 12000, 'Xuất Coca Cola lần 2'),
(33, 5, 21, 7, 25000, 'Xuất sữa tươi lần 2'),
(33, 6, 25, 4, 35000, 'Xuất phô mai lần 2'),
(34, 7, 2, 12, 6000, 'Xuất mì Omachi lần 2'),
(34, 8, 5, 6, 40000, 'Xuất bánh Richy lần 2'),
(35, 9, 14, 20, 12000, 'Xuất Pepsi lần 2'),
(35, 10, 15, 16, 10000, 'Xuất trà xanh lần 2'),
(36, 11, 29, 5, 25000, 'Xuất cà chua lần 2'),
(36, 12, 30, 8, 5000, 'Xuất rau muống lần 2'),
(37, 13, 35, 2, 120000, 'Xuất thịt heo lần 2'),
(37, 14, 37, 1, 250000, 'Xuất thịt bò lần 2'),
(38, 15, 41, 5, 35000, 'Xuất nước rửa chén lần 2'),
(38, 16, 43, 3, 85000, 'Xuất bột giặt lần 2'),
(39, 17, 47, 4, 65000, 'Xuất dầu gội lần 2'),
(39, 18, 49, 3, 25000, 'Xuất kem đánh răng lần 2'),
(40, 19, 53, 8, 65000, 'Xuất giấy A4 lần 2'),
(40, 20, 55, 15, 5000, 'Xuất bút bi lần 2');




