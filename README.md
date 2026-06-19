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
* 資料庫存取：Spring Data JPA / JdbcTemplate(參數化查詢)
* 依賴注入：建構子注入

### 資料庫 (Database)
* 系統主體：Microsoft SQL Server
* 核心邏輯：透過 Stored Procedure 執行投票與統計票數

---

## 📂 專案結構 (Project Structure)

```text
VOTE/
├── backend/          # 後端 Spring Boot 專案原始碼
├── frontend/         # 前端 Vue 3 專案原始碼
└── db/               # 資料庫初始化與建置腳本
    └── init.sql      # 包含 DDL, DML, Stored Procedure 的完整 SQL
```

---

## 資料庫建置步驟

1.開啟 SQL Server Management Studio
2.載入並執行專案中 db/init.sql 檔案內的完整腳本。
3.該腳本將建立 VoteSystem 資料庫、建立相關資料表（DDL）、插入測試初始資料（DML，包含電腦、滑鼠），以及註冊投票所需的預存程序（Stored Procedure）。

---

## 專案本地啟動指南

1. 後端啟動 (Backend Setup)
# 進入後端專案資料夾
cd backend

# 使用專案內建的 Maven Wrapper 一鍵編譯並啟動伺服器
.\mvnw.cmd spring-boot:run

2. 前端啟動 (Frontend Setup)
# 進入前端專案資料夾
cd frontend

# 安裝 Node 模組依賴
npm install

# 啟動本地開發伺服器
npm run dev