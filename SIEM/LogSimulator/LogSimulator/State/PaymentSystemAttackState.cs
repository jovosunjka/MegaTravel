using LogSimulator.Service.Interface;

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
            for (int i = 0; i < count; i++)
            {
                var log = logService.GetLog($"User from ip address '{appSettings.MaliciousIpAddress}' accessed GET flights/today");
                logService.WriteLogToFile(appSettings.LogsFilePath, log);
            }
        }
    }
}
