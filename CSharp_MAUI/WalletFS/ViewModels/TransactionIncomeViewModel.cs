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
using WalletFS.Services;
using WalletFS.Views;

namespace WalletFS.ViewModels
{
    public partial class TransactionIncomeViewModel : ObservableObject, IQueryAttributable
    {
        public decimal Amount
        {
            get
            {
                return _transaction == null ? 0 : _transaction.Amount;
            }
            set
            {
                if (_transaction != null)
                {
                    SetProperty(_transaction.Amount, value, _transaction, (tr, a) => { tr.Amount = a; }, nameof(Amount));
                }
            }
        }

        public ICommand SaveCommand { get; }
        public ICommand BackCommand { get; }

        [ObservableProperty]
        private ObservableCollection<Account> _accounts;
        
        [ObservableProperty]
        private ObservableCollection<CategoryIncome> _categories;

        [ObservableProperty]
        private CategoryIncome _selectedCategory;

        [ObservableProperty]
        private Account _selectedAccount;

        private readonly DatabaseService _dbService;

        private TransactionIncome _transaction;

        public TransactionIncomeViewModel()
        {
            _dbService = ServicePlatformProvider.GetService<DatabaseService>();
            Accounts = new ObservableCollection<Account>();
            Categories = new ObservableCollection<CategoryIncome>();
            _transaction = new TransactionIncome();

            SaveCommand = new AsyncRelayCommand(SaveAsync);
            BackCommand = new AsyncRelayCommand(BackAsync);

            InitializeAsync();
        }

        public async Task InitializeAsync()
        {
            //Accounts = new ObservableCollection<Account>(await _dbService.GetAllAccountsAsync());
            Categories = new ObservableCollection<CategoryIncome>(await _dbService.GetAllCategoriesAsync<CategoryIncome>());
        }

        public async Task BackAsync()
        {
            await Shell.Current.GoToAsync($"..", true);
        }

        public async Task SaveAsync()
        {
            if (_transaction != null)
            {
                if (_transaction.Id == 0)
                {
                    
                }

                await _dbService.SaveTransactionAsync<TransactionIncome>(_transaction);
                await Shell.Current.GoToAsync($"..", true);
            }
        }

        partial void OnSelectedAccountChanged(Account value)
        {
            if (value != null)
                _transaction.AccountId = value.Id;
        }

        partial void OnSelectedCategoryChanged(CategoryIncome value)
        {
            if (value != null)
                _transaction.CategoryIncomeId = value.Id;
        }

        public void ApplyQueryAttributes(IDictionary<string, object> query)
        {
            
        }
    }
}
