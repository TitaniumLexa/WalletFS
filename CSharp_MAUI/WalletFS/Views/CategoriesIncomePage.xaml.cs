using WalletFS.ViewModels;

namespace WalletFS.Views;

public partial class CategoriesIncomePage : ContentPage
{
	public CategoriesIncomePage()
	{
		InitializeComponent();

        var vm = new CategoriesViewModel
        {
            CategoryType = 1
        };
        BindingContext = vm;
    }

    private void ContentPage_Appearing(object sender, EventArgs e)
    {
        base.OnAppearing();
        categoriesCollection.SelectedItem = null;

        var vm = BindingContext as CategoriesViewModel;
        vm.RefreshCommand.Execute(null);
    }
}