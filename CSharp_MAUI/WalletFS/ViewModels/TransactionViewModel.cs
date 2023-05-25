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
    public partial class TransactionViewModel : ObservableObject, IQueryAttributable
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

        public string Comment
        {
            get
            {
                return _transaction == null ? "" : _transaction.Comment;
            }
            set
            {
                if (_transaction != null)
                {
                    SetProperty(_transaction.Comment, value, _transaction, (tr, cm) => { tr.Comment = cm; }, nameof(Comment));
                }
            }
        }

        public ICommand SaveCommand { get; }
        public ICommand BackCommand { get; }

        public int CategoryType { get; set; }

        [ObservableProperty]
        private ObservableCollection<AccountDTO> _accounts;

        [ObservableProperty]
        private ObservableCollection<CategoryDTO> _categories;

        [ObservableProperty]
        private CategoryDTO _selectedCategory;

        [ObservableProperty]
        private AccountDTO _selectedAccount;

        private readonly DatabaseService _dbService;

        private TransactionWithAccountAndCategoryNamesDTO _transaction;

        public TransactionViewModel()
        {
            _dbService = ServicePlatformProvider.GetService<DatabaseService>();
            Accounts = new ObservableCollection<AccountDTO>();
            Categories = new ObservableCollection<CategoryDTO>();

            SaveCommand = new AsyncRelayCommand(SaveAsync);
            BackCommand = new AsyncRelayCommand(BackAsync);
        }

        public async Task InitializeAsync()
        {
            Accounts = new ObservableCollection<AccountDTO>(await _dbService.GetAllAccountsAsync());
            Categories = new ObservableCollection<CategoryDTO>(await _dbService.GetTypedCategories(CategoryType));
        }

        public async Task BackAsync()
        {
            await Shell.Current.GoToAsync($"..", true);
        }

        public async Task SaveAsync()
        {
            _transaction.Amount = Math.Abs(_transaction.Amount);
            if (CategoryType == 2)
            {
                _transaction.Amount *= -1;
            }

            await _dbService.SaveTransactionAsync(_transaction);
            await Shell.Current.GoToAsync($"..", true);
        }

        partial void OnSelectedAccountChanged(AccountDTO value)
        {
            if (value != null)
                _transaction.AccountId = value.Id;
        }

        partial void OnSelectedCategoryChanged(CategoryDTO value)
        {
            if (value != null)
                _transaction.CategoryId = value.Id;
        }

        public void ApplyQueryAttributes(IDictionary<string, object> query)
        {
            if (query.TryGetValue("transaction", out var transaction))
            {
                _transaction = transaction as TransactionWithAccountAndCategoryNamesDTO;

                OnPropertyChanged(nameof(Amount));
                OnPropertyChanged(nameof(SelectedAccount));
                OnPropertyChanged(nameof(SelectedCategory));
            }
            else
            {
                _transaction = new TransactionWithAccountAndCategoryNamesDTO();
            }

            if (query.TryGetValue("category", out var category))
            {
                if (category is int categoryInt)
                {
                    CategoryType = categoryInt;
                    InitializeAsync();
                }
            }
        }
    }
}
