-- 1. 建立資料庫
CREATE DATABASE VoteSystem;
GO

USE VoteSystem;
GO

-- 建立使用者資料表 (User Entity)
CREATE TABLE [users]
(
    [id] INT IDENTITY(1,1) PRIMARY KEY,
    [username] VARCHAR(50) NOT NULL UNIQUE,
    [password] VARCHAR(255) NOT NULL,
    [role] VARCHAR(20) NOT NULL DEFAULT 'user'
);
GO

-- 建立投票項目資料表
CREATE TABLE Vote_Item
(
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL UNIQUE
);
GO

-- 投票紀錄資料表 (VoteRecord Entity)
CREATE TABLE Vote_Record
(
    id INT IDENTITY(1,1) PRIMARY KEY,
    voter_id INT NULL,
    item_id INT NOT NULL,
    vote_time DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_VoteRecord_User FOREIGN KEY (voter_id) REFERENCES [users](id),
    CONSTRAINT FK_VoteRecord_VoteItem FOREIGN KEY (item_id) REFERENCES Vote_Item(id) ON DELETE CASCADE
);
GO

-- 初始資料 (DML)　＋　測資
INSERT INTO [users]
    (username, password, role)
VALUES
    ('admin', '$2a$10$7v1bF24r8Kszw/pE5eS9Ke3Wd78d21hZ.v3J6Zg9b16B4XN/wGyG.', 'admin');

IF NOT EXISTS (SELECT 1
FROM Vote_Item)
BEGIN
    INSERT INTO Vote_Item
        (name)
    VALUES
        (N'電腦');
    INSERT INTO Vote_Item
        (name)
    VALUES
        (N'滑鼠');
END;
GO

-- 投票預存程序 (防止重複投票)
CREATE PROCEDURE sp_vote
    @voter INT,
    @item_id INT
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (SELECT 1
    FROM Vote_Record
    WHERE voter_id = @voter AND item_id = @item_id)
    BEGIN
        RAISERROR (N'錯誤：您已經投過這個項目了，請勿重複投票！', 16, 1);
        RETURN;
    END

    INSERT INTO Vote_Record
        (voter_id, item_id)
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

-- 刪除投票項目(deleteItem)
CREATE PROCEDURE sp_delete_item
    @item_id INT
AS
BEGIN
    SET NOCOUNT ON;

    IF NOT EXISTS (SELECT 1
    FROM Vote_Item
    WHERE id = @item_id)
    BEGIN
        RAISERROR (N'錯誤：找不到指定 ID (%d) 的投票項目，無法刪除！', 16, 1, @item_id);
        RETURN;
    END

    DELETE FROM Vote_Item WHERE id = @item_id;
END;
GO

-- 更新既有投票項目
CREATE OR ALTER PROCEDURE sp_update_item
    @item_id INT,
    @new_name NVARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;

    IF NOT EXISTS (SELECT 1
    FROM Vote_Item
    WHERE id = @item_id)
    BEGIN
        RAISERROR (N'錯誤：找不到指定 ID (%d) 的投票項目！', 16, 1, @item_id);
        RETURN;
    END

    IF EXISTS (SELECT 1
    FROM Vote_Item
    WHERE RTRIM(LTRIM(name)) = RTRIM(LTRIM(@new_name)) AND id <> @item_id)
    BEGIN
        RAISERROR (N'錯誤：名稱「%s」已被其他投票項目使用，請換個名稱！', 16, 1, @new_name);
        RETURN;
    END

    UPDATE Vote_Item
    SET name = RTRIM(LTRIM(@new_name))
    WHERE id = @item_id;
END;
GO