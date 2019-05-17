using LogSimulator.Service.Interface;

namespace LogSimulator.State
{
    /// <summary>
    /// Omogućiti detekciju suviše učestalih zahteva (više od 50 u roku od 60 sekundi): Zahtevi bilo kog tipa aktiviraju alarm za DoS napad
    /// </summary>
    public class DenialOfServiceAttackState : IState
    {
        public string Description => Helper.Constants.StateDescription.DenialOfServiceAttack;

        public void Simulate(IAppSettings appSettings, ILogService logService)
        {
            var count = int.Parse(appSettings.AttackRequestNum);
            for(int i = 0; i < count; i++)
            {
                var log = logService.GetLog($"User from ip address '{appSettings.MaliciousIpAddress}' accessed GET flights/today");
                logService.WriteLogToFile(appSettings.OtherLogsFolderPath, log);
            }
        }
    }
}
