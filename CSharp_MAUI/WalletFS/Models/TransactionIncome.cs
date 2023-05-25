using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WalletFS.Models
{
    public class TransactionIncome : Transaction
    {
        public int CategoryIncomeId { get; set; }
    }
}
