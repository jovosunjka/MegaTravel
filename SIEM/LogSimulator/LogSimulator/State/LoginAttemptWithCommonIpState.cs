using LogSimulator.Model.Enum;
using LogSimulator.Service.Interface;

namespace LogSimulator.State
{
    /// <summary>
    /// 15 ili više neuspešnih pokušaja prijave na različite delove informacionog sistema sa iste IP adrese u roku od 5 dana;
    /// Pokušaj prijave na nalog koji nije bio aktivan 90 ili više dana
    /// Ukoliko sa iste IP adrese registruje 30 ili više neuspešnih pokušaja prijave na sistem u roku od 24h, dodati tu IP adresu u spisak malicioznih IP adresa
    /// </summary>
    public class LoginAttemptWithCommonIpState : IState
    {
        public string Description => Helper.Constants.StateDescription.LoginAttemptWithCommonIp;

        public void Simulate(IAppSettings appSettings, ILogService logService)
        {
            var count = int.Parse(appSettings.UnsuccessfulLoginAttemptNum);
            for (int i = 0; i < count; i++)
            {
                var log = logService.GetLog($"Login attempt with username '{appSettings.Username}' from ip address '{appSettings.IpAddress2}'", LogCategory.LOGIN, LogLevelType.WARN);
                logService.WriteLogToFile(appSettings.LoginLogsFolderPath, log);
            }
        }
    }
}
