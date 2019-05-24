using LogSimulator.Model.Enum;
using LogSimulator.Service.Interface;

namespace LogSimulator.State
{
    /// <summary>
    /// Prijavljivanje na sistem od istog korisnika na dva ili više dela informacionog sistema u razmaku manjem od 10 sekundi sa različitih IP adresa
    /// </summary>
    public class LoginWithCommonIpState : IState
    {
        public string Description => Helper.Constants.StateDescription.LoginWithCommonIp;

        public void Simulate(IAppSettings appSettings, ILogService logService)
        {
            var log = logService.GetLog($"Login success with username '{appSettings.Username}' from ip address '{appSettings.IpAddress2}'", LogCategory.LOGIN);
            logService.WriteLogToFile(appSettings.LoginLogsFolderPath, log);
        }
    }
}
