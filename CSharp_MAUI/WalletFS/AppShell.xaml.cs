using WalletFS.Views;

namespace WalletFS;

public partial class AppShell : Shell
{
    public AppShell()
    {
        InitializeComponent();

        Routing.RegisterRoute("HomePage", typeof(HomePage));
        Routing.RegisterRoute("AccountPage", typeof(AccountPage));
        Routing.RegisterRoute("AccountsPage", typeof(AccountsPage));
        Routing.RegisterRoute("ThemesPage", typeof(ThemesPage));

        Routing.RegisterRoute("CategoriesIncomePage", typeof(CategoriesIncomePage));
        Routing.RegisterRoute("CategoriesExpensePage", typeof(CategoriesExpensePage));
        Routing.RegisterRoute("Category", typeof(CategoryPage));

        Routing.RegisterRoute("TransactionsIncomePage", typeof(TransactionsIncomePage));
        Routing.RegisterRoute("TransactionsExpensePage", typeof(TransactionsExpensePage));
        Routing.RegisterRoute("TransactionsTransferPage", typeof(TransactionsPage));
        Routing.RegisterRoute("Transaction", typeof(TransactionPage));
    }
}
