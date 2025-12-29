USE [ProjectG5];
GO

-- =============================================
-- 1. 系統權限設定
-- =============================================
IF NOT EXISTS (SELECT name FROM sys.database_principals WHERE name = 'G5')
BEGIN
    CREATE USER [G5] FOR LOGIN [G5] WITH DEFAULT_SCHEMA=[dbo];
    ALTER ROLE [db_owner] ADD MEMBER [G5];
END
GO

-- =============================================
-- 2. 建立資料表 (Tables)
-- =============================================

-- [Questions]
IF OBJECT_ID('[dbo].[Questions]', 'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[Questions](
        [id]              INT IDENTITY(1,1) NOT NULL,
        [question_text]   NVARCHAR(255) NOT NULL,
        [correct_answer]  BIT NOT NULL,
        [weight]          INT NOT NULL CONSTRAINT [DF_Questions_Weight] DEFAULT ((1)),
        [is_active]       BIT NOT NULL CONSTRAINT [DF_Questions_IsActive] DEFAULT ((1)),
        CONSTRAINT [PK_Questions] PRIMARY KEY CLUSTERED ([id] ASC)
    );
END
GO

-- [Students]
IF OBJECT_ID('[dbo].[Students]', 'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[Students](
        [id]           INT IDENTITY(1,1) NOT NULL,
        [student_name] NVARCHAR(50) NOT NULL,
        [total_score]  INT NOT NULL CONSTRAINT [DF_Students_TotalScore] DEFAULT ((0)),
        [raw_answers]  VARCHAR(MAX) NOT NULL,
        [exam_date]    DATETIME NOT NULL CONSTRAINT [DF_Students_ExamDate] DEFAULT (GETDATE()),
        CONSTRAINT [PK_Students] PRIMARY KEY CLUSTERED ([id] ASC)
    );
END
GO

-- [UserInfo]
IF OBJECT_ID('[dbo].[UserInfo]', 'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[UserInfo](
        [Username] NVARCHAR(50) NOT NULL,
        [Password] NVARCHAR(50) NOT NULL,
        CONSTRAINT [PK_UserInfo] PRIMARY KEY CLUSTERED ([Username] ASC)
    );
END
GO

-- [SystemUsers]
IF OBJECT_ID('[dbo].[SystemUsers]', 'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[SystemUsers](
        [username] NVARCHAR(50) NULL,
        [password] NVARCHAR(50) NULL
    );
END
GO

-- [ExamRecords]
IF OBJECT_ID('[dbo].[ExamRecords]', 'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[ExamRecords](
        [RecordID]       INT IDENTITY(1,1) NOT NULL,
        [StudentID]      INT NULL,
        [TotalQuestions] INT NULL,
        [WrongCount]     INT NULL,
        [IsCorrectRatio] FLOAT NULL,
        [TestDate]       DATETIME NULL DEFAULT (GETDATE()),
        PRIMARY KEY CLUSTERED ([RecordID] ASC)
    );
END
GO

-- [ExamResults]
IF OBJECT_ID('[dbo].[ExamResults]', 'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[ExamResults](
        [id]              INT IDENTITY(1,1) NOT NULL,
        [student_id]      INT NULL,
        [wrong_count]     INT NULL,
        [total_questions] INT NULL,
        [created_at]      DATETIME NULL,
        PRIMARY KEY CLUSTERED ([id] ASC)
    );
END
GO

-- =============================================
-- 3. 資料初始化 (Data Seeding)
-- =============================================

-- A. 初始化 10 題 Java 題目 (各 10 分)
IF NOT EXISTS (SELECT TOP 1 1 FROM [dbo].[Questions])
BEGIN
    INSERT INTO [dbo].[Questions] ([question_text], [correct_answer], [weight], [is_active])
    VALUES 
    (N'Java 原始碼檔案的副檔名必須是 .java', 1, 10, 1),
    (N'Java 編譯後產生的 Bytecode 檔案副檔名是 .class', 1, 10, 1),
    (N'Java 的 main 方法必須宣告為 public static void', 1, 10, 1),
    (N'int 資料型態在 Java 中佔用 8 個 bytes', 0, 10, 1),
    (N'Java 陣列的索引 (Index) 是從 1 開始的', 0, 10, 1),
    (N'System.out.println 用於在控制台輸出文字並換行', 1, 10, 1),
    (N'Java 語言是「直譯式」語言，不需要編譯', 0, 10, 1),
    (N'一個類別 (Class) 可以繼承 (Extends) 多個父類別', 0, 10, 1),
    (N'String 在 Java 中是基本資料型態 (Primitive Type)', 0, 10, 1),
    (N'在 Java 中，== 運算子用於比較兩個物件的內容是否相同', 0, 10, 1);
    PRINT '已初始化 10 題預設題目。';
END
GO

-- B. 初始化管理員帳號 (預設 Admin / admin123)
-- 針對 UserInfo 表
IF NOT EXISTS (SELECT * FROM [dbo].[UserInfo] WHERE Username = 'admin')
BEGIN
    INSERT INTO [dbo].[UserInfo] (Username, Password) VALUES ('admin', 'admin123');
    PRINT '已建立 UserInfo 管理員: admin';
END

-- 針對 SystemUsers 表 (防止舊程式碼讀錯表)
IF NOT EXISTS (SELECT * FROM [dbo].[SystemUsers] WHERE username = 'admin')
BEGIN
    INSERT INTO [dbo].[SystemUsers] (username, password) VALUES ('admin', 'admin123');
    PRINT '已建立 SystemUsers 管理員: admin';
END
GO