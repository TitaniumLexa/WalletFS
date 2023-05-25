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
    partial class CategoriesIncomeViewModel : ObservableObject
    {
        [ObservableProperty]
        private ObservableCollection<CategoryIncome> _categories;

        private readonly DatabaseService _dbService;

        [ObservableProperty]
        private bool _isRefreshing;

        public ICommand RefreshCommand { get; }
        public ICommand SelectCommand { get; }
        public ICommand NewCommand { get; }

        public CategoriesIncomeViewModel()
        {
            _dbService = ServicePlatformProvider.GetService<DatabaseService>();

            RefreshCommand = new AsyncRelayCommand(RefreshAsync);
            NewCommand = new AsyncRelayCommand(NewAsync);
            SelectCommand = new AsyncRelayCommand<CategoryIncome>(SelectAsync);

            RefreshCommand.Execute(null);
        }

        private async Task RefreshAsync()
        {
            Categories = new ObservableCollection<CategoryIncome>(await _dbService.GetAllCategoriesAsync<CategoryIncome>());

            IsRefreshing = false;
        }

        private async Task NewAsync()
        {
            await Shell.Current.GoToAsync($"{nameof(CategoryIncomePage)}", true);
        }

        private async Task SelectAsync(CategoryIncome category)
        {
            if (category != null)
            {
                await Shell.Current.GoToAsync($"{nameof(CategoryIncomePage)}", true, new Dictionary<string, object>() { ["Category"] = category });
            }
        }


    }
}
