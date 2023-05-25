using WalletFS.ViewModels;

namespace WalletFS.Views;

public partial class TransactionsExpensePage : ContentPage
{
    public TransactionsExpensePage()
    {
        InitializeComponent();

        var vm = new TransactionsViewModel
        {
            CategoryType = 2
        };
        BindingContext = vm;
    }

    private void ContentPage_Appearing(object sender, EventArgs e)
    {
        var vm = BindingContext as TransactionsViewModel;
        vm.RefreshCommand.Execute(null);
    }
}