using LogSimulator.Model.Enum;
using LogSimulator.Service.Interface;

namespace LogSimulator.State
{
    /// <summary>
    /// Prijava ili pokušaj prijave sa IP adrese koje se nalazi na spisku malicioznih IP adresa
    /// </summary>
    public class LoginWithMaliciousIpState : IState
    {
        public string Description => Helper.Constants.StateDescription.LoginWithMaliciousIp;

        public void Simulate(IAppSettings appSettings, ILogService logService)
        {
            var log = logService.GetLog($"Login attempt with username '{appSettings.MaliciousUsername}' from ip address '{appSettings.MaliciousIpAddress}'", LogCategory.LOGIN);
            logService.WriteLogToFile(appSettings.LoginLogsFolderPath, log);
        }
    }
}
