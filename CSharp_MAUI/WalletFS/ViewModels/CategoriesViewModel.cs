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
    public partial class CategoriesViewModel : ObservableObject
    {
        [ObservableProperty]
        private ObservableCollection<CategoryWithSpentAndBudgetDTO> _categories;

        [ObservableProperty]
        private bool _isRefreshing;

        public int CategoryType { get; set; }

        public ICommand RefreshCommand { get; }
        public ICommand SelectCommand { get; }
        public ICommand NewCommand { get; }

        private DatabaseService _dbService;
        public CategoriesViewModel()
        {
            _dbService = ServicePlatformProvider.GetService<DatabaseService>();

            RefreshCommand = new AsyncRelayCommand(RefreshAsync);
            NewCommand = new AsyncRelayCommand(NewAsync);
            SelectCommand = new AsyncRelayCommand<CategoryWithSpentAndBudgetDTO>(SelectAsync);

            RefreshCommand.Execute(null);
        }
        private async Task RefreshAsync()
        {
            Categories = new ObservableCollection<CategoryWithSpentAndBudgetDTO>(await _dbService.GetTypedCategoriesWithSpentAndBudgetAsync(CategoryType));

            IsRefreshing = false;
        }

        private async Task NewAsync()
        {
            await Shell.Current.GoToAsync($"Category", true, new Dictionary<string, object>() { ["type"] = CategoryType });
        }

        private async Task SelectAsync(CategoryWithSpentAndBudgetDTO category)
        {
            if (category != null)
            {
                await Shell.Current.GoToAsync($"Category", true, new Dictionary<string, object>() { ["category"] = category, ["type"] = CategoryType });
            }
        }
    }
}
