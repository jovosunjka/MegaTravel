using LogSimulator.Service.Interface;

namespace LogSimulator.State
{
    public class BruteForceAttackState : IState
    {
        public string Description => Helper.Constants.StateDescription.BruteForceAttack;

        public void Simulate(IAppSettings appSettings, ILogService logService)
        {
            var count = int.Parse(appSettings.AttackRequestNum);
            for (int i = 0; i < count; i++)
            {
                var log = logService.GetLog($"Login attempt with username '{appSettings.MaliciousIpAddress}' from ip address '{appSettings.MaliciousIpAddress}'");
                logService.WriteLogToFile(appSettings.LoginLogsFolderPath, log);
            }
        }
    }
}
