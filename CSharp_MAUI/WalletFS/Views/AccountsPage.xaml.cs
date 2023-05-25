using WalletFS.ViewModels;

namespace WalletFS.Views;

public partial class AccountsPage : ContentPage
{
	public AccountsPage()
	{
		InitializeComponent();
	}

    // Too many appearing breaks selected item reset
    private void ContentPage_Appearing(object sender, EventArgs e)
    {
        base.OnAppearing();
        accountsCollection.SelectedItem = null;

        var vm = BindingContext as AccountsViewModel;
        vm.RefreshCommand.Execute(null);
    }
}