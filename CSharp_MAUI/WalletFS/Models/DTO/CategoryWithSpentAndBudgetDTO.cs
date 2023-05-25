using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WalletFS.Models.DTO
{
    public class CategoryWithSpentAndBudgetDTO
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public decimal Spent { get; set; }
        public decimal Budget { get; set; }
        public int CategoryTypeId { get; set; }
    }
}
