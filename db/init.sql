-- 1. 建立資料庫
CREATE DATABASE VoteSystem;
GO
USE VoteSystem;
GO

-- 2. DDL：建立投票項目資料表
CREATE TABLE Vote_Item
(
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL
);

-- 3. DDL：建立投票紀錄資料表
CREATE TABLE Vote_Record
(
    id INT PRIMARY KEY IDENTITY(1,1),
    voter NVARCHAR(50),
    item_id INT,
    FOREIGN KEY (item_id) REFERENCES Vote_Item(id)
);
GO

-- 4. DML：插入初始測試資料
INSERT INTO Vote_Item
    (name)
VALUES
    (N'電腦'),
    (N'滑鼠');

INSERT INTO Vote_Record
    (voter, item_id)
VALUES
    (N'Leo', 1),
    (N'Sandy', 1),
    (N'Sandy', 2),
    (N'Randy', 2),
    (N'RSY', 2);
GO

-- 5. SP：查詢投票與票數
GO
CREATE PROCEDURE sp_get_vote_result
AS
BEGIN
    SELECT
        v.id,
        v.name,
        COUNT(r.id) AS vote_count
    FROM Vote_Item v
        LEFT JOIN Vote_Record r ON v.id = r.item_id
    GROUP BY v.id, v.name
END
GO

-- 6. SP：執行投票功能
GO
CREATE PROCEDURE sp_vote
    @voter NVARCHAR(50),
    @item_id INT
AS
BEGIN
    BEGIN TRANSACTION

    INSERT INTO Vote_Record
        (voter, item_id)
    VALUES
        (@voter, @item_id)

    COMMIT
END
GO

-- 7. 測試執行
EXEC sp_get_vote_result;
GO