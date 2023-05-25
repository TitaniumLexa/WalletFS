using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WalletFS.Models.DTO
{
    public class DTO_TransactionsIncomePage : TransactionIncome
    {
        public string AccountName { get; set; }

        public string CategoryName { get; set; }
    }
}
