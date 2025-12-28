-- Create Database (Optional, assumming ProjectG5 already exists or user will create it)
-- USE ProjectG5;
-- GO

-- Drop tables if they exist to start fresh
IF OBJECT_ID('dbo.Students', 'U') IS NOT NULL DROP TABLE dbo.Students;
IF OBJECT_ID('dbo.Questions', 'U') IS NOT NULL DROP TABLE dbo.Questions;
GO

-- Create Questions Table
CREATE TABLE dbo.Questions (
    id              INT IDENTITY(1,1) NOT NULL,
    question_text   NVARCHAR(MAX) NOT NULL,
    correct_answer  BIT NOT NULL,
    weight          INT NOT NULL,
    is_active       BIT NOT NULL,
    
    CONSTRAINT PK_Questions PRIMARY KEY (id),
    CONSTRAINT DF_Questions_Weight DEFAULT 1 FOR weight,
    CONSTRAINT DF_Questions_IsActive DEFAULT 1 FOR is_active
);
GO

-- Create Students Table
CREATE TABLE dbo.Students (
    id              INT IDENTITY(1,1) NOT NULL,
    student_name    NVARCHAR(100) NOT NULL,
    total_score     INT NOT NULL,
    raw_answers     VARCHAR(MAX) NOT NULL, -- Storing binary string '10101...'
    
    CONSTRAINT PK_Students PRIMARY KEY (id),
    CONSTRAINT DF_Students_TotalScore DEFAULT 0 FOR total_score
);
GO
