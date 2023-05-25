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
    public partial class TransactionsIncomeViewModel : ObservableObject, IQueryAttributable
    {
        private readonly DatabaseService _dbService;

        [ObservableProperty]
        private ObservableCollection<DTO_TransactionsIncomePage> _transactions;

        [ObservableProperty]
        private bool _isRefreshing;

        public ICommand RefreshCommand { get; }

        public TransactionsIncomeViewModel()
        {
            _dbService = ServicePlatformProvider.GetService<DatabaseService>();

            RefreshCommand = new AsyncRelayCommand(RefreshAsync);

            RefreshCommand.Execute(null);
        }

        public async Task RefreshAsync()
        {
            string queryTransactions = $@"
                SELECT TransactionIncome.*, CategoryIncome.Name as CategoryName,  Account.Name as AccountName
                FROM TransactionIncome
                INNER JOIN CategoryIncome ON TransactionIncome.CategoryIncomeId = CategoryIncome.Id
                INNER JOIN Account ON TransactionIncome.AccountId = Account.Id";
            Transactions = new ObservableCollection<DTO_TransactionsIncomePage>(await _dbService.GetItemsWithQuery<DTO_TransactionsIncomePage>(queryTransactions));
            IsRefreshing = false;
        }

        public void ApplyQueryAttributes(IDictionary<string, object> query)
        {
        }
    }
}
