using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;
using WalletFS.Models.DTO;
using WalletFS.Services;

namespace WalletFS.ViewModels
{
    public partial class CategoryViewModel : ObservableObject, IQueryAttributable
    {
        public string Name 
        {
            get
            {
                return _category is null ? "" : _category.Name;
            }
            set
            {
                if (_category is not null)
                {
                    SetProperty(_category.Name, value, _category, (c, n) => { c.Name = n; }, nameof(Name));
                }
            }
        }

        public int CategoryType { 
            get
            {
                return _category is null ? 1 : _category.CategoryTypeId;
            }
            set 
            {
                if (_category is not null)
                {
                    SetProperty(_category.CategoryTypeId, value, _category, (c, ct) => { c.CategoryTypeId = ct; }, nameof(CategoryType));
                }
            }
        }

        public ICommand SaveCommand { get; }
        public ICommand BackCommand { get; }

        private CategoryWithSpentAndBudgetDTO _category;
        private DatabaseService _dbService;
        public CategoryViewModel() 
        {
            _dbService = ServicePlatformProvider.GetService<DatabaseService>();

            SaveCommand = new AsyncRelayCommand(SaveAsync);
            BackCommand = new AsyncRelayCommand(BackAsync);
        }

        private async Task SaveAsync()
        {
            await _dbService.SaveCategoryAsync(_category);

            await Shell.Current.GoToAsync($"..", true);
        }

        private async Task BackAsync()
        {
            await Shell.Current.GoToAsync($"..", true);
        }

        public void ApplyQueryAttributes(IDictionary<string, object> query)
        {
            if (query.TryGetValue("category", out var categoryObject))
            {
                _category = categoryObject as CategoryWithSpentAndBudgetDTO;

                OnPropertyChanged(nameof(Name));
            }
            else 
            {
                _category = new CategoryWithSpentAndBudgetDTO();
            }

            if (query.TryGetValue("type", out var type))
            {
                if (type is int typeInt)
                {
                    _category.CategoryTypeId = typeInt;

                    OnPropertyChanged(nameof(CategoryType));
                }
            }
        }
    }
}
