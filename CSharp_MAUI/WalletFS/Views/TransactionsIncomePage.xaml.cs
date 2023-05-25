using WalletFS.ViewModels;

namespace WalletFS.Views;

public partial class TransactionsIncomePage : ContentPage
{
	public TransactionsIncomePage()
	{
		InitializeComponent();

        var vm = new TransactionsViewModel
        {
            CategoryType = 1
        };
        BindingContext = vm;
    }

    private void ContentPage_Appearing(object sender, EventArgs e)
    {
        var vm = BindingContext as TransactionsViewModel;
        vm.RefreshCommand.Execute(null);
    }
}