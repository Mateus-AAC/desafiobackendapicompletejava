CREATE PROCEDURE InsertRandomCPFs
AS
BEGIN
    DECLARE @counter INT = 0;
    DECLARE @randomNumber INT;
    DECLARE @cpf NVARCHAR(14);
    
    WHILE @counter < 50
    BEGIN
        SET @randomNumber = ROUND(RAND() * 899999999 + 100000000, 0);
        SET @cpf = FORMAT(@randomNumber, '00000000000');
        
        INSERT INTO cpfs (cpf, createdAt) VALUES (@cpf, GETDATE());
        
        SET @counter = @counter + 1;
    END
END;
GO

EXEC InsertRandomCPFs;
