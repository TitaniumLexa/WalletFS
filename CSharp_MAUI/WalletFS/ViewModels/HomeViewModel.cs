using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;
using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using WalletFS.Services;
using WalletFS.Views;

namespace WalletFS.ViewModels
{
    public partial class HomeViewModel : ObservableObject
    {
        [ObservableProperty]
        private decimal _totalAmount = 0;

        private readonly DatabaseService _dbService;

        public ICommand RefreshCommand { get; }
        public ICommand AddTransactionExpenseCommand { get; }
        public ICommand AddTransactionIncomeCommand { get; }

        public HomeViewModel()
        {
            _dbService = ServicePlatformProvider.GetService<DatabaseService>();

            RefreshCommand = new AsyncRelayCommand(RefreshAsync);
            AddTransactionExpenseCommand = new AsyncRelayCommand(AddTransactionExpenseAsync);
            AddTransactionIncomeCommand = new AsyncRelayCommand(AddTransactionIncomeAsync);
        }

        private async Task AddTransactionExpenseAsync()
        {
            await Shell.Current.GoToAsync($"Transaction", true, new Dictionary<string, object>() { ["category"] = 2 });
        }

        private async Task AddTransactionIncomeAsync()
        {
            await Shell.Current.GoToAsync($"Transaction", true, new Dictionary<string, object>() { ["category"] = 1 });
        }

        private async Task RefreshAsync()
        {
            TotalAmount = await _dbService.GetTotalBalance();
        }
    }
}
