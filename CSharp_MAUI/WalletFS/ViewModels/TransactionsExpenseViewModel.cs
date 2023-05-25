using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using Microsoft.Maui.Controls;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;
using WalletFS.Models;
using WalletFS.Models.DTO;
using WalletFS.Services;

namespace WalletFS.ViewModels
{
    public partial class TransactionsExpenseViewModel : ObservableObject, IQueryAttributable
    {
        private readonly DatabaseService _dbService;

        [ObservableProperty]
        private ObservableCollection<DTO_TransactionsExpensePage> _transactions;

        [ObservableProperty]
        private bool _isRefreshing;

        public ICommand RefreshCommand { get; }

        public TransactionsExpenseViewModel()
        {
            _dbService = ServicePlatformProvider.GetService<DatabaseService>();

            RefreshCommand = new AsyncRelayCommand(RefreshAsync);

            RefreshCommand.Execute(null);
        }

        public async Task RefreshAsync()
        {
            string queryTransactions = $@"
                SELECT TransactionExpense.*, CategoryExpense.Name as CategoryName,  Account.Name as AccountName
                FROM TransactionExpense
                INNER JOIN CategoryExpense ON TransactionExpense.CategoryExpenseId = CategoryExpense.Id
                INNER JOIN Account ON TransactionExpense.AccountId = Account.Id";
            Transactions = new ObservableCollection<DTO_TransactionsExpensePage>(await _dbService.GetItemsWithQuery<DTO_TransactionsExpensePage>(queryTransactions));
            IsRefreshing = false;
        }

        public void ApplyQueryAttributes(IDictionary<string, object> query)
        {
        }
    }
}
