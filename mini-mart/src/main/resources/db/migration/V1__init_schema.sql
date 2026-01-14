CREATE TABLE category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(250) NOT NULL,
    description VARCHAR(500),
    is_active TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB;

-- ===============================
-- TABLE: customer
-- ===============================
CREATE TABLE customer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(250) NOT NULL,
    phone VARCHAR(50),
    email VARCHAR(255),
    address VARCHAR(1000),
    note VARCHAR(1000)
) ENGINE=InnoDB;

-- ===============================
-- TABLE: supplier
-- ===============================
CREATE TABLE supplier (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(500) NOT NULL,
    phone VARCHAR(50),
    email VARCHAR(255),
    address VARCHAR(500),
    note VARCHAR(1000)
) ENGINE=InnoDB;

-- ===============================
-- TABLE: User
-- ===============================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    address VARCHAR(500),
    role VARCHAR(50) NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT NOW(),
    CHECK (role IN ('Staff', 'Admin'))
) ENGINE=InnoDB;

-- ===============================
-- TABLE: product
-- ===============================
CREATE TABLE product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(500) NOT NULL,
    brand VARCHAR(250),
    description TEXT,
    image_url VARCHAR(500),
    unit VARCHAR(50),
    selling_price DECIMAL(18,2) NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT NOW(),
    CONSTRAINT FK_product_category
     FOREIGN KEY (category_id)
         REFERENCES category(id)
) ENGINE=InnoDB;

-- ===============================
-- TABLE: import_receipt
-- ===============================
CREATE TABLE import_receipt (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    supplier_id INT NOT NULL,
    import_date DATETIME NOT NULL,
    note VARCHAR(1000),
    created_at DATETIME NOT NULL DEFAULT NOW(),
    CONSTRAINT FK_import_receipt_User
       FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT FK_import_receipt_supplier
       FOREIGN KEY (supplier_id) REFERENCES supplier(id)
) ENGINE=InnoDB;

-- ===============================
-- TABLE: import_receipt_item
-- ===============================
CREATE TABLE import_receipt_item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    import_receipt_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    remaining_quantity INT NOT NULL,
    import_price DECIMAL(18,2) NOT NULL,
    expire_date DATE,
    note VARCHAR(1000),
    CONSTRAINT FK_ImportItem_import_receipt
        FOREIGN KEY (import_receipt_id) REFERENCES import_receipt(id),
    CONSTRAINT FK_ImportItem_product
        FOREIGN KEY (product_id) REFERENCES product(id),
    CHECK (quantity > 0),
    CHECK (remaining_quantity >= 0)
) ENGINE=InnoDB;

CREATE TABLE export_receipt (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    customer_id INT NOT NULL,
    export_date DATETIME NOT NULL,
    note VARCHAR(1000),
    created_at DATETIME NOT NULL DEFAULT NOW(),
    CONSTRAINT FK_export_receipt_User
       FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT FK_export_receipt_customer
       FOREIGN KEY (customer_id) REFERENCES customer(id)
) ENGINE=InnoDB;

-- ===============================
-- TABLE: export_receipt_item
-- ===============================
CREATE TABLE export_receipt_item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    export_receipt_id INT NOT NULL,
    import_receipt_item_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    selling_price DECIMAL(18,2) NOT NULL,
    note VARCHAR(1000),
    CONSTRAINT FK_ExportItem_export_receipt
       FOREIGN KEY (export_receipt_id) REFERENCES export_receipt(id),
    CONSTRAINT FK_ExportItem_import_receipt_item
       FOREIGN KEY (import_receipt_item_id) REFERENCES import_receipt_item(id),
    CONSTRAINT FK_ExportItem_product
       FOREIGN KEY (product_id) REFERENCES product(id),
    CHECK (quantity > 0)
) ENGINE=InnoDB;
