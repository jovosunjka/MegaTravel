using LogSimulator.Model.Enum;
using LogSimulator.Service.Interface;
using System;

namespace LogSimulator.State
{
    /// <summary>
    /// Omogućiti detekciju suviše učestalih zahteva (više od 50 u roku od 60 sekundi): Zahtevi koji su povezani sa podsistemom za plaćanja aktivira alarm za payment sistem
    /// </summary>
    public class PaymentSystemAttackState : IState
    {
        public string Description => Helper.Constants.StateDescription.PaymentSystemAttack;

        public void Simulate(IAppSettings appSettings, ILogService logService)
        {
            var count = int.Parse(appSettings.AttackRequestNum);
            var random = new Random();
            for (int i = 0; i < count; i++)
            {
                var log = logService.GetLog($"User from ip address '{appSettings.MaliciousIpAddress}' paid '100$' for flight '{random.Next(1, 10000)}'", LogCategory.PAYMENT_SYSTEM);
                logService.WriteLogToFile(appSettings.OtherLogsFolderPath, log);
            }
        }
    }
}
