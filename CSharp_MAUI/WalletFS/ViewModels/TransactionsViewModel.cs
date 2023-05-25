using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;
using WalletFS.Models.DTO;
using WalletFS.Services;

namespace WalletFS.ViewModels
{
    public partial class TransactionsViewModel: ObservableObject
    {
        private readonly DatabaseService _dbService;

        [ObservableProperty]
        private ObservableCollection<TransactionWithAccountAndCategoryNamesDTO> _transactions;

        [ObservableProperty]
        private bool _isRefreshing;

        public int CategoryType { get; set; }

        public ICommand RefreshCommand { get; }

        public TransactionsViewModel()
        {
            _dbService = ServicePlatformProvider.GetService<DatabaseService>();

            RefreshCommand = new AsyncRelayCommand(RefreshAsync);

            RefreshCommand.Execute(null);
        }

        public async Task RefreshAsync()
        {
            Transactions = new ObservableCollection<TransactionWithAccountAndCategoryNamesDTO>(await _dbService.GetTypedTransactionsWithAccountAndCategoryNamesAsync(CategoryType));
            IsRefreshing = false;
        }
    }
}
