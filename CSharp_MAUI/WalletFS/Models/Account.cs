using SQLite;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WalletFS.Models
{
    public class Account
    {
        [PrimaryKey, AutoIncrement]
        public int Id { get; set; }
        public string Name { get; set; }

        /// <summary>
        /// Foreign Key to <see cref="AccountState"/>
        /// </summary>
        public int AccountStateId { get; set; }
    }
}
