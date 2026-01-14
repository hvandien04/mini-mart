-- Seed Master Data
-- Categories, Products, Suppliers, Customers mẫu đầy đủ cho hệ thống Mini Mart

-- ===============================
-- SEED CATEGORIES (8 categories)
-- ===============================
INSERT INTO category (name, description, is_active) VALUES
('Thực phẩm khô', 'Các loại thực phẩm khô như mì gói, bánh kẹo, đồ hộp', 1),
('Đồ uống', 'Nước ngọt, nước suối, trà, cà phê', 1),
('Sữa & Sản phẩm từ sữa', 'Sữa tươi, sữa chua, phô mai', 1),
('Rau củ quả', 'Rau tươi, củ quả tươi', 1),
('Thịt & Hải sản', 'Thịt tươi, cá, tôm, cua', 1),
('Đồ gia dụng', 'Dụng cụ nhà bếp, vệ sinh', 1),
('Mỹ phẩm & Chăm sóc cá nhân', 'Dầu gội, sữa tắm, kem đánh răng', 1),
('Đồ dùng văn phòng', 'Giấy, bút, văn phòng phẩm', 1);

-- ===============================
-- SEED SUPPLIERS (10 nhà cung cấp)
-- ===============================
INSERT INTO supplier (name, phone, email, address, note) VALUES
('Công ty TNHH Thực phẩm ABC', '0901234567', 'abc@example.com', '123 Đường ABC, Quận 1, TP.HCM', 'Nhà cung cấp thực phẩm khô chính'),
('Công ty Nước giải khát XYZ', '0902345678', 'xyz@example.com', '456 Đường XYZ, Quận 2, TP.HCM', 'Nhà cung cấp đồ uống'),
('Công ty Sữa Vinamilk', '0903456789', 'vinamilk@example.com', '789 Đường DEF, Quận 3, TP.HCM', 'Nhà cung cấp sữa và sản phẩm từ sữa'),
('Nông trại Rau sạch Green', '0904567890', 'green@example.com', '321 Đường GHI, Quận 4, TP.HCM', 'Nhà cung cấp rau củ quả tươi'),
('Công ty Thịt & Hải sản Fresh', '0905678901', 'fresh@example.com', '654 Đường JKL, Quận 5, TP.HCM', 'Nhà cung cấp thịt và hải sản'),
('Công ty Gia dụng Home', '0906789012', 'home@example.com', '987 Đường MNO, Quận 6, TP.HCM', 'Nhà cung cấp đồ gia dụng'),
('Công ty Mỹ phẩm Beauty', '0907890123', 'beauty@example.com', '147 Đường PQR, Quận 7, TP.HCM', 'Nhà cung cấp mỹ phẩm'),
('Công ty Văn phòng phẩm Office', '0908901234', 'office@example.com', '258 Đường STU, Quận 8, TP.HCM', 'Nhà cung cấp văn phòng phẩm'),
('Công ty Tổng hợp Đa dạng', '0909012345', 'diversified@example.com', '369 Đường VWX, Quận 9, TP.HCM', 'Nhà cung cấp đa dạng sản phẩm'),
('Công ty Nhập khẩu Import', '0900123456', 'import@example.com', '741 Đường YZ, Quận 10, TP.HCM', 'Nhà cung cấp hàng nhập khẩu');

-- ===============================
-- SEED CUSTOMERS (15 khách hàng)
-- ===============================
INSERT INTO customer (full_name, phone, email, address, note) VALUES
('Nguyễn Văn An', '0912345678', 'nguyenvanan@example.com', '100 Đường A, Phường 1, Quận 1, TP.HCM', 'Khách hàng thân thiết'),
('Trần Thị Bình', '0923456789', 'tranthibinh@example.com', '200 Đường B, Phường 2, Quận 2, TP.HCM', 'Khách hàng mua thường xuyên'),
('Lê Văn Cường', '0934567890', 'levancuong@example.com', '300 Đường C, Phường 3, Quận 3, TP.HCM', NULL),
('Phạm Thị Dung', '0945678901', 'phamthidung@example.com', '400 Đường D, Phường 4, Quận 4, TP.HCM', NULL),
('Hoàng Văn Em', '0956789012', 'hoangvanem@example.com', '500 Đường E, Phường 5, Quận 5, TP.HCM', NULL),
('Vũ Thị Phương', '0967890123', 'vuthiphuong@example.com', '600 Đường F, Phường 6, Quận 6, TP.HCM', NULL),
('Đặng Văn Giang', '0978901234', 'dangvangiang@example.com', '700 Đường G, Phường 7, Quận 7, TP.HCM', NULL),
('Bùi Thị Hoa', '0989012345', 'buithihoa@example.com', '800 Đường H, Phường 8, Quận 8, TP.HCM', NULL),
('Đỗ Văn Ích', '0990123456', 'dovanich@example.com', '900 Đường I, Phường 9, Quận 9, TP.HCM', NULL),
('Ngô Thị Kim', '0901234567', 'ngothikim@example.com', '1000 Đường J, Phường 10, Quận 10, TP.HCM', NULL),
('Lý Văn Long', '0912345678', 'lyvanlong@example.com', '1100 Đường K, Phường 11, Quận 11, TP.HCM', NULL),
('Võ Thị Mai', '0923456789', 'vothimai@example.com', '1200 Đường L, Phường 12, Quận 12, TP.HCM', NULL),
('Phan Văn Nam', '0934567890', 'phanvannam@example.com', '1300 Đường M, Phường 13, Quận Bình Thạnh, TP.HCM', NULL),
('Trương Thị Oanh', '0945678901', 'truongthioanh@example.com', '1400 Đường N, Phường 14, Quận Tân Bình, TP.HCM', NULL),
('Đinh Văn Phú', '0956789012', 'dinhvanphu@example.com', '1500 Đường O, Phường 15, Quận Phú Nhuận, TP.HCM', NULL);

-- ===============================
-- SEED PRODUCTS (60 sản phẩm đầy đủ)
-- ===============================
-- Lưu ý: 
-- - Không lưu hạn sử dụng trong Product, giá bán hợp lý cho siêu thị mini
-- - Products sẽ được seed trong V4__seed_products.sql sau khi cột supplier_id đã được tạo trong V3
