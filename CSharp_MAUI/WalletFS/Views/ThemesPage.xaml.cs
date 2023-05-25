namespace WalletFS.Views;

public partial class ThemesPage : ContentPage
{
    public ThemesPage()
    {
        InitializeComponent();

        systemCheckbox.IsChecked = false;
        lightCheckbox.IsChecked = false;
        darkCheckbox.IsChecked = false;

        AppTheme currentTheme = Application.Current.RequestedTheme;
        switch (currentTheme)
        {
            case AppTheme.Unspecified: systemCheckbox.IsChecked = true; break;
            case AppTheme.Light: lightCheckbox.IsChecked = true; break;
            case AppTheme.Dark: darkCheckbox.IsChecked = true; break;
            default: systemCheckbox.IsChecked = true; break;
        }
    }

    private void systemCheckbox_CheckedChanged(object sender, CheckedChangedEventArgs e)
    {
        if (e.Value == true)
        {
            lightCheckbox.IsChecked = false;
            darkCheckbox.IsChecked = false;

            Application.Current.UserAppTheme = AppTheme.Unspecified;
        }
    }

    private void lightCheckbox_CheckedChanged(object sender, CheckedChangedEventArgs e)
    {
        if (e.Value == true)
        {
            systemCheckbox.IsChecked = false;
            darkCheckbox.IsChecked = false;

            Application.Current.UserAppTheme = AppTheme.Light;
        }
    }

    private void darkCheckbox_CheckedChanged(object sender, CheckedChangedEventArgs e)
    {
        if (e.Value == true)
        {
            systemCheckbox.IsChecked = false;
            lightCheckbox.IsChecked = false;

            Application.Current.UserAppTheme = AppTheme.Dark;
        }
    }
}