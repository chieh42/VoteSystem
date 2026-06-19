
-- 1. 建立資料庫
CREATE DATABASE VoteSystem;
GO

USE VoteSystem;
GO

-- 建立投票項目資料表
CREATE TABLE Vote_Item
(
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL UNIQUE
);
GO

-- 投票紀錄資料表
CREATE TABLE Vote_Record
(
    id INT IDENTITY(1,1) PRIMARY KEY,
    voter NVARCHAR(50) NULL,
    item_id INT NOT NULL,
    vote_time DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_VoteRecord_VoteItem FOREIGN KEY (item_id) REFERENCES Vote_Item(id)
);
GO


--初始資料 (DML)
INSERT INTO Vote_Item
    (name)
VALUES
    (N'電腦');
INSERT INTO Vote_Item
    (name)
VALUES
    (N'滑鼠');
GO

INSERT INTO Vote_Record
    (voter, item_id)
VALUES
    (N'Leo', 1);
INSERT INTO Vote_Record
    (voter, item_id)
VALUES
    (N'Sandy', 1);
INSERT INTO Vote_Record
    (voter, item_id)
VALUES
    (N'Sandy', 2);
INSERT INTO Vote_Record
    (voter, item_id)
VALUES
    (N'Randy', 2);
INSERT INTO Vote_Record
    (voter, item_id)
VALUES
    (N'RSY', 2);
GO

--Stored Procedures
CREATE PROCEDURE sp_vote
    @voter NVARCHAR(50),
    @item_id INT
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO Vote_Record
        (voter, item_id)
    VALUES
        (@voter, @item_id);
END;
GO

-- 投票統計結果
CREATE PROCEDURE sp_get_vote_result
AS
BEGIN
    SET NOCOUNT ON;
    SELECT
        i.id,
        i.name,
        COUNT(r.id) AS vote_count
    FROM Vote_Item i
        LEFT JOIN Vote_Record r ON i.id = r.item_id
    GROUP BY i.id, i.name;
END;
GO

-- 新增投票項目
CREATE PROCEDURE sp_add_item
    @item_name NVARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (SELECT 1
    FROM Vote_Item
    WHERE RTRIM(LTRIM(name)) = RTRIM(LTRIM(@item_name)))
    BEGIN
        RAISERROR (N'錯誤：投票項目「%s」已經存在，請勿重複新增！', 16, 1, @item_name);
        RETURN;
    END

    INSERT INTO Vote_Item
        (name)
    VALUES
        (RTRIM(LTRIM(@item_name)));
END;
GO