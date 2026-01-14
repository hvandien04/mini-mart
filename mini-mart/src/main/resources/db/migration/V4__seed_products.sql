-- Seed Products với supplier_id
-- Migration này chạy sau V3__add_supplier_to_product.sql để đảm bảo cột supplier_id đã tồn tại
-- Lưu ý: 
-- - Không lưu hạn sử dụng trong Product, giá bán hợp lý cho siêu thị mini
-- - Mỗi product phải có supplier_id (ràng buộc Product-Supplier)
-- - Phân bổ products cho các suppliers một cách hợp lý

-- Thực phẩm khô (10 sản phẩm) - Supplier 1 (Thực phẩm ABC)
INSERT INTO product (category_id, supplier_id, sku, name, brand, description, unit, selling_price, is_active) VALUES
(1, 1, 'TPK001', 'Mì tôm Hảo Hảo chua cay', 'Acecook', 'Mì tôm chua cay 75g', 'gói', 5000, 1),
(1, 1, 'TPK002', 'Mì tôm Omachi', 'Omachi', 'Mì tôm Omachi 75g', 'gói', 6000, 1),
(1, 1, 'TPK003', 'Mì tôm Kokomi', 'Kokomi', 'Mì tôm Kokomi 75g', 'gói', 5500, 1),
(1, 1, 'TPK004', 'Bánh quy bơ Danisa', 'Danisa', 'Bánh quy bơ thơm ngon 200g', 'hộp', 45000, 1),
(1, 1, 'TPK005', 'Bánh quy bơ Richy', 'Richy', 'Bánh quy bơ Richy 200g', 'hộp', 40000, 1),
(1, 1, 'TPK006', 'Kẹo dẻo Haribo', 'Haribo', 'Kẹo dẻo trái cây 200g', 'túi', 35000, 1),
(1, 1, 'TPK007', 'Bánh Oreo', 'Oreo', 'Bánh quy kem Oreo 133g', 'hộp', 25000, 1),
(1, 1, 'TPK008', 'Bánh Chocopie', 'Lotte', 'Bánh Chocopie 12 cái', 'hộp', 35000, 1),
(1, 1, 'TPK009', 'Đậu phộng rang muối', 'Không', 'Đậu phộng rang muối 200g', 'túi', 20000, 1),
(1, 1, 'TPK010', 'Hạt điều rang muối', 'Không', 'Hạt điều rang muối 200g', 'túi', 80000, 1);

-- Đồ uống (10 sản phẩm) - Supplier 2 (Nước giải khát XYZ)
INSERT INTO product (category_id, supplier_id, sku, name, brand, description, unit, selling_price, is_active) VALUES
(2, 2, 'DU001', 'Nước suối Lavie 500ml', 'Lavie', 'Nước suối thiên nhiên 500ml', 'chai', 8000, 1),
(2, 2, 'DU002', 'Nước suối Aquafina 500ml', 'Aquafina', 'Nước suối tinh khiết 500ml', 'chai', 8000, 1),
(2, 2, 'DU003', 'Coca Cola 330ml', 'Coca Cola', 'Nước ngọt có ga 330ml', 'lon', 12000, 1),
(2, 2, 'DU004', 'Pepsi 330ml', 'Pepsi', 'Nước ngọt có ga 330ml', 'lon', 12000, 1),
(2, 2, 'DU005', 'Trà xanh không độ 500ml', 'C2', 'Trà xanh không đường 500ml', 'chai', 10000, 1),
(2, 2, 'DU006', 'Trà xanh Oolong 500ml', 'C2', 'Trà xanh Oolong 500ml', 'chai', 10000, 1),
(2, 2, 'DU007', 'Nước cam ép 1L', 'Tropicana', 'Nước cam ép nguyên chất 1L', 'chai', 45000, 1),
(2, 2, 'DU008', 'Nước ép táo 1L', 'Tropicana', 'Nước ép táo nguyên chất 1L', 'chai', 45000, 1),
(2, 2, 'DU009', 'Cà phê hòa tan G7', 'Trung Nguyên', 'Cà phê hòa tan 3in1 16g', 'gói', 3000, 1),
(2, 2, 'DU010', 'Cà phê hòa tan Nescafé', 'Nescafé', 'Cà phê hòa tan 3in1 16g', 'gói', 3500, 1);

-- Sữa & Sản phẩm từ sữa (8 sản phẩm) - Supplier 3 (Sữa Vinamilk)
INSERT INTO product (category_id, supplier_id, sku, name, brand, description, unit, selling_price, is_active) VALUES
(3, 3, 'SUA001', 'Sữa tươi Vinamilk 1L', 'Vinamilk', 'Sữa tươi tiệt trùng 1L', 'hộp', 25000, 1),
(3, 3, 'SUA002', 'Sữa tươi TH True Milk 1L', 'TH True Milk', 'Sữa tươi tiệt trùng 1L', 'hộp', 28000, 1),
(3, 3, 'SUA003', 'Sữa chua Vinamilk', 'Vinamilk', 'Sữa chua có đường 100g', 'hộp', 5000, 1),
(3, 3, 'SUA004', 'Sữa chua TH True Milk', 'TH True Milk', 'Sữa chua có đường 100g', 'hộp', 5500, 1),
(3, 3, 'SUA005', 'Phô mai con bò cười', 'Laughing Cow', 'Phô mai 120g', 'hộp', 35000, 1),
(3, 3, 'SUA006', 'Phô mai Bel', 'Bel', 'Phô mai Bel 120g', 'hộp', 40000, 1),
(3, 3, 'SUA007', 'Sữa đặc Ông Thọ', 'Vinamilk', 'Sữa đặc có đường 380g', 'hộp', 25000, 1),
(3, 3, 'SUA008', 'Sữa đặc Cô Gái Hà Lan', 'FrieslandCampina', 'Sữa đặc có đường 380g', 'hộp', 28000, 1);

-- Rau củ quả (8 sản phẩm) - Supplier 4 (Nông trại Rau sạch Green)
INSERT INTO product (category_id, supplier_id, sku, name, brand, description, unit, selling_price, is_active) VALUES
(4, 4, 'RCQ001', 'Cà chua', 'Không', 'Cà chua tươi', 'kg', 25000, 1),
(4, 4, 'RCQ002', 'Rau muống', 'Không', 'Rau muống tươi', 'bó', 5000, 1),
(4, 4, 'RCQ003', 'Cà rốt', 'Không', 'Cà rốt tươi', 'kg', 20000, 1),
(4, 4, 'RCQ004', 'Khoai tây', 'Không', 'Khoai tây tươi', 'kg', 30000, 1),
(4, 4, 'RCQ005', 'Hành tây', 'Không', 'Hành tây tươi', 'kg', 25000, 1),
(4, 4, 'RCQ006', 'Tỏi', 'Không', 'Tỏi tươi', 'kg', 80000, 1),
(4, 4, 'RCQ007', 'Ớt', 'Không', 'Ớt tươi', 'kg', 60000, 1),
(4, 4, 'RCQ008', 'Rau cải', 'Không', 'Rau cải tươi', 'bó', 8000, 1);

-- Thịt & Hải sản (6 sản phẩm) - Supplier 5 (Thịt & Hải sản Fresh)
INSERT INTO product (category_id, supplier_id, sku, name, brand, description, unit, selling_price, is_active) VALUES
(5, 5, 'THS001', 'Thịt heo ba chỉ', 'Không', 'Thịt heo ba chỉ tươi', 'kg', 120000, 1),
(5, 5, 'THS002', 'Thịt heo nạc', 'Không', 'Thịt heo nạc tươi', 'kg', 150000, 1),
(5, 5, 'THS003', 'Thịt bò', 'Không', 'Thịt bò tươi', 'kg', 250000, 1),
(5, 5, 'THS004', 'Cá basa fillet', 'Không', 'Cá basa đã fillet', 'kg', 80000, 1),
(5, 5, 'THS005', 'Tôm sú', 'Không', 'Tôm sú tươi', 'kg', 200000, 1),
(5, 5, 'THS006', 'Cá thu', 'Không', 'Cá thu tươi', 'kg', 100000, 1);

-- Đồ gia dụng (6 sản phẩm) - Supplier 6 (Gia dụng Home)
INSERT INTO product (category_id, supplier_id, sku, name, brand, description, unit, selling_price, is_active) VALUES
(6, 6, 'DGD001', 'Nước rửa chén Sunlight', 'Sunlight', 'Nước rửa chén 900ml', 'chai', 35000, 1),
(6, 6, 'DGD002', 'Nước rửa chén Joy', 'Joy', 'Nước rửa chén 900ml', 'chai', 38000, 1),
(6, 6, 'DGD003', 'Bột giặt Omo', 'Omo', 'Bột giặt 2kg', 'túi', 85000, 1),
(6, 6, 'DGD004', 'Bột giặt Tide', 'Tide', 'Bột giặt 2kg', 'túi', 90000, 1),
(6, 6, 'DGD005', 'Nước lau sàn Sunlight', 'Sunlight', 'Nước lau sàn 900ml', 'chai', 40000, 1),
(6, 6, 'DGD006', 'Khăn giấy ướt', 'Kleenex', 'Khăn giấy ướt 80 tờ', 'gói', 25000, 1);

-- Mỹ phẩm & Chăm sóc cá nhân (8 sản phẩm) - Supplier 7 (Mỹ phẩm Beauty)
INSERT INTO product (category_id, supplier_id, sku, name, brand, description, unit, selling_price, is_active) VALUES
(7, 7, 'MP001', 'Dầu gội đầu Clear', 'Clear', 'Dầu gội đầu 400ml', 'chai', 65000, 1),
(7, 7, 'MP002', 'Dầu gội đầu Head & Shoulders', 'Head & Shoulders', 'Dầu gội đầu 400ml', 'chai', 70000, 1),
(7, 7, 'MP003', 'Kem đánh răng P/S', 'P/S', 'Kem đánh răng 120g', 'tuýp', 25000, 1),
(7, 7, 'MP004', 'Kem đánh răng Colgate', 'Colgate', 'Kem đánh răng 120g', 'tuýp', 28000, 1),
(7, 7, 'MP005', 'Sữa tắm Lifebuoy', 'Lifebuoy', 'Sữa tắm 750ml', 'chai', 55000, 1),
(7, 7, 'MP006', 'Sữa tắm Dove', 'Dove', 'Sữa tắm 750ml', 'chai', 60000, 1),
(7, 7, 'MP007', 'Xà phòng Lifebuoy', 'Lifebuoy', 'Xà phòng 125g', 'bánh', 15000, 1),
(7, 7, 'MP008', 'Xà phòng Dove', 'Dove', 'Xà phòng 125g', 'bánh', 18000, 1);

-- Đồ dùng văn phòng (4 sản phẩm) - Supplier 8 (Văn phòng phẩm Office)
INSERT INTO product (category_id, supplier_id, sku, name, brand, description, unit, selling_price, is_active) VALUES
(8, 8, 'DVP001', 'Giấy A4 Double A', 'Double A', 'Giấy A4 500 tờ', 'ram', 65000, 1),
(8, 8, 'DVP002', 'Giấy A4 Paper One', 'Paper One', 'Giấy A4 500 tờ', 'ram', 70000, 1),
(8, 8, 'DVP003', 'Bút bi Thiên Long', 'Thiên Long', 'Bút bi xanh', 'cây', 5000, 1),
(8, 8, 'DVP004', 'Bút chì 2B', 'Không', 'Bút chì 2B', 'cây', 3000, 1);

