using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;
using WalletFS.Models;
using WalletFS.Services;
using WalletFS.Views;

namespace WalletFS.ViewModels
{
    partial class CategoryExpenseViewModel: ObservableObject, IQueryAttributable
    {
        private readonly DatabaseService _dbService;

        public ICommand SaveCommand { get; }
        public ICommand BackCommand { get; }

        public string Name
        {
            get
            {
                return _category == null ? "CategoryName" : _category.Name;
            }
            set
            {
                if (_category != null)
                {
                    SetProperty(_category.Name, value, _category, (c, n) => { c.Name = n; }, nameof(Name));
                }
            }
        }

        private CategoryExpense _category;

        public CategoryExpenseViewModel()
        {
            _dbService = ServicePlatformProvider.GetService<DatabaseService>();

            SaveCommand = new AsyncRelayCommand(SaveAsync);
            BackCommand = new AsyncRelayCommand(BackAsync);
        }

        private async Task SaveAsync()
        {
            await _dbService.SaveCategoryAsync<CategoryExpense>(_category);
            await Shell.Current.GoToAsync($"//{nameof(CategoriesExpensePage)}", true);
        }

        private async Task BackAsync()
        {
            await Shell.Current.GoToAsync($"//{nameof(CategoriesExpensePage)}", true);
        }

        public void ApplyQueryAttributes(IDictionary<string, object> query)
        {
            if (query.TryGetValue("Category", out object category))
            {
                _category = category as CategoryExpense;

                OnPropertyChanged(nameof(Name));
            }
            else
            {
                _category = new CategoryExpense();
            }
        }
    }
}
