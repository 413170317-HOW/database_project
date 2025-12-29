USE [ProjectG5];
GO

-- =============================================
-- 1. 系統權限設定 (Security)
-- =============================================
-- 建立應用程式專用使用者 G5，並給予權限
CREATE USER [G5] FOR LOGIN [G5] WITH DEFAULT_SCHEMA=[dbo];
GO
ALTER ROLE [db_owner] ADD MEMBER [G5];
GO

-- =============================================
-- 2. 核心功能模組 (Core Simulation Module)
-- =============================================

-- [Questions] 題目表：儲存題目內容、正確答案與權重
CREATE TABLE [dbo].[Questions](
    [id]             INT IDENTITY(1,1) NOT NULL,
    [question_text]  NVARCHAR(255) NOT NULL,
    [correct_answer] BIT NOT NULL,
    [weight]         INT NOT NULL,
    [is_active]      BIT NOT NULL,

    CONSTRAINT [PK_Questions] PRIMARY KEY CLUSTERED ([id] ASC),
    CONSTRAINT [DF_Questions_Weight] DEFAULT ((1)) FOR [weight],
    CONSTRAINT [DF_Questions_IsActive] DEFAULT ((1)) FOR [is_active]
);
GO

-- [Students] 學生表：核心模擬資料，含原始作答字串 (varchar max)
CREATE TABLE [dbo].[Students](
    [id]           INT IDENTITY(1,1) NOT NULL,
    [student_name] NVARCHAR(50) NOT NULL,
    [total_score]  INT NOT NULL,
    [raw_answers]  VARCHAR(MAX) NOT NULL, -- 關鍵：儲存作答歷程
    [exam_date]    DATETIME NOT NULL,

    CONSTRAINT [PK_Students] PRIMARY KEY CLUSTERED ([id] ASC),
    CONSTRAINT [DF_Students_TotalScore] DEFAULT ((0)) FOR [total_score],
    CONSTRAINT [DF_Students_ExamDate] DEFAULT (GETDATE()) FOR [exam_date]
);
GO

-- =============================================
-- 3. 管理員模組 (Admin/Login Module)
-- =============================================

-- [UserInfo] 管理員資訊表 (建議使用此表，因為有 Primary Key)
CREATE TABLE [dbo].[UserInfo](
    [Username] NVARCHAR(50) NOT NULL,
    [Password] NVARCHAR(50) NOT NULL,
    
    CONSTRAINT [PK_UserInfo] PRIMARY KEY CLUSTERED ([Username] ASC)
);
GO

-- [SystemUsers] 系統使用者 (備用/舊表)
CREATE TABLE [dbo].[SystemUsers](
    [username] NVARCHAR(50) NULL,
    [password] NVARCHAR(50) NULL
);
GO

-- =============================================
-- 4. 歷史紀錄與擴充模組 (Legacy/Expansion)
-- =============================================

-- [ExamRecords] 考試紀錄擴充表 (用於紀錄詳細考試數據)
CREATE TABLE [dbo].[ExamRecords](
    [RecordID]       INT IDENTITY(1,1) NOT NULL,
    [StudentID]      INT NULL,
    [TotalQuestions] INT NULL,
    [WrongCount]     INT NULL,
    [IsCorrectRatio] FLOAT NULL,
    [TestDate]       DATETIME NULL DEFAULT (GETDATE()),

    PRIMARY KEY CLUSTERED ([RecordID] ASC)
);
GO

-- [ExamResults] 考試結果細項表
CREATE TABLE [dbo].[ExamResults](
    [id]              INT IDENTITY(1,1) NOT NULL,
    [student_id]      INT NULL,
    [wrong_count]     INT NULL,
    [total_questions] INT NULL,
    [created_at]      DATETIME NULL,

    PRIMARY KEY CLUSTERED ([id] ASC)
);
GO