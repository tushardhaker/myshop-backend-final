# 🛒 MyShop.pro – Multi-Vendor E-commerce Platform

**MyShop.pro** is a robust, full-stack e-commerce application designed to bridge the gap between local shopkeepers and customers. Built using **Java Spring Boot** and **MySQL**, the platform offers a secure, scalable, and high-performance shopping experience.

---

## 🚀 Live Demo
🔗 [View Live Project](https://myshop-pro-tushar-2004.vercel.app/)

## 🛠️ Tech Stack
* **Backend:** Java 17+, Spring Boot 3.x, Spring Security, Spring Data JPA.
* **Database:** MySQL (Relational Schema).
* **Frontend:** JavaScript (ES6+), HTML5, CSS3, Bootstrap 5.
* **Authentication:** Google OAuth 2.0 (Google Login).
* **Deployment:** Render / AWS / Railway.

---

## ✨ Key Features

### 👤 Customer Features
* **Social Login:** Secure and fast onboarding using **Google Login**.
* **Dynamic Product Search:** Filter products by 25+ categories with real-time search.
* **Multi-Image Sliders:** View products from multiple angles using an interactive **Bootstrap Carousel**.
* **Persistent Cart:** Cart and Wishlist items are saved locally using **Browser LocalStorage**.
* **Secure Checkout:** Integrated payment gateway (Sandbox mode) for seamless transactions.

### 🏪 Shopkeeper Dashboard
* **Inventory Management:** Add, update, or delete products with ease.
* **Advanced Image Support:** Support for multiple high-resolution image URLs (optimized via `TEXT` database type).
* **Sales Analytics:** Track total revenue, top-selling products, and stock alerts.
* **Staff Management:** Manage shop roles and inventory visibility.

---

## 🏗️ System Architecture
The project follows a **Layered Architecture** to ensure clean code and scalability:
1.  **Controller Layer:** Handles REST API endpoints and HTTP requests.
2.  **Service Layer:** Contains the core business logic and validations.
3.  **Repository Layer:** Interacts with the MySQL database using **Hibernate/JPA**.
4.  **Security Layer:** Manages OAuth2 and session-based authentication.

---

## 🔧 Database Optimization Case Study
**Problem:** Initially, the `imageUrl` column used `VARCHAR(255)`, which caused data truncation when sellers added multiple high-resolution image links.
**Solution:** Migrated the column type to `TEXT` in the MySQL schema and updated the JPA Entity with `@Column(columnDefinition = "TEXT")`. 
**Result:** Enabled support for unlimited image assets per product, significantly improving the UI's visual appeal.

---
