using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
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
using WalletFS.Views;

namespace WalletFS.ViewModels
{
    public partial class AccountsViewModel : ObservableObject, IQueryAttributable
    {
        [ObservableProperty]
        private ObservableCollection<AccountWithBalanceDTO> accounts;

        [ObservableProperty]
        private bool _isRefreshing;

        private readonly DatabaseService _dbService;

        public ICommand NewCommand { get; }
        public ICommand SelectCommand { get; }
        public ICommand RefreshCommand { get; }

        public AccountsViewModel()
        {
            _dbService = ServicePlatformProvider.GetService<DatabaseService>();
            NewCommand = new AsyncRelayCommand(NewAsync);
            SelectCommand = new AsyncRelayCommand<AccountWithBalanceDTO>(SelectAsync);
            RefreshCommand = new AsyncRelayCommand(RefreshAsync);

            RefreshCommand.Execute(null);
        }

        void IQueryAttributable.ApplyQueryAttributes(IDictionary<string, object> query)
        {
        }

        private async Task RefreshAsync()
        {
            Accounts = new ObservableCollection<AccountWithBalanceDTO>(await _dbService.GetAllAccountsWithBalanceAsync());

            IsRefreshing = false;
        }

        private async Task NewAsync()
        {
            await Shell.Current.GoToAsync($"{nameof(AccountPage)}", true);
        }

        private async Task SelectAsync(AccountWithBalanceDTO account)
        {
            if (account != null)
                await Shell.Current.GoToAsync($"{nameof(AccountPage)}", true, new Dictionary<string, object>() { ["Account"] = account });
        }
    }
}
