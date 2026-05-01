

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ── Users ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN','ARTISAN','BUYER','MARKETING')),
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE','SUSPENDED')),
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP DEFAULT NOW()
);

-- ── Artisan Profiles ──────────────────────────────────
CREATE TABLE IF NOT EXISTS artisan_profiles (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT REFERENCES users(id) ON DELETE CASCADE,
    bio         TEXT,
    specialty   VARCHAR(100),
    location    VARCHAR(150),
    avatar_url  VARCHAR(500),
    is_approved BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT NOW()
);

-- ── Buyer Profiles ────────────────────────────────────
CREATE TABLE IF NOT EXISTS buyer_profiles (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT REFERENCES users(id) ON DELETE CASCADE,
    phone       VARCHAR(20),
    address     TEXT,
    city        VARCHAR(100),
    country     VARCHAR(100) DEFAULT 'India',
    created_at  TIMESTAMP DEFAULT NOW()
);

-- ── Categories ────────────────────────────────────────
CREATE TABLE IF NOT EXISTS categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    slug        VARCHAR(100) NOT NULL UNIQUE,
    image_url   VARCHAR(500),
    created_at  TIMESTAMP DEFAULT NOW()
);

-- ── Products ──────────────────────────────────────────
CREATE TABLE IF NOT EXISTS products (
    id                  BIGSERIAL PRIMARY KEY,
    artisan_id          BIGINT REFERENCES users(id) ON DELETE CASCADE,
    name                VARCHAR(200) NOT NULL,
    description         TEXT,
    price               DECIMAL(10,2) NOT NULL,
    original_price      DECIMAL(10,2),
    category            VARCHAR(100),
    material            VARCHAR(100),
    weight              VARCHAR(50),
    care_instructions   TEXT,
    stock_count         INTEGER NOT NULL DEFAULT 0,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','APPROVED','REJECTED')),
    created_at          TIMESTAMP DEFAULT NOW(),
    updated_at          TIMESTAMP DEFAULT NOW()
);

-- ── Product Images ────────────────────────────────────
CREATE TABLE IF NOT EXISTS product_images (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT REFERENCES products(id) ON DELETE CASCADE,
    image_url   VARCHAR(500) NOT NULL,
    is_primary  BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT NOW()
);

-- ── Inventory ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS inventory (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT REFERENCES products(id) ON DELETE CASCADE UNIQUE,
    quantity    INTEGER NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP DEFAULT NOW()
);

-- ── Wishlist ──────────────────────────────────────────
CREATE TABLE IF NOT EXISTS wishlist (
    id          BIGSERIAL PRIMARY KEY,
    buyer_id    BIGINT REFERENCES users(id) ON DELETE CASCADE,
    product_id  BIGINT REFERENCES products(id) ON DELETE CASCADE,
    added_at    TIMESTAMP DEFAULT NOW(),
    UNIQUE(buyer_id, product_id)
);

-- ── Cart ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS cart (
    id          BIGSERIAL PRIMARY KEY,
    buyer_id    BIGINT REFERENCES users(id) ON DELETE CASCADE UNIQUE,
    created_at  TIMESTAMP DEFAULT NOW()
);

-- ── Cart Items ────────────────────────────────────────
CREATE TABLE IF NOT EXISTS cart_items (
    id          BIGSERIAL PRIMARY KEY,
    cart_id     BIGINT REFERENCES cart(id) ON DELETE CASCADE,
    product_id  BIGINT REFERENCES products(id) ON DELETE CASCADE,
    quantity    INTEGER NOT NULL DEFAULT 1,
    UNIQUE(cart_id, product_id)
);

-- ── Orders ────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS orders (
    id                  BIGSERIAL PRIMARY KEY,
    buyer_id            BIGINT REFERENCES users(id),
    total_amount        DECIMAL(10,2) NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','CONFIRMED','SHIPPED','DELIVERED','CANCELLED')),
    shipping_address    TEXT,
    tracking_number     VARCHAR(100),
    created_at          TIMESTAMP DEFAULT NOW(),
    updated_at          TIMESTAMP DEFAULT NOW()
);

-- ── Order Items ───────────────────────────────────────
CREATE TABLE IF NOT EXISTS order_items (
    id          BIGSERIAL PRIMARY KEY,
    order_id    BIGINT REFERENCES orders(id) ON DELETE CASCADE,
    product_id  BIGINT REFERENCES products(id),
    quantity    INTEGER NOT NULL,
    unit_price  DECIMAL(10,2) NOT NULL
);

-- ── Payments ──────────────────────────────────────────
CREATE TABLE IF NOT EXISTS payments (
    id              BIGSERIAL PRIMARY KEY,
    order_id        BIGINT REFERENCES orders(id),
    amount          DECIMAL(10,2) NOT NULL,
    method          VARCHAR(50),
    status          VARCHAR(20) DEFAULT 'PENDING',
    transaction_id  VARCHAR(200),
    created_at      TIMESTAMP DEFAULT NOW()
);

-- ── Shipping Details ──────────────────────────────────
CREATE TABLE IF NOT EXISTS shipping_details (
    id              BIGSERIAL PRIMARY KEY,
    order_id        BIGINT REFERENCES orders(id) UNIQUE,
    carrier         VARCHAR(100),
    tracking_number VARCHAR(100),
    estimated_date  DATE,
    delivered_at    TIMESTAMP
);

-- ── Reviews ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS reviews (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT REFERENCES products(id) ON DELETE CASCADE,
    buyer_id    BIGINT REFERENCES users(id),
    rating      INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment     TEXT,
    created_at  TIMESTAMP DEFAULT NOW()
);

-- ── Campaigns ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS campaigns (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    type        VARCHAR(50),
    status      VARCHAR(20) DEFAULT 'DRAFT' CHECK (status IN ('DRAFT','ACTIVE','ENDED')),
    start_date  DATE,
    end_date    DATE,
    created_by  BIGINT REFERENCES users(id),
    created_at  TIMESTAMP DEFAULT NOW()
);

-- ── Banners ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS banners (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    image_url   VARCHAR(500),
    link_url    VARCHAR(500),
    position    VARCHAR(50),
    is_active   BOOLEAN DEFAULT TRUE,
    created_by  BIGINT REFERENCES users(id),
    created_at  TIMESTAMP DEFAULT NOW()
);

-- ── Notifications ─────────────────────────────────────
CREATE TABLE IF NOT EXISTS notifications (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT REFERENCES users(id) ON DELETE CASCADE,
    title       VARCHAR(200),
    message     TEXT,
    is_read     BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT NOW()
);

-- ── Reports ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS reports (
    id              BIGSERIAL PRIMARY KEY,
    reporter_id     BIGINT REFERENCES users(id),
    reported_type   VARCHAR(50),
    reported_id     BIGINT,
    reason          TEXT,
    status          VARCHAR(20) DEFAULT 'OPEN',
    created_at      TIMESTAMP DEFAULT NOW()
);

-- ============================================================
-- INDEXES for performance
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_products_status ON products(status);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_products_artisan ON products(artisan_id);
CREATE INDEX IF NOT EXISTS idx_orders_buyer ON orders(buyer_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_reviews_product ON reviews(product_id);
CREATE INDEX IF NOT EXISTS idx_wishlist_buyer ON wishlist(buyer_id);

-- ============================================================
-- SEED DATA - Sample categories
-- ============================================================
INSERT INTO categories (name, slug) VALUES
    ('Sarees', 'sarees'),
    ('Dupattas', 'dupattas'),
    ('Shawls', 'shawls'),
    ('Kurtas', 'kurtas'),
    ('Stoles', 'stoles'),
    ('Home Textiles', 'home-textiles')
ON CONFLICT (slug) DO NOTHING;

-- ============================================================
-- SEED DATA - Admin user (password: admin123)
-- BCrypt hash of 'admin123'
-- ============================================================
INSERT INTO users (name, email, password, role) VALUES
    ('Admin User', 'admin@handloom.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN'),
    ('Meera Devi', 'artisan@handloom.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ARTISAN'),
    ('Sarah Johnson', 'buyer@handloom.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'BUYER'),
    ('Marketing Team', 'marketing@handloom.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MARKETING')
ON CONFLICT (email) DO NOTHING;
