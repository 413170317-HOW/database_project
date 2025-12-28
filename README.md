# 考試模擬系統 (Exam Simulation System)

這是一個基於 Java Swing 與 SQL Server 開發的視窗應用程式，旨在模擬學生考試過程並分析考試數據。系統採用 MVC (Model-View-Controller) 架構設計，具備題庫管理、自動化考試模擬以及成績統計分析功能。

## ✨ 主要功能

本系統分為三大核心模組：

### 1. 題庫管理 (Question Management)
- **檢視題庫**：列表顯示所有題目資訊（題目、正確答案、權重）。
- **新增/編輯/刪除**：支援對題目進行 CRUD 操作。


### 2. 模擬控制 (Simulation Control)
- **參數設定**：目前預設模擬 100,000 名學生進行考試。
- **即時模擬**：利用多執行緒 (SwingWorker) 技術，模擬學生答題行為，並即時更新進度條與狀態。
- **數據生成**：模擬結束後自動計算每位學生的總分並存入資料庫。

### 3. 分析儀表板 (Analysis Dashboard)
- **數據統計**：自動計算平均分及標準差。
- **成績分佈**：支援以柱狀圖顯示各分數區間 (0-9, 10-19...90-99) 的學生人數分佈。



## 🛠️ 技術架構

- **程式語言**：Java (JDK 8+)
- **使用者介面**：Java Swing (JFrame, JPanel, JTable, JFreeChart 概念應用)
- **資料庫**：Microsoft SQL Server
- **資料庫存取**：JDBC (Java Database Connectivity)
- **架構模式**：MVC (Model-View-Controller) 分層架構
  - `view`: UI 介面層
  - `service`: 業務邏輯層 (模擬演算、統計)
  - `dao`: 資料存取層
  - `model`: 資料模型

## 📂 專案結構

```text
c:\database_Project
├── dao/                 # 資料存取物件 (QuestionDAO, StudentDAO)
├── model/               # 資料模型 (Question, Student)
├── service/             # 業務邏輯 (SimulationService, StatService 等)
├── view/                # 視窗介面 (MainFrame, LoginDialog 等)
├── database/            # 資料庫連線設定
├── lib/                 #外部函式庫 (如 mssql-jdbc.jar)
├── schema.sql           # 資料庫初始化腳本
├── Main.java            # 程式進入點
└── README.md            # 專案說明文件
```

## 🚀 安裝與執行說明

### 系統需求
- Java Development Kit (JDK) 8 或以上
- Microsoft SQL Server
- SQL Server JDBC Driver (請確保 `lib` 資料夾中有對應的 `.jar` 檔)

### 資料庫設定
1. 開啟 SQL Server Management Studio (SSMS)。
2. 執行專案根目錄下的 `schema.sql` 腳本，以建立 `Questions` 與 `Students` 資料表。
3. 確保 `database/DatabaseConnection.java` (或相應設定檔) 中的連線資訊 (URL, User, Password) 正確無誤。

### 執行程式
1. 將專案匯入 Eclipse / IntelliJ IDEA 或 VS Code。
2. 確保 `lib` 資料夾中的 JDBC Driver 已加入 Build Path。
3. 執行 `Main.java`。
4. 預設登入帳號/密碼 (若有設定) 請參閱 `LoginService.java`。