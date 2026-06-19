# 線上投票系統

這是一個採用**前後端分離架構**開發的線上投票系統。前端使用 Vue 3 建立高質感的互動介面，後端基於 Spring Boot 提供 RESTful API，並結合 SQL Server 透過預存程序（Stored Procedure）進行高效且安全的事務處理。

---

## 🛠️ 技術棧 (Tech Stack)

### 前端 (Frontend)
* 框架：Vue 3 (Vite)
* 連線工具：Axios
* 樣式：獨立 CSS 模組化設計

### 後端 (Backend)
* 框架：Spring Boot
* 資料庫存取：Spring Data JPA / JdbcTemplate
* 事務管理：@Transactional

### 資料庫 (Database)
* SQL Server
* 核心邏輯：透過 Stored Procedure 執行投票與統計票數

---

## 📂 專案結構 (Project Structure)

```text
VOTE/
├── vote/             # 後端 Spring Boot 專案
└── frontend/         # 前端 Vue 3 專案