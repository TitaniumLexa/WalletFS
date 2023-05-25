using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WalletFS.Models.DTO
{
    public class TransactionWithAccountAndCategoryNamesDTO
    {
        public int Id { get; set; }
        public int AccountId { get; set; }
        public int CategoryId { get; set; }
        public string Comment { get; set; }
        public decimal Amount { get; set; }
        public DateTime Date { get; set; }

        public string AccountName { get; set; }
        public string CategoryName { get; set; }
    }
}
