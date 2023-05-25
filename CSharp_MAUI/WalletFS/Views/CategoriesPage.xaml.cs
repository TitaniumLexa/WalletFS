using WalletFS.ViewModels;

namespace WalletFS.Views;

public partial class CategoriesPage : ContentPage
{
	public CategoriesPage()
	{
		InitializeComponent();
    }

    private void ContentPage_Appearing(object sender, EventArgs e)
    {
        base.OnAppearing();
        categoriesCollection.SelectedItem = null;

        var vm = BindingContext as CategoriesViewModel;
        vm.RefreshCommand.Execute(null);
    }
}