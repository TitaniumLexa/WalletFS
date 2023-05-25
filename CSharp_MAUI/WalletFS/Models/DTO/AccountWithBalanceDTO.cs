using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WalletFS.Models.DTO
{
    public class AccountWithBalanceDTO
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public bool IsCreditCard { get; set; }
        public decimal CreditLimit { get; set; }
        public decimal Balance { get; set; }
    }
}
