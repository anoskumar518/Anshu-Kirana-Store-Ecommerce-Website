# Anshu Kirana Store (అంశు కిరాణా స్టోర్)
### Professional Village Kirana E-Commerce Platform

A modern, responsive, full-stack grocery e-commerce platform built for local Kirana shops in Indian villages and small towns. Designed to provide a BigBasket/Blinkit/JioMart shopping experience, optimized for rural customers with dual English/Telugu language support, village delivery area selection, doorstep Cash on Delivery (COD), and an admin store-management dashboard.

---

## 1. Key Features

### For Village Customers
- **Bilingual Interface**: Seamless 1-click toggle between **English** and **Telugu (తెలుగు)** across all categories, products, prices, and checkout steps.
- **Village Delivery Area Selector**: Easily select delivery location (e.g., Repudi Village, Kummarikunta, Kothapalli, Nagareddy Gudam, Ananthapur Village, etc.).
- **Grocery Catalog with Pack Size Variants**: Support for 250g, 500g, 1kg, 2kg, 5kg, 10kg, 25kg, 1L, and multipacks with live price & MRP savings calculation.
- **Smart Shopping Cart**: Live quantity modifiers, inventory limit checks, and free village delivery progress meter (*"Add ₹XX more for FREE delivery!"*).
- **Simple 1-Click Village Checkout**: Simple checkout with Cash on Delivery (COD) enabled out-of-the-box (and UPI QR code on delivery).
- **Order Tracking**: Visual status timeline tracking order progression:
  $$\text{PLACED} \longrightarrow \text{CONFIRMED} \longrightarrow \text{PACKING} \longrightarrow \text{OUT\_FOR\_DELIVERY} \longrightarrow \text{DELIVERED}$$
- **Direct WhatsApp Ordering**: Quick floating WhatsApp button with pre-filled order messages.
- **Re-Order Grocery Essentials**: 1-click reorder button to instantly reload previous ration items into cart.

### For Store Owners & Admins
- **Interactive Overview Dashboard**: Live KPIs for Total Revenue (₹), Today's Revenue (₹), Total Orders, Pending Orders, Low-Stock items, and Village Customers.
- **Sales Analytics Chart**: Visual daily sales trends powered by Chart.js.
- **Product Management**: Full CRUD for products, image URLs, pack size variants, MRP discounts, and homepage featured status.
- **Bilingual Category Management**: Create and manage English and Telugu categories with custom slugs and icons.
- **Inventory & Stock Management**: Real-time stock levels, low-stock threshold detection, adjustment modal, and stock movement audit history.
- **Order Processing & Printable Bills**: Update order statuses, process cancellations (with automatic inventory restock), and print customer tax/cash receipts.

---

## 2. Technology Stack

- **Backend**:
  - Java 17 LTS
  - Spring Boot 3.2.x (Spring Web, Spring Data JPA, Spring Security, Spring Validation)
  - JSON Web Tokens (JJWT 0.11.5) for stateless REST authentication
  - Flyway for reliable database schema migrations
  - Maven for build lifecycle and dependency management
- **Database**:
  - MySQL 8.0+ (Production & Local Development)
  - In-Memory H2 Database profile (Zero-setup development in Eclipse)
- **Frontend**:
  - HTML5, CSS3, JavaScript (ES6 Modules)
  - Bootstrap 5.3 & Bootstrap Icons
  - Fetch API for asynchronous backend communication
  - Bilingual translation engine (`i18n.js`)

---

## 3. Database Design & Tables

The database follows a clean relational schema with foreign keys, indexes, and audit timestamps:

1. `users`: Stores registered customers and administrators.
2. `roles`: Role definitions (`ROLE_ADMIN`, `ROLE_CUSTOMER`).
3. `user_roles`: Many-to-many user to role mapping.
4. `customer_addresses`: Saved village delivery addresses.
5. `categories`: Bilingual grocery categories with slugs.
6. `products`: Grocery items with brands, descriptions, and categories.
7. `product_variants`: Pack sizes (e.g. 1kg, 5kg, 1L), MRP, and selling prices.
8. `inventory`: Available quantity and low-stock threshold per variant.
9. `carts`: Active shopping carts for customers.
10. `cart_items`: Variant quantity in cart.
11. `orders`: Order header with village delivery details, totals, and statuses.
12. `order_items`: Historical price snapshot of purchased items.
13. `payments`: Payment records (COD, UPI, status).
14. `stock_movements`: Audit log of all stock increases, sales, and cancellation restocks.
15. `offers`: Promotional coupons and discounts.

---

## 4. Test Credentials

The application automatically seeds default accounts on startup:

| Role | Mobile Number / Email | Password | Access |
|---|---|---|---|
| **Store Admin** | `7993542873` / `admin@anshukirana.com` | `admin123` | Full Admin Dashboard & Store Management |
| **Village Customer** | `9876543211` / `customer@village.com` | `customer123` | Customer Storefront, Cart & Orders |

---

## 5. Running the Application

### Option A: Running in Eclipse IDE (Recommended for Development)

1. **Open Eclipse IDE**:
   - Ensure you have **Eclipse IDE for Enterprise Java and Web Developers** (2022-09 or newer) with Java 17 installed.
2. **Import the Project**:
   - Click `File` -> `Import...` -> `Maven` -> `Existing Maven Projects`.
   - Browse to the project folder (`my website`) and select `pom.xml`.
   - Click `Finish` and wait for Maven to download dependencies.
3. **Run as Spring Boot App**:
   - Right-click `AnshuKiranaStoreApplication.java` (under `src/main/java/com/kirana/`).
   - Select `Run As` -> `Spring Boot App` (or `Java Application`).
4. **Access the Website**:
   - Open your web browser and navigate to:
     - **Customer Storefront**: `http://localhost:8080/`
     - **Admin Dashboard**: `http://localhost:8080/admin/index.html`
     - **H2 In-Memory Database Console (if active)**: `http://localhost:8080/h2-console`

### Option B: Running with Command-Line Maven

1. Open PowerShell or Command Prompt in the project directory.
2. Run with H2 In-Memory Database (No external MySQL server needed):
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=h2
   ```
3. Run with Local MySQL Server:
   - Ensure MySQL is running on port `3306`.
   - Create database: `CREATE DATABASE anshu_kirana_db;`
   - Set password in `src/main/resources/application.yml` or pass via environment variables:
   ```bash
   mvn spring-boot:run
   ```

---

## 6. Complete REST API Endpoints

### Authentication APIs
- `POST /api/auth/register`: Register new customer with mobile number & password.
- `POST /api/auth/login`: Authenticate customer or admin, returns JWT token.
- `GET /api/auth/me`: Get current authenticated user profile.

### Product & Category APIs
- `GET /api/products`: Paginated product listing with filters (`categoryId`, `brand`, `keyword`, `sortBy`, `direction`).
- `GET /api/products/{id}`: Detailed product info with pack variants and inventory stock.
- `GET /api/products/featured`: Homepage featured grocery items.
- `GET /api/products/brands`: Distinct brands list.
- `GET /api/categories`: Active categories.
- `GET /api/store/config`: Store details, tagline, WhatsApp helpline, and serviceable village list.

### Shopping Cart APIs
- `GET /api/cart`: Current customer's cart with calculated subtotal, delivery charge, and totals.
- `POST /api/cart/items`: Add variant to cart with stock validation.
- `PUT /api/cart/items/{id}`: Modify item quantity.
- `DELETE /api/cart/items/{id}`: Remove item from cart.

### Order APIs
- `POST /api/orders`: Place new COD order (deducts stock and snapshots prices).
- `GET /api/orders/my-orders`: Customer's order history.
- `GET /api/orders/{id}`: Order details.
- `PUT /api/orders/{id}/cancel`: Cancel eligible order (automatically restores inventory balance).

### Admin APIs (`ROLE_ADMIN` Required)
- `GET /api/admin/dashboard/statistics`: Overview metrics (revenue, orders, low-stock count).
- `GET /api/admin/dashboard/sales`: Daily sales chart data.
- `GET /api/admin/dashboard/top-products`: Best-selling products report.
- `GET /api/admin/orders`: List all orders with status filtering.
- `PUT /api/admin/orders/{id}/status`: Advance status (`CONFIRMED`, `PACKING`, `OUT_FOR_DELIVERY`, `DELIVERED`, `CANCELLED`).
- `POST /api/admin/products`: Create new product with variants and inventory.
- `PUT /api/admin/products/{id}`: Update product details.
- `DELETE /api/admin/products/{id}`: Deactivate product.
- `GET /api/admin/inventory/low-stock`: Low stock alerts.
- `PUT /api/admin/inventory/{variantId}`: Adjust stock quantity & threshold with audit movement log.
- `GET /api/admin/inventory/{variantId}/movements`: Stock movement history.

---

## 7. Running Tests

Execute backend unit and integration tests:
```bash
mvn clean test
```

Test coverage includes:
- `ProductServiceTest`: Product filtering, pagination, and variant mapping.
- `InventoryServiceTest`: Stock updates, low-stock detection, and preventing negative inventory.
- `OrderServiceTest`: Transactional checkout, inventory deduction, price snapshots, and cancellation restocking.
- `AnshuKiranaStoreIntegrationTest`: End-to-end flow from store configuration, categories, product search, authentication, and order placement.
