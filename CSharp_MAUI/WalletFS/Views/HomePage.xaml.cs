using WalletFS.ViewModels;

namespace WalletFS.Views;

public partial class HomePage : ContentPage
{
	public HomePage()
	{
		InitializeComponent();
	}

    private void ContentPage_Appearing(object sender, EventArgs e)
    {
		var vm = BindingContext as HomeViewModel;
		vm.RefreshCommand.Execute(null);
    }
}