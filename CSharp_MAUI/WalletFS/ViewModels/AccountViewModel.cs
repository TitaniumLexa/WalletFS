using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using System;
using System.Collections.Generic;
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
    public partial class AccountViewModel : ObservableObject, IQueryAttributable
    {        
        public string Name
        {
            get
            {
                return _account == null ? "" : _account.Name;
            }
            set
            {
                if (_account != null)
                {
                    SetProperty(_account.Name, value, _account, (acc, n) => { acc.Name = n; }, nameof(Name));
                }                
            }
        }
                
        public decimal Balance
        {
            get
            {
                return _account == null ? 0 : _account.Balance;
            }
            set
            {
                if (_account != null)
                {
                    SetProperty(_account.Balance, value, _account, (acc, b) => { acc.Balance = b; }, nameof(Balance));
                }
            }
        }

        public bool IsCreditCard
        {
            get 
            {
                return _account == null ? false : _account.IsCreditCard;
            }
            set
            {
                if (_account != null)
                {
                    SetProperty(_account.IsCreditCard, value, _account, (acc, i) => { acc.IsCreditCard = i; }, nameof(IsCreditCard));
                }
            }
        }

        public decimal CreditLimit
        {
            get 
            {
                return _account == null ? 0 : _account.CreditLimit;
            }
            set
            {
                if (_account != null)
                {
                    SetProperty(_account.CreditLimit, value, _account, (acc, c) => { acc.CreditLimit = c; }, nameof(CreditLimit));
                }
            }
        }

        private AccountWithBalanceDTO _account;

        private readonly DatabaseService _dbService;

        public ICommand SaveCommand { get; }
        public ICommand BackCommand { get; }
        public AccountViewModel()
        {
            _dbService = ServicePlatformProvider.GetService<DatabaseService>();

            SaveCommand = new AsyncRelayCommand(Save);
            BackCommand = new AsyncRelayCommand(Back);
        }

        private async Task Back()
        {
            //await Shell.Current.GoToAsync($"//{nameof(AccountsPage)}", true);
            await Shell.Current.GoToAsync($"..", true);
        }

        private async Task Save()
        {
            await _dbService.SaveAccountAsync(_account);

            //await Shell.Current.GoToAsync($"//{nameof(AccountsPage)}", true);
            await Shell.Current.GoToAsync($"..", true);

        }

        void IQueryAttributable.ApplyQueryAttributes(IDictionary<string, object> query)
        {
            if (query.TryGetValue("Account", out object account))
            {
                _account = account as AccountWithBalanceDTO;

                OnPropertyChanged(nameof(Name));
                OnPropertyChanged(nameof(Balance));
                OnPropertyChanged(nameof(IsCreditCard));
                OnPropertyChanged(nameof(CreditLimit));
            }
            else
            {
                _account = new AccountWithBalanceDTO();
            }
        }
    }
}
