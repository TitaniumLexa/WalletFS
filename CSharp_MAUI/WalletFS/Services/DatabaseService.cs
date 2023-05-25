using SQLite;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WalletFS.Models;
using WalletFS.Models.DTO;

namespace WalletFS.Services
{
    public class DatabaseService
    {
        public const string DatabaseFilename = "database.db3";

        public const SQLiteOpenFlags Flags =
            SQLiteOpenFlags.ReadWrite |
            SQLiteOpenFlags.Create |
            SQLiteOpenFlags.SharedCache;

        public static string DatabasePath =>
            Path.Combine(FileSystem.AppDataDirectory, DatabaseFilename);

        private readonly SQLiteAsyncConnection _connection;

        public DatabaseService()
        {
            //File.Delete(DatabasePath);
            _connection = new SQLiteAsyncConnection(DatabasePath, Flags, false);
            Task.Run(Initialize).Wait();
        }

        private async Task Initialize()
        {
            /* // Remove old tables
            await _connection.ExecuteAsync($"PRAGMA foreign_keys = OFF");
            await _connection.ExecuteAsync($"DROP TABLE IF EXISTS {nameof(Account)}");
            await _connection.ExecuteAsync($"DROP TABLE IF EXISTS {nameof(TransactionExpense)}");
            await _connection.ExecuteAsync($"DROP TABLE IF EXISTS {nameof(AccountState)}");
            await _connection.ExecuteAsync($"DROP TABLE IF EXISTS {nameof(CategoryExpense)}");
            await _connection.ExecuteAsync($"DROP TABLE IF EXISTS {nameof(CategoryIncome)}");
            await _connection.ExecuteAsync($"DROP TABLE IF EXISTS {nameof(TransactionExpense)}");
            await _connection.ExecuteAsync($"DROP TABLE IF EXISTS {nameof(TransactionIncome)}");
            await _connection.ExecuteAsync($"DROP TABLE IF EXISTS {nameof(AccountState)}");

            await _connection.ExecuteAsync($"DROP TABLE IF EXISTS Category");
            await _connection.ExecuteAsync($"DROP TABLE IF EXISTS CategoryType");
            await _connection.ExecuteAsync($"PRAGMA foreign_keys = ON");
            */

            // Может Default 0 для CreditLimit
            await _connection.ExecuteAsync(
                @"CREATE TABLE IF NOT EXISTS Account 
                (
                    Id INTEGER PRIMARY KEY AUTOINCREMENT, 
                    Name VARCHAR(255) NOT NULL, 
                    IsCreditCard INTEGER NOT NULL DEFAULT (0), 
                    CreditLimit DECIMAL(11,2)
                );"
            );

            await _connection.ExecuteAsync(
                @"CREATE TABLE IF NOT EXISTS CategoryType 
                (
                    Id INTEGER PRIMARY KEY AUTOINCREMENT, 
                    Name NOT NULL
                );"
            );

            await _connection.ExecuteAsync(
                @"CREATE TABLE IF NOT EXISTS Category 
                (
                    Id INTEGER PRIMARY KEY AUTOINCREMENT, 
                    Name VARCHAR(255) NOT NULL, 
                    CategoryTypeId INTEGER NOT NULL REFERENCES CategoryType (Id)
                );"
            );

            await _connection.ExecuteAsync(
                @"INSERT OR IGNORE INTO CategoryType
                VALUES (1, 'Доход'),
                    (2, 'Расход'),
                    (3, 'Перевод'),
                    (4, 'Баланс');"
            );

            await _connection.ExecuteAsync(
                @"INSERT OR IGNORE INTO Category
                VALUES (1, 'Перевод', 3),
                    (2, 'Баланс', 4);"
            );

            // CategoryId on delete set system category
            await _connection.ExecuteAsync(
                @"CREATE TABLE IF NOT EXISTS 'Transaction' 
                (
                    Id INTEGER PRIMARY KEY AUTOINCREMENT, 
                    AccountId INTEGER REFERENCES Account (Id) ON DELETE SET NULL, 
                    CategoryId INTEGER REFERENCES Category (Id) ON DELETE SET NULL, 
                    Comment VARCHAR(255), 
                    Amount DECIMAL(11,2) NOT NULL, 
                    Date NUMERIC DEFAULT (CURRENT_TIMESTAMP) NOT NULL
                );"
            );

            // Cascade or transfer to amount to system category
            await _connection.ExecuteAsync(
                @"CREATE TABLE IF NOT EXISTS Budget 
                (
                    Id INTEGER PRIMARY KEY AUTOINCREMENT,
                    Amount DECIMAL(11,2) NOT NULL,
                    CategoryId INTEGER REFERENCES Category (Id) ON DELETE CASCADE 
                );"
            );
        }

        public async Task<IEnumerable<T>> GetItemsWithQuery<T>(string query) where T : class, new()
        {
            return await _connection.QueryAsync<T>(query);
        }

        public async Task<decimal> GetTotalBalance()
        {
            var query = @"
                SELECT SUM(Amount) as TotalBalance
                FROM 'Transaction' as acc
                JOIN (SELECT AccountId, MAX(Date) as StartDate
                    FROM 'Transaction'
                    -- Выбираем баланс
                    WHERE CategoryId = (SELECT Id FROM Category WHERE CategoryTypeId = 4 LIMIT 1)
                    GROUP BY AccountId) as accSD 
                ON acc.AccountId = accSD.AccountId
                WHERE Date >= StartDate
            ";
            return await _connection.ExecuteScalarAsync<decimal>(query);
        }

        #region Accounts
        public async Task<int> SaveAccountAsync(AccountWithBalanceDTO item)
        {
            string date = DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss");
            if (item.Id != 0)
            {
                // Updating existing account
                await _connection.RunInTransactionAsync(transaction =>
                {
                    var query = $@"
                        UPDATE Account
                        SET Name = ?, IsCreditCard = ?, CreditLimit = ? 
                        WHERE Id = ?
                    ";
                    transaction.Execute(query, item.Name, item.IsCreditCard, item.CreditLimit);

                    // Stupid way. Better cache retrived by viewmodel entity and return list of changed columns.
                    // Calculation of account balance
                    query = $@"
                        SELECT SUM(Amount) as Balance
                        FROM 'Transaction' as acc
                        JOIN (SELECT AccountId, MAX(Date) as StartDate
                            FROM 'Transaction'
                            -- Выбираем баланс
                            WHERE CategoryId = (SELECT Id FROM Category WHERE CategoryTypeId = 4 LIMIT 1) AND AccountId == ?
                            ) as accSD 
                        ON acc.AccountId = accSD.AccountId
                        WHERE Date >= StartDate
                    ";
                    var sums = transaction.QueryScalars<decimal>(query, item.Id);

                    if (sums.Count != 1)
                    {
                        // Something strange
                        transaction.Rollback();
                    }
                    else
                    {
                        decimal balance = sums[0];
                        if (balance != item.Balance)
                        {
                            query = $@"
                                INSERT INTO 'Transaction'
                                VALUES (NULL, ?, ?, ?, ?, ?);
                            ";
                            // 2 in categoryId change to Enum                    
                            transaction.Execute(query, item.Id, 2, "Корректирование баланса", item.Balance, date);

                        }
                    }

                    transaction.Commit();
                });

                return 2;
            }
            else
            {
                // Creating new account
                await _connection.RunInTransactionAsync(transaction =>
                {
                    var query = $@"
                        INSERT INTO Account
                        VALUES (NULL, ?, ?, ?)
                        RETURNING Id;
                    ";
                    var accountIds = transaction.QueryScalars<int>(query, item.Name, item.IsCreditCard, item.CreditLimit);
                    if (accountIds.Count != 1)
                    {
                        transaction.Rollback();
                        return; // Maybe throw exception and make error modal window?
                    }
                    var accountId = accountIds[0];

                    query = $@"
                        INSERT INTO 'Transaction'
                        VALUES (NULL, ?, ?, ?, ?, ?);
                    ";
                    // 2 in categoryId change to Enum                    
                    transaction.Execute(query, accountId, 2, "Начальный баланс", item.Balance, date);
                    transaction.Commit();
                });

                return 2;
            }
        }

        public async Task<List<AccountWithBalanceDTO>> GetAllAccountsWithBalanceAsync()
        {
            var query = @"
                SELECT ac.Id, ac.Name, ac.IsCreditCard, ac.CreditLimit, ifnull(acc.Balance, 0) as Balance
                FROM Account ac 
                LEFT JOIN (SELECT acc.AccountId, SUM(Amount) as Balance
                FROM 'Transaction' as acc
                JOIN (SELECT AccountId, MAX(Date) as StartDate
                    FROM 'Transaction'
                    -- Выбираем баланс
                    WHERE CategoryId = (SELECT Id FROM Category WHERE CategoryTypeId = 4 LIMIT 1)
                    GROUP BY AccountId) as accSD 
                ON acc.AccountId = accSD.AccountId
                WHERE Date >= StartDate
                GROUP BY acc.AccountId) as acc ON acc.AccountId = ac.Id;
            ";
            return await _connection.QueryAsync<AccountWithBalanceDTO>(query);
        }

        public async Task<List<AccountDTO>> GetAllAccountsAsync()
        {
            var query = @$"
                SELECT *
                FROM Account
            ";
            return await _connection.QueryAsync<AccountDTO>(query);
        }
        #endregion

        #region Categories
        public async Task<List<CategoryWithSpentAndBudgetDTO>> GetTypedCategoriesWithSpentAndBudgetAsync(int categoryType)
        {
            var query = @"
                SELECT ct.Id, ct.Name, SUM(tr.Amount) as Spent, bg.Amount as Bugdet
                FROM 'Category' ct
                LEFT JOIN 'Transaction' tr ON ct.Id = tr.CategoryId
                LEFT JOIN 'Budget' bg ON ct.Id = bg.CategoryId
                WHERE ct.CategoryTypeId = ?
                GROUP BY ct.Id;
            ";
            return await _connection.QueryAsync<CategoryWithSpentAndBudgetDTO>(query, categoryType);
        }

        public async Task<int> SaveCategoryAsync(CategoryWithSpentAndBudgetDTO item)
        {
            if (item.Id != 0)
            {
                // Replace changing CategoryTypeId
                var query = @"
                    UPDATE Category
                    SET Name = ?, CategoryTypeId = ? 
                    WHERE Id = ?;
                ";
                return await _connection.ExecuteAsync(query, item.Name, item.CategoryTypeId, item.Id);
            }
            else
            {
                var query = @"
                    INSERT INTO Category
                    VALUES (NULL, ?, ?);
                ";

                return await _connection.ExecuteAsync(query, item.Name, item.CategoryTypeId);
            }


        }

        public async Task<int> SaveCategoryExpenseAsync(CategoryExpense item)
        {
            if (item.Id != 0)
            {
                return await _connection.UpdateAsync(item);
            }
            else
            {
                return await _connection.InsertAsync(item);
            }
        }

        public async Task<int> SaveCategoryIncomeAsync(CategoryIncome item)
        {
            if (item.Id != 0)
            {
                return await _connection.UpdateAsync(item);
            }
            else
            {
                return await _connection.InsertAsync(item);
            }
        }

        public async Task<int> SaveCategoryAsync<T>(T item) where T : Category, new()
        {
            if (item.Id != 0)
            {
                return await _connection.UpdateAsync(item);
            }
            else
            {
                return await _connection.InsertAsync(item);
            }
        }

        public async Task<List<CategoryExpense>> GetAllCategoriesExpenseAsync()
        {
            return await _connection.Table<CategoryExpense>().ToListAsync();
        }

        public async Task<List<CategoryIncome>> GetAllCategoriesIncomeAsync()
        {
            return await _connection.Table<CategoryIncome>().ToListAsync();
        }

        public async Task<List<T>> GetAllCategoriesAsync<T>() where T : Category, new()
        {
            return await _connection.Table<T>().ToListAsync();
        }
        #endregion

        public async Task<List<CategoryDTO>> GetTypedCategories(int categoryType)
        {
            var query = @"
                SELECT *
                FROM Category
                WHERE CategoryTypeId == ?
            ";
            return await _connection.QueryAsync<CategoryDTO>(query, categoryType);
        }

        #region Transactions

        public async Task<List<TransactionWithAccountAndCategoryNamesDTO>> GetTypedTransactionsWithAccountAndCategoryNamesAsync(int categoryType)
        {
            var query = @"
                SELECT t.*, ac.Name as AccountName, ct.Name as CategoryName
                FROM 'Transaction' t  
                JOIN Account ac ON t.AccountId = ac.Id 
                JOIN Category ct ON t.CategoryId = ct.Id
                WHERE ct.CategoryTypeId == ?
                ORDER BY Date DESC
            ";
            return await _connection.QueryAsync<TransactionWithAccountAndCategoryNamesDTO>(query, categoryType);
        }

        public async Task<int> SaveTransactionAsync(TransactionWithAccountAndCategoryNamesDTO item)
        {
            string date = DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss");
            var query = @"
                INSERT INTO 'Transaction'
                VALUES (NULL, ?, ?, ?, ?, ?)
            ";
            return await _connection.ExecuteAsync(query, item.AccountId, item.CategoryId, item.Comment, item.Amount, date);
        }

        public async Task<List<T>> GetAllTransactions<T>() where T : Transaction, new()
        {
            return await _connection.Table<T>().ToListAsync();
        }

        public async Task<int> SaveTransactionAsync<T>(T item) where T : Transaction, new()
        {
            int id;
            if (item.Id != 0)
            {
                id = await _connection.UpdateAsync(item);
            }
            else
            {
                id = await _connection.InsertAsync(item);
            }

            // ToDo: Rewrite this shit using normal sql queries
            bool isExpense = item is TransactionExpense;
            var accountStates = await _connection.QueryAsync<AccountState>(
                $@"
                    SELECT * 
                    FROM AccountState
                    WHERE date(Date) = '{DateTime.Today:yyyy-MM-dd}' AND AccountId = {item.AccountId}
                ");

            if (accountStates.Count == 0)
            {
                var accountStatesYesterday = await _connection.QueryAsync<AccountState>(
                $@"
                    SELECT * 
                    FROM AccountState
                    WHERE date(Date) = '{DateTime.Today.AddDays(-1):yyyy-MM-dd}' AND AccountId = {item.AccountId}
                ");

                var amount = isExpense ? -item.Amount : item.Amount;

                if (accountStatesYesterday.Count != 0)
                {
                    amount += accountStatesYesterday[0].Amount;
                }

                var date = DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss");
                await _connection.ExecuteAsync(
                    $@"
                        INSERT INTO {nameof(AccountState)}
                        VALUES (NULL, ?, '{date}', {item.AccountId})
                    ", amount);
            }
            else
            {
                var amount = isExpense ? accountStates[0].Amount - item.Amount : accountStates[0].Amount + item.Amount;
                var date = DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss");
                await _connection.ExecuteAsync(
                    $@"
                        UPDATE {nameof(AccountState)}
                        SET Amount = ?, Date = ? 
                        WHERE Id = ?
                    ", amount, DateTime.Now, accountStates[0].Id);
            }

            return id;
        }
        #endregion
    }
}
