using WalletFS.ViewModels;

namespace WalletFS.Views;

public partial class TransactionsPage : ContentPage
{
	public TransactionsPage()
	{
		InitializeComponent();
	}

    private void ContentPage_Appearing(object sender, EventArgs e)
    {
        base.OnAppearing();
        transactionsCollection.SelectedItem = null;

        var vm = BindingContext as TransactionsViewModel;
        vm.RefreshCommand.Execute(null);
    }

    protected override void OnNavigatedTo(NavigatedToEventArgs args)
    {
        // HACK to pass CategoryTypeId
        var route = Shell.Current.CurrentState.Location.OriginalString.Split('/').LastOrDefault();

        int type = route switch
        {
            "income" => 1,
            "expense" => 2,
            "transfer" => 3,
            "balance" => 4,
            _ => 0
        };
        var vm = BindingContext as TransactionsViewModel;
        vm.CategoryType = type;
        vm.RefreshCommand.Execute(null);

        base.OnNavigatedTo(args);
    }
}